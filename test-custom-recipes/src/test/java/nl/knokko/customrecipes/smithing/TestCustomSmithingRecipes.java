package nl.knokko.customrecipes.smithing;

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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestCustomSmithingRecipes {

    @Test
    public void testBlockIngredients() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        SmithingRecipe vanillaUpgradeRecipe = new SmithingTransformRecipe(
                new NamespacedKey("minecraft", "upgrade"),
                new ItemStack(Material.NETHERITE_SWORD),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.MaterialChoice(Material.DIAMOND_SWORD),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)
        );
        SmithingRecipe blockedUpgradeRecipe = new SmithingTransformRecipe(
                new NamespacedKey("blocked", "upgrade"),
                new ItemStack(Material.NETHERITE_SWORD),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.MaterialChoice(Material.DIAMOND_SWORD),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)
        );

        ItemStack silverSword = new ItemStack(Material.DIAMOND_SWORD);
        silverSword.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        CustomSmithingRecipes smithingRecipes = new CustomSmithingRecipes();
        smithingRecipes.blockIngredients(new IngredientBlocker(
                namespace -> namespace == null || !namespace.equals("minecraft"),
                item -> item != null && item.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) > 2
        ));

        CustomSmithingRecipe customRecipe = new CustomSmithingRecipe(
                ingredients -> new ItemStack(Material.NETHERITE_SWORD),
                new CustomIngredient(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new CustomIngredient(Material.DIAMOND_SWORD), new CustomIngredient(Material.NETHERITE_INGOT)
        );
        smithingRecipes.add(customRecipe);

        Set<NamespacedKey> customKeys = new HashSet<>();
        smithingRecipes.register(plugin, customKeys);
        assertEquals(1, customKeys.size());

        SmithingRecipe ownRecipe = new SmithingTransformRecipe(
                customKeys.iterator().next(), customRecipe.result.apply(null),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new RecipeChoice.MaterialChoice(Material.DIAMOND_SWORD),
                new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)
        );

        BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        ItemStack template = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        ItemStack ingot = new ItemStack(Material.NETHERITE_INGOT);
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);

        // Block this recipe because both the namespace and an ingredient are blocked
        checkResult(inventory, view, blockedUpgradeRecipe, null, template, silverSword, ingot);

        // Don't block this recipe because the namespace is not blocked
        checkResult(inventory, view, vanillaUpgradeRecipe, vanillaUpgradeRecipe.getResult(), template, silverSword, ingot);

        // Never block own recipes
        checkResult(inventory, view, ownRecipe, ownRecipe.getResult(), template, silverSword, ingot);

        // Don't block this recipe because the ingredients are not blocked
        checkResult(inventory, view, blockedUpgradeRecipe, blockedUpgradeRecipe.getResult(), template, diamondSword, ingot);
    }

    private static void checkResult(
            BetterSmithingInventoryMock inventory, InventoryView view, SmithingRecipe recipe,
            ItemStack expectedResult, ItemStack... ingredients
    ) {
        ItemStack[] initialIngredients = ingredients;
        ItemStack[] expectedFinalIngredients = null;
        if (ingredients.length == 6) {
            expectedFinalIngredients = Arrays.copyOfRange(ingredients, 3, 6);
            initialIngredients = Arrays.copyOf(ingredients, 3);
        } else if (ingredients.length != 3) throw new IllegalArgumentException();

        inventory.setStorageContents(initialIngredients);
        inventory.setResult(recipe.getResult());
        inventory.setRecipe(recipe);
        {
            PrepareSmithingEvent prepareEvent = new PrepareSmithingEvent(view, recipe.getResult());
            assertTrue(prepareEvent.callEvent());
            assertEquals(expectedResult, prepareEvent.getResult());

            inventory.setResult(recipe.getResult());
            SmithItemEvent smithEvent = createSmithEvent(view);
            smithEvent.setCurrentItem(recipe.getResult());
            assertEquals(expectedResult != null, smithEvent.callEvent());
            assertEquals(expectedResult, smithEvent.getCurrentItem());

            Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
            if (expectedFinalIngredients != null) {
                assertArrayEquals(expectedFinalIngredients, inventory.getStorageContents());
            }
            if (expectedResult == null && expectedFinalIngredients == null) {
                assertArrayEquals(initialIngredients, inventory.getStorageContents());
            }
        }
    }

    private static SmithItemEvent createSmithEvent(InventoryView view) {
        return new SmithItemEvent(view, InventoryType.SlotType.RESULT, 4, ClickType.LEFT, InventoryAction.PICKUP_ALL);
    }

    @Test
    public void testOverlappingWeakRecipes() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomIngredient iron = new CustomIngredient(Material.IRON_INGOT);

        ItemStack smiteStick = new ItemStack(Material.STICK);
        smiteStick.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        ItemStack sharpStick = new ItemStack(Material.STICK);
        sharpStick.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

        ItemStack smiteSword = new ItemStack(Material.IRON_SWORD);
        smiteSword.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);

        ItemStack sharpSword = new ItemStack(Material.IRON_SWORD);
        sharpSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

        CustomSmithingRecipes smithingRecipes = new CustomSmithingRecipes();
        smithingRecipes.add(new CustomSmithingRecipe(ingredients -> smiteSword, iron, new CustomIngredient(
                Material.STICK, stick -> stick.containsEnchantment(Enchantment.DAMAGE_UNDEAD)
        ), iron));
        smithingRecipes.add(new CustomSmithingRecipe(ingredients -> sharpSword, iron, new CustomIngredient(
                Material.STICK, stick -> stick.containsEnchantment(Enchantment.DAMAGE_ALL)
        ), iron));

        Set<NamespacedKey> customKeys = new HashSet<>();
        smithingRecipes.register(plugin, customKeys);
        assertEquals(1, customKeys.size());

        SmithingRecipe mergedRecipe = new SmithingTransformRecipe(
                customKeys.iterator().next(), smiteSword.clone(),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT),
                new RecipeChoice.MaterialChoice(Material.STICK),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT)
        );

        BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        ItemStack singleIron = new ItemStack(Material.IRON_INGOT);
        checkResult(inventory, view, mergedRecipe, null, singleIron, new ItemStack(Material.STICK), singleIron);
        checkResult(inventory, view, mergedRecipe, smiteSword, singleIron, smiteStick, singleIron);
        checkResult(inventory, view, mergedRecipe, sharpSword, singleIron, sharpStick, singleIron);
    }

    @Test
    public void testAmountAndRemainingItem() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomSmithingRecipes smithingRecipes = new CustomSmithingRecipes();
        smithingRecipes.add(new CustomSmithingRecipe(
                ingredients -> new ItemStack(Material.BLAZE_ROD),
                new CustomIngredient(Material.BLAZE_POWDER, powder -> true, 1, powder -> new ItemStack(Material.GLOWSTONE_DUST)),
                new CustomIngredient(Material.STICK, stick -> true, 2, null),
                new CustomIngredient(Material.REDSTONE_TORCH, torch -> true, 3, redstoneTorches -> {
                    ItemStack torch = new ItemStack(Material.TORCH, 3);
                    if (redstoneTorches.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)) {
                        torch.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, redstoneTorches.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS));
                    }
                    return torch;
                })
        ));

        Set<NamespacedKey> keys = new HashSet<>();
        smithingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        SmithingRecipe bukkitRecipe = new SmithingTransformRecipe(
                keys.iterator().next(), new ItemStack(Material.BLAZE_POWDER),
                new RecipeChoice.MaterialChoice(Material.BLAZE_POWDER),
                new RecipeChoice.MaterialChoice(Material.STICK),
                new RecipeChoice.MaterialChoice(Material.REDSTONE_TORCH)
        );

        BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        // All ingredient amounts are 1, which is not enough
        checkResult(
                inventory, view, bukkitRecipe, null, new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.STICK), new ItemStack(Material.REDSTONE_TORCH)
        );

        // 3 redstone torches are required, but only 2 are given
        checkResult(
                inventory, view, bukkitRecipe, null, new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.STICK, 2), new ItemStack(Material.REDSTONE_TORCH, 2)
        );

        // Too much blaze powder: exactly 1 is required, but 2 are given
        checkResult(
                inventory, view, bukkitRecipe, null, new ItemStack(Material.BLAZE_POWDER, 2),
                new ItemStack(Material.STICK, 2), new ItemStack(Material.REDSTONE_TORCH, 3)
        );

        // Too many redstone torches this time
        checkResult(
                inventory, view, bukkitRecipe, null, new ItemStack(Material.BLAZE_POWDER),
                new ItemStack(Material.STICK, 2), new ItemStack(Material.REDSTONE_TORCH, 4)
        );

        // Perfect fit
        ItemStack enchantedRedstoneTorches = new ItemStack(Material.REDSTONE_TORCH, 3);
        enchantedRedstoneTorches.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
        ItemStack enchantedTorches = new ItemStack(Material.TORCH, 3);
        enchantedTorches.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
        checkResult(
                inventory, view, bukkitRecipe, new ItemStack(Material.BLAZE_ROD),
                new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.STICK, 2), enchantedRedstoneTorches,
                new ItemStack(Material.GLOWSTONE_DUST), null, enchantedTorches
        );

        // More sticks than needed, which is fine
        checkResult(
                inventory, view, bukkitRecipe, new ItemStack(Material.BLAZE_ROD),
                new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.STICK, 10), enchantedRedstoneTorches,
                new ItemStack(Material.GLOWSTONE_DUST), new ItemStack(Material.STICK, 8), enchantedTorches
        );

        SmithItemEvent smithEvent = new SmithItemEvent(
                view, InventoryType.SlotType.RESULT, 4,
                ClickType.LEFT, InventoryAction.MOVE_TO_OTHER_INVENTORY
        );
        smithEvent.setCurrentItem(new ItemStack(Material.BLAZE_ROD));
        inventory.setRecipe(bukkitRecipe);
        inventory.setStorageContents(new ItemStack[]{
                new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.STICK, 10), enchantedRedstoneTorches
        });
        inventory.setResult(new ItemStack(Material.BLAZE_ROD));
        assertTrue(smithEvent.callEvent());

        Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
        assertEquals(new ItemStack(Material.BLAZE_ROD), smithEvent.getCurrentItem());
        assertEquals(new ItemStack(Material.GLOWSTONE_DUST), inventory.getItem(0));
        assertEquals(new ItemStack(Material.STICK, 8), inventory.getItem(1));
        assertEquals(enchantedTorches, inventory.getItem(2));
    }

    @Test
    public void testShiftClick() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomSmithingRecipes smithingRecipes = new CustomSmithingRecipes();
        smithingRecipes.add(new CustomSmithingRecipe(
                ingredients -> new ItemStack(Material.GOLD_INGOT),
                new CustomIngredient(Material.REDSTONE),
                new CustomIngredient(Material.COAL, coal -> true, 2, null),
                new CustomIngredient(Material.IRON_INGOT, iron -> true, 3, null)
        ));
        Set<NamespacedKey> keys = new HashSet<>();
        smithingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        SmithingRecipe bukkitRecipe = new SmithingTransformRecipe(
                keys.iterator().next(), new ItemStack(Material.GOLD_INGOT),
                new RecipeChoice.MaterialChoice(Material.REDSTONE),
                new RecipeChoice.MaterialChoice(Material.COAL),
                new RecipeChoice.MaterialChoice(Material.IRON_INGOT)
        );

        BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        SmithItemEvent smithEvent = new SmithItemEvent(
                view, InventoryType.SlotType.RESULT, 3, ClickType.LEFT, InventoryAction.MOVE_TO_OTHER_INVENTORY
        );
        inventory.setItem(0, new ItemStack(Material.REDSTONE, 10));
        inventory.setItem(1, new ItemStack(Material.COAL, 10));
        inventory.setItem(2, new ItemStack(Material.IRON_INGOT, 10));
        inventory.setRecipe(bukkitRecipe);
        inventory.setResult(new ItemStack(Material.GOLD_INGOT));
        assertFalse(smithEvent.callEvent());

        smithEvent = new SmithItemEvent(
                view, InventoryType.SlotType.RESULT, 3, ClickType.LEFT, InventoryAction.MOVE_TO_OTHER_INVENTORY
        );
        inventory.setItem(0, new ItemStack(Material.REDSTONE, 1));
        inventory.setRecipe(bukkitRecipe);
        inventory.setResult(new ItemStack(Material.GOLD_INGOT));

        assertTrue(smithEvent.callEvent());
        Objects.requireNonNull(MockBukkit.getOrCreateMock()).getScheduler().performOneTick();
        assertEquals(new ItemStack(Material.REDSTONE, 1), inventory.getItem(0));
        assertEquals(new ItemStack(Material.COAL, 8), inventory.getItem(1));
        assertEquals(new ItemStack(Material.IRON_INGOT, 7), inventory.getItem(2));
    }

    @Test
    public void testIngredientsToResultFunction() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        ItemStack[][] pExpectedIngredients = { null };
        int[] pCounter = { 0 };

        CustomSmithingRecipes smithingRecipes = new CustomSmithingRecipes();
        smithingRecipes.add(new CustomSmithingRecipe(ingredients -> {
            assertArrayEquals(pExpectedIngredients[0], ingredients);
            pCounter[0] += 1;
            if (ingredients != null) {
                for (ItemStack ingredient : ingredients) ingredient.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 3);
            }
            return new ItemStack(Material.REDSTONE);
        }, new CustomIngredient(Material.COAL), new CustomIngredient(
                Material.STICK, stick -> true, 2, null), new CustomIngredient(Material.GLOWSTONE)
        ));

        Set<NamespacedKey> keys = new HashSet<>();
        smithingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        SmithingRecipe bukkitRecipe = new SmithingTransformRecipe(
                keys.iterator().next(), new ItemStack(Material.GOLD_INGOT),
                new RecipeChoice.MaterialChoice(Material.COAL),
                new RecipeChoice.MaterialChoice(Material.STICK),
                new RecipeChoice.MaterialChoice(Material.GLOWSTONE)
        );

        BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(player);
        InventoryView view = new PlayerInventoryViewMock(player, inventory);

        pExpectedIngredients[0] = new ItemStack[] {
                new ItemStack(Material.COAL), new ItemStack(Material.STICK, 2), new ItemStack(Material.GLOWSTONE)
        };
        pExpectedIngredients[0][0].addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

        ItemStack enchantedCoal = new ItemStack(Material.COAL);
        enchantedCoal.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        ItemStack clonedCoal = enchantedCoal.clone();
        ItemStack sticks = new ItemStack(Material.STICK, 2);
        ItemStack glowstones = new ItemStack(Material.GLOWSTONE, 2);
        checkIngredients(inventory, view, bukkitRecipe, pCounter, enchantedCoal, sticks, glowstones);

        assertEquals(clonedCoal, enchantedCoal);
        assertEquals(new ItemStack(Material.STICK, 2), sticks);
        assertEquals(new ItemStack(Material.GLOWSTONE, 2), glowstones);
    }

    private static void checkIngredients(
            BetterSmithingInventoryMock inventory, InventoryView view,
            SmithingRecipe recipe, int[] pCounter, ItemStack... ingredients
    ) {

        inventory.setStorageContents(ingredients);
        inventory.setResult(recipe.getResult());
        inventory.setRecipe(recipe);
        {
            int oldCounter = pCounter[0];
            PrepareSmithingEvent prepareEvent = new PrepareSmithingEvent(view, recipe.getResult().clone());
            assertTrue(prepareEvent.callEvent());
            assertEquals(oldCounter + 1, pCounter[0]);

            inventory.setResult(recipe.getResult());
            SmithItemEvent smithEvent = createSmithEvent(view);
            assertTrue(smithEvent.callEvent());
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

        CustomSmithingRecipes smithingRecipes = new CustomSmithingRecipes();
        smithingRecipes.add(new CustomSmithingRecipe(
                ingredients -> new ItemStack(Material.IRON_INGOT),
                crafter -> crafter.hasPermission("test"),
                new CustomIngredient(Material.FLINT),
                new CustomIngredient(Material.FLINT),
                new CustomIngredient(Material.FLINT)
        ));

        Set<NamespacedKey> keys = new HashSet<>();
        smithingRecipes.register(plugin, keys);
        assertEquals(1, keys.size());

        SmithingRecipe bukkitRecipe = new SmithingTransformRecipe(
                keys.iterator().next(), new ItemStack(Material.IRON_INGOT),
                new RecipeChoice.MaterialChoice(Material.FLINT),
                new RecipeChoice.MaterialChoice(Material.FLINT),
                new RecipeChoice.MaterialChoice(Material.FLINT)
        );

        ItemStack flint = new ItemStack(Material.FLINT);
        {
            BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(admin);
            InventoryView view = new PlayerInventoryViewMock(admin, inventory);
            checkResult(inventory, view, bukkitRecipe, new ItemStack(Material.IRON_INGOT), flint, flint, flint);
        }
        {
            BetterSmithingInventoryMock inventory = new BetterSmithingInventoryMock(loser);
            InventoryView view = new PlayerInventoryViewMock(loser, inventory);
            checkResult(inventory, view, bukkitRecipe, null, flint, flint, flint);
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
