package nl.knokko.customrecipes.furnace;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;
import nl.knokko.customrecipes.crafting.CustomCraftingRecipes;
import nl.knokko.customrecipes.crafting.CustomShapedRecipe;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestShapedRecipes {

    @Test
    public void testBlockIngredients() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ShapedRecipe vanillaTorchRecipe = new ShapedRecipe(
                new NamespacedKey("minecraft", "torch"),
                new ItemStack(Material.TORCH, 4)
        );
        ShapedRecipe blockedTorchRecipe = new ShapedRecipe(
                new NamespacedKey("block", "torch"),
                new ItemStack(Material.TORCH, 4)
        );

        ItemStack customStick = new ItemStack(Material.STICK);
        customStick.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        craftingRecipes.blockIngredients(new IngredientBlocker(
                namespace -> namespace == null || !namespace.equals("minecraft"),
                item -> item != null && item.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) > 2
        ));

        CustomShapedRecipe customRecipe = new CustomShapedRecipe(new ItemStack(Material.REDSTONE_TORCH), "c", "s");
        customRecipe.ingredientMap.put('c', new CustomIngredient(Material.COAL));
        customRecipe.ingredientMap.put('s', new CustomIngredient(
                Material.STICK, candidate -> candidate.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) > 2
        ));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> customKeys = new HashSet<>();
        craftingRecipes.register(plugin, customKeys);
        assertEquals(1, customKeys.size());

        ShapedRecipe ownTorchRecipe = new ShapedRecipe(customKeys.iterator().next(), customRecipe.result);

        ItemStack[] forbiddenMatrix = {
                null, new ItemStack(Material.COAL), null,
                null, customStick, null,
                null, null, null
        };
        ItemStack[] okMatrix = Arrays.copyOf(forbiddenMatrix, 9);
        okMatrix[4] = new ItemStack(Material.STICK);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        // Block this recipe because both the namespace and an ingredient are blocked
        checkResult(inventory, view, forbiddenMatrix, blockedTorchRecipe, null, null);

        // Don't block this recipe because the namespace is not blocked
        checkResult(inventory, view, forbiddenMatrix, vanillaTorchRecipe, vanillaTorchRecipe.getResult(), null);

        // Never block own recipes
        checkResult(inventory, view, forbiddenMatrix, ownTorchRecipe, ownTorchRecipe.getResult(), null);

        // Don't block this recipe because the ingredients are not blocked
        checkResult(inventory, view, okMatrix, blockedTorchRecipe, blockedTorchRecipe.getResult(), null);
    }

    private void checkResult(
            CraftingInventoryMock inventory, InventoryView view,
            ItemStack[] matrix, Recipe recipe, ItemStack expectedResult, ItemStack[] expectedFinalMatrix
    ) {
        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(recipe.getResult());
        inventory.setRecipe(recipe);
        {
            PrepareItemCraftEvent prepareEvent = new PrepareItemCraftEvent(inventory, view, false);
            assertTrue(prepareEvent.callEvent());
            assertEquals(expectedResult, inventory.getResult());

            inventory.setResult(recipe.getResult());
            CraftItemEvent craftEvent = createCraftEvent(inventory, view);
            assertEquals(expectedResult != null, craftEvent.callEvent());
            assertEquals(expectedResult, inventory.getResult());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            if (expectedFinalMatrix != null) {
                assertArrayEquals(expectedFinalMatrix, inventory.getMatrix());
            }
            if (expectedResult == null && expectedFinalMatrix == null) {
                assertArrayEquals(matrix, inventory.getMatrix());
            }
        }
    }

    private CraftItemEvent createCraftEvent(CraftingInventoryMock inventory, InventoryView view) {
        return createCraftEvent(inventory, view, InventoryAction.PICKUP_ALL);
    }

    private CraftItemEvent createCraftEvent(CraftingInventoryMock inventory, InventoryView view, InventoryAction action) {
        return new CraftItemEvent(
                Objects.requireNonNull(inventory.getRecipe()), view, InventoryType.SlotType.RESULT,
                0, ClickType.LEFT, action
        );
    }

    @Test
    public void testOverlappingWeakRecipes() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ItemStack smiteStick = new ItemStack(Material.STICK);
        smiteStick.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        ItemStack sharpStick = new ItemStack(Material.STICK);
        sharpStick.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

        ItemStack smiteSword = new ItemStack(Material.IRON_SWORD);
        smiteSword.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        ItemStack sharpSword = new ItemStack(Material.IRON_SWORD);
        sharpSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();

        CustomShapedRecipe smiteRecipe = new CustomShapedRecipe(smiteSword, "i", "s");
        smiteRecipe.ingredientMap.put('i', new CustomIngredient(Material.IRON_INGOT));
        smiteRecipe.ingredientMap.put('s', new CustomIngredient(
                Material.STICK, stick -> stick.containsEnchantment(Enchantment.DAMAGE_UNDEAD)
        ));
        craftingRecipes.add(smiteRecipe);

        CustomShapedRecipe sharpRecipe = new CustomShapedRecipe(sharpSword, "x", "s");
        sharpRecipe.ingredientMap.put('x', new CustomIngredient(Material.IRON_INGOT));
        sharpRecipe.ingredientMap.put('s', new CustomIngredient(
                Material.STICK, stick -> stick.containsEnchantment(Enchantment.DAMAGE_ALL)
        ));
        craftingRecipes.add(sharpRecipe);

        Set<NamespacedKey> customKeys = new HashSet<>();
        craftingRecipes.register(plugin, customKeys);
        assertEquals(1, customKeys.size());

        ShapedRecipe mergedRecipe = new ShapedRecipe(customKeys.iterator().next(), smiteRecipe.result);

        ItemStack[] blockedMatrix = {
                null, new ItemStack(Material.IRON_INGOT), null,
                null, new ItemStack(Material.STICK), null,
                null, null, null
        };

        ItemStack[] smiteMatrix = Arrays.copyOf(blockedMatrix, 9);
        smiteMatrix[4] = new ItemStack(Material.STICK);
        smiteMatrix[4].addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        ItemStack[] sharpMatrix = Arrays.copyOf(blockedMatrix, 9);
        sharpMatrix[4] = new ItemStack(Material.STICK);
        sharpMatrix[4].addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        checkResult(inventory, view, blockedMatrix, mergedRecipe, null, null);
        checkResult(inventory, view, smiteMatrix, mergedRecipe, smiteSword, null);
        checkResult(inventory, view, sharpMatrix, mergedRecipe, sharpSword, null);
    }

    @Test
    public void testAmountAndRemainingItem() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapedRecipe customRecipe = new CustomShapedRecipe(new ItemStack(Material.BLAZE_ROD), "abc");
        customRecipe.ingredientMap.put('a', new CustomIngredient(
                Material.BLAZE_POWDER, blazePowder -> true, 1, new ItemStack(Material.GLOWSTONE_DUST)
        ));
        customRecipe.ingredientMap.put('b', new CustomIngredient(
                Material.STICK, stick -> true, 2, null
        ));
        customRecipe.ingredientMap.put('c', new CustomIngredient(
                Material.REDSTONE_TORCH, torch -> true, 3, new ItemStack(Material.TORCH, 3)
        ));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), customRecipe.result);

        ItemStack[] matrix = {
                null, null, null,
                null, null, null,
                new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.STICK), new ItemStack(Material.REDSTONE_TORCH)
        };

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        // All ingredients have a stacksize of 1, which is not enough
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[7].setAmount(2);
        matrix[8].setAmount(2);

        // The third ingredient needs a stacksize of 3, but only has 2
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[6].setAmount(2);
        matrix[8].setAmount(3);

        // The stacksize of the first ingredient is too large (so no space for the remaining item)
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[6].setAmount(1);
        matrix[8].setAmount(4);

        // The stacksize of the third ingredient is too large
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        // Perfect fit
        ItemStack[] remainingMatrix = {
                null, null, null,
                null, null, null,
                new ItemStack(Material.GLOWSTONE_DUST), null, new ItemStack(Material.TORCH, 3)
        };
        matrix[8].setAmount(3);
        checkResult(inventory, view, matrix, bukkitRecipe, customRecipe.result, remainingMatrix);

        // Stacksize is larger than needed for the second slot, which is fine since it has no remaining item
        matrix[7].setAmount(10);
        remainingMatrix[7] = new ItemStack(Material.STICK, 8);
        checkResult(inventory, view, matrix, bukkitRecipe, customRecipe.result, remainingMatrix);

        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(customRecipe.result);
        inventory.setRecipe(bukkitRecipe);
        {
            CraftItemEvent craftEvent = createCraftEvent(inventory, view, InventoryAction.MOVE_TO_OTHER_INVENTORY);
            assertTrue(craftEvent.callEvent());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            assertArrayEquals(inventory.getMatrix(), remainingMatrix);
        }
    }

    @Test
    public void testRemainingItemShiftClick() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapedRecipe customRecipe = new CustomShapedRecipe(new ItemStack(Material.GOLD_INGOT), "c");
        customRecipe.ingredientMap.put('c', new CustomIngredient(
                Material.REDSTONE, redStone -> true, 5, new ItemStack(Material.GLOWSTONE_DUST)
        ));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), customRecipe.result);

        ItemStack[] matrix = {
                null, null, null,
                null, null, new ItemStack(Material.REDSTONE, 5),
                null, null, null
        };
        ItemStack[] remainingMatrix = {
                null, null, null,
                null, null, new ItemStack(Material.GLOWSTONE_DUST),
                null, null, null
        };

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        // All ingredients have a stacksize of 1, which is not enough
        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(customRecipe.result);
        inventory.setRecipe(bukkitRecipe);
        {
            CraftItemEvent craftEvent = createCraftEvent(inventory, view, InventoryAction.MOVE_TO_OTHER_INVENTORY);
            assertTrue(craftEvent.callEvent());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            assertArrayEquals(inventory.getMatrix(), remainingMatrix);
        }
    }

    // TODO Check shift-click

    // TODO Check partially-conflicting shapes

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
