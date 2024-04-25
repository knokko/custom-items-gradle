package nl.knokko.customrecipes.crafting;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import nl.knokko.customrecipes.ingredient.IngredientBlocker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static nl.knokko.customrecipes.crafting.TestShapedRecipes.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestShapelessRecipes {

    @Test
    public void testBlockIngredients() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ShapelessRecipe vanillaWoodRecipe = new ShapelessRecipe(
                new NamespacedKey("minecraft", "wood"),
                new ItemStack(Material.OAK_PLANKS, 4)
        );
        ShapelessRecipe blockedWoodRecipe = new ShapelessRecipe(
                new NamespacedKey("block", "wood"),
                new ItemStack(Material.OAK_PLANKS, 4)
        );

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        craftingRecipes.blockIngredients(new IngredientBlocker(
                namespace -> namespace == null || !namespace.equals("minecraft"),
                item -> item != null && item.getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0
        ));

        CustomShapelessRecipe customRecipe = new CustomShapelessRecipe(new ItemStack(Material.OAK_PLANKS, 4), new CustomIngredient(
                Material.OAK_LOG, log -> log.getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0
        ));
        craftingRecipes.add(customRecipe);

        Set<NamespacedKey> customKeys = new HashSet<>();
        craftingRecipes.register(plugin, customKeys);
        assertEquals(1, customKeys.size());

        ShapelessRecipe ownWoodRecipe = new ShapelessRecipe(customKeys.iterator().next(), customRecipe.result.apply(null));

        ItemStack[] simpleMatrix = {
                null, new ItemStack(Material.OAK_LOG), null,
                null, null, null,
                null, null, null
        };
        ItemStack[] blockedMatrix = Arrays.copyOf(simpleMatrix, 9);
        blockedMatrix[1] = blockedMatrix[1].clone();
        blockedMatrix[1].addUnsafeEnchantment(Enchantment.ARROW_FIRE, 4);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        checkResult(inventory, view, blockedMatrix, blockedWoodRecipe, null, null);
        checkResult(inventory, view, blockedMatrix, vanillaWoodRecipe, vanillaWoodRecipe.getResult(), null);
        checkResult(inventory, view, blockedMatrix, ownWoodRecipe, ownWoodRecipe.getResult(), null);

        checkResult(inventory, view, simpleMatrix, blockedWoodRecipe, blockedWoodRecipe.getResult(), null);
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

        CustomShapelessRecipe smiteRecipe = new CustomShapelessRecipe(
                smiteSword, new CustomIngredient(Material.IRON_INGOT),
                new CustomIngredient(Material.STICK, stick -> stick.containsEnchantment(Enchantment.DAMAGE_UNDEAD))
        );
        CustomShapelessRecipe sharpRecipe = new CustomShapelessRecipe(
                sharpSword, new CustomIngredient(Material.IRON_INGOT),
                new CustomIngredient(Material.STICK, stick -> stick.containsEnchantment(Enchantment.DAMAGE_ALL))
        );

        craftingRecipes.add(smiteRecipe);
        craftingRecipes.add(sharpRecipe);
        Set<NamespacedKey> customKeys = new HashSet<>();
        craftingRecipes.register(plugin, customKeys);
        assertEquals(1, customKeys.size());

        ShapelessRecipe mergedRecipe = new ShapelessRecipe(customKeys.iterator().next(), smiteRecipe.result.apply(null));

        ItemStack[] blockedMatrix = {
                null, new ItemStack(Material.IRON_INGOT), null,
                null, null, null,
                new ItemStack(Material.STICK), null, null
        };

        ItemStack[] smiteMatrix = Arrays.copyOf(blockedMatrix, 9);
        smiteMatrix[6] = new ItemStack(Material.STICK);
        smiteMatrix[6].addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        ItemStack[] sharpMatrix = Arrays.copyOf(blockedMatrix, 9);
        sharpMatrix[6] = new ItemStack(Material.STICK);
        sharpMatrix[6].addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

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
        CustomShapelessRecipe customRecipe = new CustomShapelessRecipe(
                new ItemStack(Material.BLAZE_ROD),
                new CustomIngredient(Material.BLAZE_POWDER, blaze -> true, 1, new ItemStack(Material.GLOWSTONE_DUST)),
                new CustomIngredient(Material.STICK, stick -> true, 2, null),
                new CustomIngredient(Material.REDSTONE_TORCH, torch -> true, 3, new ItemStack(Material.TORCH, 3))
        );
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(keys.iterator().next(), customRecipe.result.apply(null));

        ItemStack[] matrix = {
                null, null, new ItemStack(Material.STICK),
                new ItemStack(Material.REDSTONE_TORCH), null, null,
                new ItemStack(Material.BLAZE_POWDER), null, null
        };

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        // All ingredients have a stacksize of 1, which is not enough
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[2].setAmount(2);
        matrix[3].setAmount(2);

        // We need 3 redstone torches, but only have 2
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[6].setAmount(2);
        matrix[3].setAmount(3);

        // The stacksize of the blaze powder is too large (so no space for the remaining item)
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[6].setAmount(1);
        matrix[3].setAmount(4);

        // The stacksize of the redstone torch is too large
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        // Perfect fit
        matrix[3].setAmount(3);
        ItemStack[] remainingMatrix = {
                null, null, null,
                new ItemStack(Material.TORCH, 3), null, null,
                new ItemStack(Material.GLOWSTONE_DUST), null, null
        };
        checkResult(inventory, view, matrix, bukkitRecipe, customRecipe.result.apply(null), remainingMatrix);

        // There are more sticks than needed, which is fine since it has no remaining item
        matrix[2].setAmount(10);
        remainingMatrix[2] = new ItemStack(Material.STICK, 8);
        checkResult(inventory, view, matrix, bukkitRecipe, customRecipe.result.apply(null), remainingMatrix);

        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(customRecipe.result.apply(null));
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
        CustomShapelessRecipe customRecipe = new CustomShapelessRecipe(new ItemStack(Material.GOLD_INGOT), new CustomIngredient(
                Material.REDSTONE, redstone -> true, 5, new ItemStack(Material.GLOWSTONE_DUST)
        ));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(keys.iterator().next(), customRecipe.result.apply(null));

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

        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(customRecipe.result.apply(null));
        inventory.setRecipe(bukkitRecipe);
        {
            CraftItemEvent craftEvent = createCraftEvent(inventory, view, InventoryAction.MOVE_TO_OTHER_INVENTORY);
            assertTrue(craftEvent.callEvent());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            assertArrayEquals(inventory.getMatrix(), remainingMatrix);
        }
    }

    @Test
    public void testShiftClick() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapelessRecipe customRecipe = new CustomShapelessRecipe(
                new ItemStack(Material.GOLD_INGOT),
                new CustomIngredient(Material.REDSTONE, redStone -> true, 1, null),
                new CustomIngredient(Material.COAL, coal -> true, 2, null),
                new CustomIngredient(Material.IRON_INGOT, iron -> true, 3, null)
        );
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(keys.iterator().next(), customRecipe.result.apply(null));

        ItemStack[] matrix = {
                null, null, new ItemStack(Material.REDSTONE, 10),
                null, null, new ItemStack(Material.COAL, 10),
                null, null, new ItemStack(Material.IRON_INGOT, 10)
        };
        ItemStack[] remainingMatrix = {
                null, null, new ItemStack(Material.REDSTONE, 7),
                null, null, new ItemStack(Material.COAL, 4),
                null, null, new ItemStack(Material.IRON_INGOT, 1)
        };

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(customRecipe.result.apply(null));
        inventory.setRecipe(bukkitRecipe);
        {
            CraftItemEvent craftEvent = createCraftEvent(inventory, view, InventoryAction.MOVE_TO_OTHER_INVENTORY);
            assertTrue(craftEvent.callEvent());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            assertArrayEquals(inventory.getMatrix(), remainingMatrix);
        }
    }

    @Test
    public void testIngredientsToResultFunction() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ItemStack[][] pExpectedIngredients = { null };
        int[] pCounter = { 0 };

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapelessRecipe recipe = new CustomShapelessRecipe(ingredients -> {
            assertArrayEquals(pExpectedIngredients[0], ingredients);
            pCounter[0] += 1;
            return new ItemStack(Material.REDSTONE);
        }, crafter -> true, new CustomIngredient(Material.COAL),
                new CustomIngredient(Material.STICK), new CustomIngredient(Material.REDSTONE)
        );
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.add(recipe);
        craftingRecipes.register(plugin, keys);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);
        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(keys.iterator().next(), recipe.result.apply(null));

        {
            ItemStack[] matrix = {
                    null, new ItemStack(Material.COAL, 3), new ItemStack(Material.STICK),
                    null, null, new ItemStack(Material.REDSTONE),
                    null, null, null
            };
            pExpectedIngredients[0] = new ItemStack[] {
                    new ItemStack(Material.COAL),
                    new ItemStack(Material.STICK),
                    new ItemStack(Material.REDSTONE)
            };
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }

        {
            ItemStack[] matrix = {
                    null ,new ItemStack(Material.STICK), null,
                    null, null, new ItemStack(Material.REDSTONE, 4),
                    new ItemStack(Material.COAL), null, null
            };
            pExpectedIngredients[0] = new ItemStack[] {
                    new ItemStack(Material.COAL),
                    new ItemStack(Material.STICK),
                    new ItemStack(Material.REDSTONE)
            };
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }

        {
            ItemStack[] matrix = {
                    null ,new ItemStack(Material.STICK), null,
                    null, null, null,
                    new ItemStack(Material.COAL), null, new ItemStack(Material.REDSTONE)
            };
            matrix[8].addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
            pExpectedIngredients[0] = new ItemStack[] {
                    new ItemStack(Material.COAL),
                    new ItemStack(Material.STICK),
                    new ItemStack(Material.REDSTONE)
            };
            pExpectedIngredients[0][2].addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }
    }

    @Test
    public void testCanCraft() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();

        Player admin = server.addPlayer();
        admin.addAttachment(plugin).setPermission("test", true);
        assertTrue(admin.hasPermission("test"));

        Player loser = server.addPlayer();
        assertFalse(loser.hasPermission("test"));

        CustomShapelessRecipe customRecipe = new CustomShapelessRecipe(
                ingredients -> new ItemStack(Material.IRON_INGOT),
                crafter -> crafter.hasPermission("test"),
                new CustomIngredient(Material.FLINT)
        );

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapelessRecipe bukkitRecipe = new ShapelessRecipe(keys.iterator().next(), customRecipe.result.apply(null));
        ItemStack[] matrix = {
                null, null, new ItemStack(Material.FLINT),
                null, null, null,
                null, null, null
        };

        {
            CraftingInventoryMock inventory = new CraftingInventoryMock(admin);
            InventoryView view = new PlayerInventoryViewMock(admin, inventory);
            checkResult(inventory, view, matrix, bukkitRecipe, new ItemStack(Material.IRON_INGOT), null);
        }

        {
            CraftingInventoryMock inventory = new CraftingInventoryMock(loser);
            InventoryView view = new PlayerInventoryViewMock(loser, inventory);
            checkResult(inventory, view, matrix, bukkitRecipe, null, null);
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
