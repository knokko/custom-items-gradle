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

        ShapedRecipe ownTorchRecipe = new ShapedRecipe(customKeys.iterator().next(), customRecipe.result.apply(null));

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

    static void checkResult(
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

    static CraftItemEvent createCraftEvent(CraftingInventoryMock inventory, InventoryView view) {
        return createCraftEvent(inventory, view, InventoryAction.PICKUP_ALL);
    }

    static CraftItemEvent createCraftEvent(CraftingInventoryMock inventory, InventoryView view, InventoryAction action) {
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

        ShapedRecipe mergedRecipe = new ShapedRecipe(customKeys.iterator().next(), smiteRecipe.result.apply(null));

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
                Material.BLAZE_POWDER, blazePowder -> true, 1, blaze -> new ItemStack(Material.GLOWSTONE_DUST)
        ));
        customRecipe.ingredientMap.put('b', new CustomIngredient(
                Material.STICK, stick -> true, 2, null
        ));
        customRecipe.ingredientMap.put('c', new CustomIngredient(Material.REDSTONE_TORCH, torch -> true, 3, redstoneTorches -> {
                ItemStack torch = new ItemStack(Material.TORCH, 3);
                if (redstoneTorches.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)) {
                    torch.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, redstoneTorches.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS));
                }
                return torch;
        }));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), customRecipe.result.apply(null));

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
        remainingMatrix[8].addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
        matrix[8].setAmount(3);
        matrix[8].addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
        checkResult(inventory, view, matrix, bukkitRecipe, customRecipe.result.apply(null), remainingMatrix);

        // Stacksize is larger than needed for the second slot, which is fine since it has no remaining item
        matrix[7].setAmount(10);
        remainingMatrix[7] = new ItemStack(Material.STICK, 8);
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
        CustomShapedRecipe customRecipe = new CustomShapedRecipe(new ItemStack(Material.GOLD_INGOT), "c");
        customRecipe.ingredientMap.put('c', new CustomIngredient(
                Material.REDSTONE, redStone -> true, 5, redstone -> new ItemStack(Material.GLOWSTONE_DUST)
        ));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), customRecipe.result.apply(null));

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
            assertFalse(craftEvent.callEvent());

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
        CustomShapedRecipe customRecipe = new CustomShapedRecipe(new ItemStack(Material.GOLD_INGOT), "a", "b", "c");
        customRecipe.ingredientMap.put('a', new CustomIngredient(
                Material.REDSTONE, redStone -> true, 1, null
        ));
        customRecipe.ingredientMap.put('b', new CustomIngredient(
                Material.COAL, coal -> true, 2, null
        ));
        customRecipe.ingredientMap.put('c', new CustomIngredient(
                Material.IRON_INGOT, iron -> true, 3, null
        ));
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), customRecipe.result.apply(null));

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
            assertFalse(craftEvent.callEvent());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            assertArrayEquals(inventory.getMatrix(), remainingMatrix);
        }
    }

    @Test
    public void testConflictingOffsetShapes() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapedRecipe freeRecipe = new CustomShapedRecipe(new ItemStack(Material.GOLD_INGOT), "a");
        CustomShapedRecipe fixedRecipe = new CustomShapedRecipe(new ItemStack(Material.DIAMOND), "   ", " a ", "   ");

        freeRecipe.ingredientMap.put('a', new CustomIngredient(
                Material.IRON_INGOT, iron -> iron.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) > 0
        ));
        fixedRecipe.ingredientMap.put('a', new CustomIngredient(
                Material.IRON_INGOT, iron -> iron.getEnchantmentLevel(Enchantment.DAMAGE_ALL) > 0
        ));
        craftingRecipes.add(freeRecipe);
        craftingRecipes.add(fixedRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), freeRecipe.result.apply(null));

        ItemStack smiteIron = new ItemStack(Material.IRON_INGOT);
        smiteIron.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 1);

        ItemStack sharpIron = new ItemStack(Material.IRON_INGOT);
        sharpIron.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack[] matrix = {
                null, null, null,
                null, smiteIron, null,
                null, null, null
        };

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        checkResult(inventory, view, matrix, bukkitRecipe, freeRecipe.result.apply(null), null);

        matrix[4] = sharpIron;
        checkResult(inventory, view, matrix, bukkitRecipe, fixedRecipe.result.apply(null), null);

        matrix[4] = null;
        matrix[3] = smiteIron;
        checkResult(inventory, view, matrix, bukkitRecipe, freeRecipe.result.apply(null), null);

        matrix[3] = sharpIron;
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[3] = null;
        matrix[7] = sharpIron;
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        ItemStack hybridIron = new ItemStack(Material.IRON_INGOT);
        hybridIron.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 1);
        hybridIron.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        matrix[4] = hybridIron;
        checkResult(inventory, view, matrix, bukkitRecipe, null, null);

        matrix[7] = null;
        {
            inventory.setMatrix(matrix);
            inventory.setRecipe(bukkitRecipe);
            inventory.setResult(new ItemStack(Material.IRON_INGOT));

            CraftItemEvent event = createCraftEvent(inventory, view);
            assertTrue(event.callEvent());

            Material result = Objects.requireNonNull(inventory.getResult()).getType();
            assertTrue(result == Material.DIAMOND || result == Material.GOLD_INGOT);
        }
    }

    @Test
    public void testIngredientsToResultFunctionTriangle() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ItemStack[][] pExpectedIngredients = { null };
        int[] pCounter = { 0 };

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapedRecipe compactRecipe = new CustomShapedRecipe(ingredients -> {
            assertArrayEquals(pExpectedIngredients[0], ingredients);
            pCounter[0] += 1;
            return new ItemStack(Material.REDSTONE);
        }, crafter -> true, "ab", " c");
        compactRecipe.ingredientMap.put('a', new CustomIngredient(Material.COAL));
        compactRecipe.ingredientMap.put('b', new CustomIngredient(Material.STICK));
        compactRecipe.ingredientMap.put('c', new CustomIngredient(Material.GLOWSTONE));

        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.add(compactRecipe);
        craftingRecipes.register(plugin, keys);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);
        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), compactRecipe.result.apply(null));

        {
            ItemStack[] matrix = {
                    null, new ItemStack(Material.COAL, 3), new ItemStack(Material.STICK),
                    null, null, new ItemStack(Material.GLOWSTONE),
                    null, null, null
            };
            pExpectedIngredients[0] = new ItemStack[] {
                    new ItemStack(Material.COAL), new ItemStack(Material.STICK),
                    null, new ItemStack(Material.GLOWSTONE)
            };
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }
    }

    @Test
    public void testIngredientsToResultFunctionLowerStrip() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ItemStack[][] pExpectedIngredients = { null };
        int[] pCounter = { 0 };

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapedRecipe halfRecipe = new CustomShapedRecipe(ingredients -> {
            assertArrayEquals(pExpectedIngredients[0], ingredients);
            pCounter[0] += 1;
            return new ItemStack(Material.REDSTONE);
        }, crafter -> true, "  ", "ab");
        halfRecipe.ingredientMap.put('a', new CustomIngredient(Material.COAL));
        halfRecipe.ingredientMap.put('b', new CustomIngredient(Material.STICK));

        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.add(halfRecipe);
        craftingRecipes.register(plugin, keys);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);
        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), halfRecipe.result.apply(null));

        {
            ItemStack[] matrix = {
                    null, null, null,
                    new ItemStack(Material.COAL, 3), new ItemStack(Material.STICK), null,
                    null, null, null
            };
            pExpectedIngredients[0] = new ItemStack[] {
                    null, null,
                    new ItemStack(Material.COAL), new ItemStack(Material.STICK),
            };
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }

        {
            ItemStack[] matrix = {
                    null, null, null,
                    null, null, null,
                    null, new ItemStack(Material.COAL, 4), new ItemStack(Material.STICK)
            };
            pExpectedIngredients[0] = new ItemStack[] {
                    null, null,
                    new ItemStack(Material.COAL), new ItemStack(Material.STICK),
            };
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }
    }

    @Test
    public void testIngredientsToResultFunctionVertical() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ItemStack[][] pExpectedIngredients = { null };
        int[] pCounter = { 0 };

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        CustomShapedRecipe verticalRecipe = new CustomShapedRecipe(ingredients -> {
            assertArrayEquals(pExpectedIngredients[0], ingredients);
            pCounter[0] += 1;
            return new ItemStack(Material.REDSTONE);
        }, crafter -> true, "a", " ", " ");
        verticalRecipe.ingredientMap.put('a', new CustomIngredient(Material.COAL));

        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.add(verticalRecipe);
        craftingRecipes.register(plugin, keys);

        CraftingInventoryMock inventory = new CraftingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);
        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), verticalRecipe.result.apply(null));

        pExpectedIngredients[0] = new ItemStack[] {
                new ItemStack(Material.COAL),
                null,
                null
        };

        {
            ItemStack[] matrix = {
                    new ItemStack(Material.COAL, 2), null, null,
                    null, null, null,
                    null, null, null,
            };

            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }

        {
            ItemStack[] matrix = {
                    null, null, new ItemStack(Material.COAL, 2),
                    null, null, null,
                    null, null, null
            };
            checkIngredients(inventory, view, matrix, bukkitRecipe, pCounter);
        }
    }

    static void checkIngredients(
            CraftingInventoryMock inventory, InventoryView view,
            ItemStack[] matrix, Recipe recipe, int[] pCounter
    ) {
        inventory.setMatrix(Arrays.copyOf(matrix, matrix.length));
        inventory.setResult(recipe.getResult());
        inventory.setRecipe(recipe);
        {
            int oldCounter = pCounter[0];
            PrepareItemCraftEvent prepareEvent = new PrepareItemCraftEvent(inventory, view, false);
            assertTrue(prepareEvent.callEvent());
            assertEquals(oldCounter + 1, pCounter[0]);

            inventory.setResult(recipe.getResult());
            CraftItemEvent craftEvent = createCraftEvent(inventory, view);
            assertTrue(craftEvent.callEvent());
            assertEquals(oldCounter + 2, pCounter[0]);
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

        CustomShapedRecipe customRecipe = new CustomShapedRecipe(
                ingredients -> new ItemStack(Material.IRON_INGOT), crafter -> crafter.hasPermission("test"), "a"
        );
        customRecipe.ingredientMap.put('a', new CustomIngredient(Material.FLINT));

        CustomCraftingRecipes craftingRecipes = new CustomCraftingRecipes();
        craftingRecipes.add(customRecipe);
        Set<NamespacedKey> keys = new HashSet<>();
        craftingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        ShapedRecipe bukkitRecipe = new ShapedRecipe(keys.iterator().next(), customRecipe.result.apply(null));
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
