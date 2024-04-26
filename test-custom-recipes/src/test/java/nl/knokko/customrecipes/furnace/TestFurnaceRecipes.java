package nl.knokko.customrecipes.furnace;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;
import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestFurnaceRecipes {

    @Test
    public void testBlockBadFuel() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        Player player = server.addPlayer();

        CustomFurnaceRecipes furnaceRecipes = new CustomFurnaceRecipes();
        furnaceRecipes.addBurnTimeFunction(fuel -> {
            if (fuel.containsEnchantment(Enchantment.DAMAGE_UNDEAD)) return 0;
            else return null;
        });
        furnaceRecipes.register(plugin, new HashSet<>());

        ItemStack badFuel = new ItemStack(Material.COAL);
        badFuel.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);

        PlayerInventoryViewMock view = new PlayerInventoryViewMock(player, server.createInventory(player, 9));
        {
            view.setCursor(badFuel);
            InventoryClickEvent clickEvent = new InventoryClickEvent(
                    view, InventoryType.SlotType.FUEL, 0, ClickType.LEFT, InventoryAction.PLACE_ALL
            );
            assertFalse(clickEvent.callEvent());
        }

        {
            view.setCursor(new ItemStack(Material.COAL));
            InventoryClickEvent clickEvent = new InventoryClickEvent(
                    view, InventoryType.SlotType.FUEL, 0, ClickType.LEFT, InventoryAction.PLACE_ALL
            );
            assertTrue(clickEvent.callEvent());
        }

        Block furnaceBlock = Objects.requireNonNull(server.getWorld("world")).getBlockAt(1, 2, 3);
        furnaceBlock.setType(Material.FURNACE);
        {
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, badFuel, 100);
            assertFalse(burnEvent.callEvent());
        }
        {
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, new ItemStack(Material.COAL), 100);
            assertTrue(burnEvent.callEvent());
        }
    }

    @Test
    public void testBlockBadIngredients() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        World world = server.addSimpleWorld("test");

        CustomFurnaceRecipes furnaceRecipes = new CustomFurnaceRecipes();
        furnaceRecipes.block(candidate -> candidate.getType() == Material.IRON_ORE &&
                candidate.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS));
        furnaceRecipes.register(plugin, new HashSet<>());

        ItemStack badIngredient = new ItemStack(Material.IRON_ORE);
        badIngredient.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 3);

        BlockMock furnaceBlock = new BlockMock(Material.FURNACE, new Location(world, 1, 2, 3));
        FurnaceMock furnaceMock = new FurnaceMock(furnaceBlock);
        furnaceBlock.setState(furnaceMock);
        FurnaceInventory furnace = furnaceMock.getInventory();
        {
            furnace.setSmelting(badIngredient);
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, new ItemStack(Material.COAL), 100);
            assertFalse(burnEvent.callEvent());
        }

        {
            furnace.setSmelting(new ItemStack(Material.IRON_ORE));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, new ItemStack(Material.COAL), 100);
            assertTrue(burnEvent.callEvent());
        }

        {
            furnace.setSmelting(badIngredient);
            @SuppressWarnings("deprecation")
            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, badIngredient, new ItemStack(Material.IRON_INGOT)
            );
            assertFalse(smeltEvent.callEvent());
        }

        {
            furnace.setSmelting(new ItemStack(Material.IRON_ORE));
            @SuppressWarnings("deprecation")
            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, new ItemStack(Material.IRON_ORE), new ItemStack(Material.IRON_INGOT)
            );
            assertTrue(smeltEvent.callEvent());
        }
    }

    @Test
    public void testRespectCustomBurnTimes() {
        MockBukkit.getOrCreateMock();
        JavaPlugin plugin = MockBukkit.createMockPlugin();

        CustomFurnaceRecipes furnaceRecipes = new CustomFurnaceRecipes();
        furnaceRecipes.addBurnTimeFunction(candidate -> {
            int level = candidate.getEnchantmentLevel(Enchantment.DIG_SPEED);
            if (level == 0) return null;
            return 20 * level;
        });
        furnaceRecipes.register(plugin, new HashSet<>());

        ItemStack simpleFuel = new ItemStack(Material.COAL);
        ItemStack dig1 = new ItemStack(Material.COAL);
        dig1.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        ItemStack dig2 = new ItemStack(Material.COAL);
        dig2.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);

        Block furnaceBlock = new BlockMock();
        {
            FurnaceBurnEvent event = new FurnaceBurnEvent(furnaceBlock, simpleFuel, 100);
            assertTrue(event.callEvent());
            assertEquals(100, event.getBurnTime());
        }
        {
            FurnaceBurnEvent event = new FurnaceBurnEvent(furnaceBlock, dig1, 100);
            assertTrue(event.callEvent());
            assertEquals(20, event.getBurnTime());
        }
        {
            FurnaceBurnEvent event = new FurnaceBurnEvent(furnaceBlock, dig2, 100);
            assertTrue(event.callEvent());
            assertEquals(40, event.getBurnTime());
        }
    }

    private CustomIngredient enchantedRedstone(Enchantment enchantment) {
        return new CustomIngredient(
                Material.REDSTONE, candidate -> candidate.getEnchantmentLevel(enchantment) > 0,
                1, null
        );
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testRecipesThatShareInputMaterial() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        World world = server.addSimpleWorld("test124");

        ItemStack badIngredient = new ItemStack(Material.REDSTONE);
        ItemStack boneIngredient = new ItemStack(Material.REDSTONE);
        boneIngredient.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemStack dirtIngredient = new ItemStack(Material.REDSTONE);
        dirtIngredient.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2);

        CustomFurnaceRecipes furnaceRecipes = new CustomFurnaceRecipes();
        furnaceRecipes.add(new CustomFurnaceRecipe(
                input -> new ItemStack(Material.BONE), enchantedRedstone(Enchantment.DAMAGE_ALL), 12f, 345
        ));
        furnaceRecipes.add(new CustomFurnaceRecipe(
                input -> new ItemStack(Material.DIRT), enchantedRedstone(Enchantment.DAMAGE_ARTHROPODS), 12f, 345)
        );
        Set<NamespacedKey> keys = new HashSet<>();
        furnaceRecipes.register(plugin, keys);

        assertEquals(1, keys.size());
        FurnaceRecipe vanillaRecipe = (FurnaceRecipe) Objects.requireNonNull(server.getRecipe(keys.iterator().next()));
        assertTrue(vanillaRecipe.getInputChoice().test(boneIngredient));
        assertTrue(vanillaRecipe.getInputChoice().test(dirtIngredient));
        assertEquals(Material.BONE, vanillaRecipe.getResult().getType());
        assertEquals(12f, vanillaRecipe.getExperience(), 0f);
        assertEquals(345, vanillaRecipe.getCookingTime());

        ItemStack fuel = new ItemStack(Material.COAL_BLOCK);

        BlockMock furnaceBlock = new BlockMock(Material.FURNACE, new Location(world, 1, 2, 3));
        FurnaceMock furnaceMock = new FurnaceMock(furnaceBlock);
        furnaceBlock.setState(furnaceMock);
        FurnaceInventory furnace = furnaceMock.getInventory();

        // Check that ingredients are blocked when it doesn't satisfy any of the recipes
        {
            furnace.setSmelting(badIngredient);
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertFalse(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(furnaceBlock, badIngredient, new ItemStack(Material.BONE));
            assertFalse(smeltEvent.callEvent());
        }

        // Check that recipe 1 is accepted
        {
            furnace.setSmelting(boneIngredient);
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertTrue(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(furnaceBlock, boneIngredient, new ItemStack(Material.BONE));
            assertTrue(smeltEvent.callEvent());
        }

        // Check that recipe 2 is accepted
        {
            furnace.setSmelting(dirtIngredient);
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertTrue(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(furnaceBlock, dirtIngredient, new ItemStack(Material.BONE));
            assertTrue(smeltEvent.callEvent());

            // Check that the result is changed to dirt
            assertEquals(Material.DIRT, smeltEvent.getResult().getType());
        }

        // Check that recipe 2 is blocked when the output of recipe 1 is present in the result
        {
            furnace.setSmelting(dirtIngredient);
            furnace.setResult(new ItemStack(Material.BONE));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertFalse(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(furnaceBlock, dirtIngredient, new ItemStack(Material.BONE));
            assertFalse(smeltEvent.callEvent());
        }
    }


    @Test
    @SuppressWarnings("deprecation")
    public void testIngredientWithAmount() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        World world = server.addSimpleWorld("test124");

        CustomFurnaceRecipes furnaceRecipes = new CustomFurnaceRecipes();
        furnaceRecipes.add(new CustomFurnaceRecipe(input -> new ItemStack(Material.DIAMOND), new CustomIngredient(
                Material.GOLD_INGOT, candidate -> true, 5, null
        ), 1f, 1));
        furnaceRecipes.register(plugin, new HashSet<>());

        ItemStack fuel = new ItemStack(Material.COAL_BLOCK);

        BlockMock furnaceBlock = new BlockMock(Material.FURNACE, new Location(world, 1, 2, 3));
        FurnaceMock furnaceMock = new FurnaceMock(furnaceBlock);
        furnaceBlock.setState(furnaceMock);
        FurnaceInventory furnace = furnaceMock.getInventory();

        // 4 gold is not enough
        {
            furnace.setSmelting(new ItemStack(Material.GOLD_INGOT, 4));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertFalse(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, new ItemStack(Material.GOLD_INGOT, 4), new ItemStack(Material.DIAMOND)
            );
            assertFalse(smeltEvent.callEvent());
        }

        // 5 gold is perfect
        {
            furnace.setSmelting(new ItemStack(Material.GOLD_INGOT, 5));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertTrue(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, new ItemStack(Material.GOLD_INGOT, 5), new ItemStack(Material.DIAMOND)
            );
            assertTrue(smeltEvent.callEvent());
            assertTrue(furnace.getSmelting() == null || furnace.getSmelting().isEmpty());
        }

        // 6 gold is also enough
        {
            furnace.setSmelting(new ItemStack(Material.GOLD_INGOT, 6));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertTrue(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, new ItemStack(Material.GOLD_INGOT, 6), new ItemStack(Material.DIAMOND)
            );
            assertTrue(smeltEvent.callEvent());
            ItemStack remaining = furnace.getSmelting();
            assertEquals(1, remaining.getAmount());
            assertEquals(Material.GOLD_INGOT, remaining.getType());
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testIngredientWithAmountAndRemainingItem() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        World world = server.addSimpleWorld("test1234");

        CustomFurnaceRecipes furnaceRecipes = new CustomFurnaceRecipes();
        furnaceRecipes.add(new CustomFurnaceRecipe(input -> new ItemStack(Material.DIAMOND), new CustomIngredient(
                Material.GOLD_INGOT, candidate -> true, 5, goldIngot -> {
                    ItemStack ironIngot = new ItemStack(Material.IRON_INGOT, goldIngot.getAmount());
                    if (goldIngot.containsEnchantment(Enchantment.DIG_SPEED)) {
                        ironIngot.addUnsafeEnchantment(Enchantment.DIG_SPEED, goldIngot.getEnchantmentLevel(Enchantment.DIG_SPEED));
                    }
                    return ironIngot;
            }
        ), 1f, 1));
        furnaceRecipes.register(plugin, new HashSet<>());

        ItemStack fuel = new ItemStack(Material.COAL_BLOCK);

        BlockMock furnaceBlock = new BlockMock(Material.FURNACE, new Location(world, 1, 2, 3));
        FurnaceMock furnaceMock = new FurnaceMock(furnaceBlock);
        furnaceBlock.setState(furnaceMock);
        FurnaceInventory furnace = furnaceMock.getInventory();

        // 4 gold is not enough
        {
            furnace.setSmelting(new ItemStack(Material.GOLD_INGOT, 4));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertFalse(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, new ItemStack(Material.GOLD_INGOT, 4), new ItemStack(Material.DIAMOND)
            );
            assertFalse(smeltEvent.callEvent());
        }

        // 5 gold works
        {
            ItemStack input = new ItemStack(Material.GOLD_INGOT, 5);
            input.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
            furnace.setSmelting(input);

            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertTrue(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, input, new ItemStack(Material.DIAMOND)
            );
            assertTrue(smeltEvent.callEvent());
            ItemStack remaining = Objects.requireNonNull(furnace.getSmelting());
            assertEquals(Material.IRON_INGOT, remaining.getType());
            assertEquals(5, remaining.getAmount());
            assertEquals(2, remaining.getEnchantmentLevel(Enchantment.DIG_SPEED));
        }

        // 6 gold is too much
        {
            furnace.setSmelting(new ItemStack(Material.GOLD_INGOT, 6));
            FurnaceBurnEvent burnEvent = new FurnaceBurnEvent(furnaceBlock, fuel, 100);
            assertFalse(burnEvent.callEvent());

            FurnaceSmeltEvent smeltEvent = new FurnaceSmeltEvent(
                    furnaceBlock, new ItemStack(Material.GOLD_INGOT, 6), new ItemStack(Material.DIAMOND)
            );
            assertFalse(smeltEvent.callEvent());
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
