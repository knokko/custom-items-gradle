package customitems.plugin.set.backward;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.recipe.ingredient.CustomIngredient;
import nl.knokko.customitems.plugin.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.*;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.OutputTable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Iterator;

import static customitems.plugin.set.backward.Backward6.*;
import static customitems.plugin.set.backward.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class Backward7 {

    public static void testBackwardCompatibility7() {
        ItemSet set7 = loadItemSet("backward7");
        testItemsOld6(set7, 21);
        testRecipesOld6(set7, 3);
        testBlockDropsOld6(set7, 1);
        testMobDropsOld6(set7, 2);
        testProjectilesOld6(set7, 1);
        testFuelRegistries7(set7, 1);
        testContainers7(set7, 1);
    }

    static void testFuelRegistries7(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.getNumFuelRegistries());

        CustomFuelRegistry registry1 = set.getFuelRegistryByName("registry1");
        assertEquals("registry1", registry1.getName());
        Iterator<FuelEntry> entryIterator = registry1.getEntries().iterator();
        FuelEntry entry1 = entryIterator.next();
        FuelEntry entry2 = entryIterator.next();
        assertFalse(entryIterator.hasNext());

        assertEquals(100, entry1.getBurnTime());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.COAL, (byte) 1, null), entry1.getFuel());
        assertEquals(500, entry2.getBurnTime());
        assertEquals(new CustomIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), entry2.getFuel());
    }

    @SuppressWarnings("deprecation")
    static void testContainers7(ItemSet set, int numContainers) {
        assertEquals(numContainers, set.getNumContainers());

        CustomContainer container1 = set.getContainers().iterator().next();
        assertEquals("container1", container1.getName());
        assertEquals(new SlotDisplay(
                new CustomItemDisplayItem(set.getCustomItemByName("simple2")),
                "First Container", new String[] { "Just", "some", "lore" }, 3
        ), container1.getSelectionIcon());
        assertEquals(FuelMode.ANY, container1.getFuelMode());
        assertEquals(VanillaContainerType.ENCHANTING_TABLE, container1.getVanillaType());
        assertFalse(container1.hasPersistentStorage());

        assertEquals(2, container1.getHeight());
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 2; y++) {
                CustomSlot slot = container1.getSlot(x, y);
                if (x == 0 && y == 0) {
                    assertEquals("input1", ((InputCustomSlot) slot).getName());
                } else if (x == 1 && y == 1) {
                    assertEquals("input2", ((InputCustomSlot) slot).getName());
                } else if (x == 2 && y == 0) {
                    assertEquals(new DecorationCustomSlot(new SlotDisplay(
                            new CustomItemDisplayItem(set.getCustomItemByName("wand_one")),
                            "Wand Show-off", new String[0], 1
                    )), slot);
                } else if (x == 3 && y == 1) {
                    assertEquals(new FuelCustomSlot(
                            "fuel1", set.getFuelRegistryByName("registry1"), null), slot);
                } else if (x == 4 && y == 0) {
                    assertEquals(new FuelIndicatorCustomSlot("fuel1", new SlotDisplay(
                            new SimpleVanillaDisplayItem(CIMaterial.LAVA_BUCKET), "", new String[0], 1
                    ), new SlotDisplay(
                            new SimpleVanillaDisplayItem(CIMaterial.BUCKET), "", new String[0], 1
                    ), new IndicatorDomain()), slot);
                } else if (x == 5 && y == 1) {
                    assertEquals(new OutputCustomSlot("output1", null), slot);
                } else if (x == 6 && y == 1) {
                    assertEquals(new OutputCustomSlot("output2", null), slot);
                } else if (x == 6) {
                    assertEquals(new ProgressIndicatorCustomSlot(new SlotDisplay(
                            new SimpleVanillaDisplayItem(CIMaterial.GOLD_AXE), "", new String[0], 1
                    ), new SlotDisplay(
                            new SimpleVanillaDisplayItem(CIMaterial.WOOD_AXE), "", new String[0], 1
                    ), new IndicatorDomain()), slot);
                } else {
                    assertTrue(slot instanceof EmptyCustomSlot);
                }
            }
        }

        assertEquals(1, container1.getRecipes().size());
        ContainerRecipe recipe = container1.getRecipes().iterator().next();
        assertEquals(60, recipe.getDuration());
        assertEquals(8, recipe.getExperience());

        assertEquals(2, recipe.getInputs().size());
        assertTrue(recipe.getInputs().contains(new ContainerRecipe.InputEntry(
                "input1", new SimpleVanillaIngredient(CIMaterial.WOOD, (byte) 1, null)
        )));
        assertTrue(recipe.getInputs().contains(new ContainerRecipe.InputEntry(
                "input2", new SimpleVanillaIngredient(CIMaterial.COBBLESTONE, (byte) 1, null)
        )));

        assertEquals(2, recipe.getOutputs().size());

        OutputTable outputTable1 = new OutputTable();
        outputTable1.getEntries().add(new OutputTable.Entry(set.getCustomItemByName("simple1").create(2), 100));
        assertTrue(recipe.getOutputs().contains(new ContainerRecipe.OutputEntry(
                "output1", outputTable1
        )));

        OutputTable outputTable2 = new OutputTable();
        ItemStack stack2 = ItemHelper.createStack(CIMaterial.LOG.name(), 1);
        MaterialData data = stack2.getData();
        data.setData((byte) 3);
        stack2.setData(data);
        stack2.setDurability(data.getData());
        outputTable2.getEntries().add(new OutputTable.Entry(stack2, 100));
        assertTrue(recipe.getOutputs().contains(new ContainerRecipe.OutputEntry(
                "output2", outputTable2
        )));
    }

    static void testBaseDefault7(CustomItem item) {
        // TODO Call testBaseDefault8
    }

    static void testSimpleDefault7(SimpleCustomItem item) {
        testBaseDefault7(item);
        // TODO Call testSimpleDefault8
    }

    static void testToolDefault7(CustomTool item) {
        testBaseDefault7(item);
        // TODO Call testToolDefault8
    }

    static void testArmorDefault7(CustomArmor item) {
        testToolDefault7(item);
        // TODO Call testArmorDefault8
    }

    static void testHoeDefault7(CustomHoe item) {
        testToolDefault7(item);
        // TODO Call testHoeDefault8
    }

    static void testShearsDefault7(CustomShears item) {
        testToolDefault7(item);
        // TODO Call testShearsDefault8
    }

    static void testBowDefault7(CustomBow item) {
        testToolDefault7(item);
        // TODO Call testBowDefault8
    }
}
