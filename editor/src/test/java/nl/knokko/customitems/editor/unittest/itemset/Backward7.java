package nl.knokko.customitems.editor.unittest.itemset;

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
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.*;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.set.recipe.result.DataVanillaResult;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.OutputTable;
import org.junit.Test;

import java.util.Iterator;

import static nl.knokko.customitems.editor.unittest.itemset.Backward3.testTextures3;
import static nl.knokko.customitems.editor.unittest.itemset.Backward6.*;
import static nl.knokko.customitems.editor.unittest.itemset.Backward8.*;
import static nl.knokko.customitems.editor.unittest.itemset.BackwardHelper.loadItemSet;
import static org.junit.Assert.*;

public class Backward7 {

    @Test
    public void testBackwardCompatibility7() {
        ItemSet set7 = loadItemSet("backward7");
        testTextures3(set7, 3);
        testItemsOld6(set7, 21);
        testRecipesOld6(set7, 3);
        testBlockDropsOld6(set7, 1);
        testMobDropsOld6(set7, 2);
        testProjectileCoversOld6(set7, 2);
        testProjectilesOld6(set7, 1);
        testFuelRegistries7(set7, 1);
        testContainers7(set7, 1);
    }

    static void testFuelRegistries7(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.getBackingFuelRegistries().size());

        CustomFuelRegistry registry1 = set.getFuelRegistryByName("registry1");
        assertEquals("registry1", registry1.getName());
        Iterator<FuelEntry> entryIterator = registry1.getEntries().iterator();
        FuelEntry entry1 = entryIterator.next();
        FuelEntry entry2 = entryIterator.next();
        assertFalse(entryIterator.hasNext());

        assertEquals(100, entry1.getBurnTime());
        assertEquals(new SimpleVanillaIngredient(CIMaterial.COAL, (byte) 1, null), entry1.getFuel());
        assertEquals(500, entry2.getBurnTime());
        assertEquals(new CustomItemIngredient(set.getCustomItemByName("simple1"), (byte) 1, null), entry2.getFuel());
    }

    static void testContainers7(ItemSet set, int numContainers) {
        assertEquals(numContainers, set.getBackingContainers().size());

        CustomContainer container1 = set.getContainerByName("container1");
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
        outputTable1.getEntries().add(new OutputTable.Entry(new CustomItemResult(set.getCustomItemByName("simple1"), (byte) 2), 100));
        assertTrue(recipe.getOutputs().contains(new ContainerRecipe.OutputEntry(
                "output1", outputTable1
        )));

        OutputTable outputTable2 = new OutputTable();
        outputTable2.getEntries().add(new OutputTable.Entry(new DataVanillaResult(CIMaterial.LOG, (byte) 3, (byte) 1), 100));
        assertTrue(recipe.getOutputs().contains(new ContainerRecipe.OutputEntry(
                "output2", outputTable2
        )));
    }

    static void testBaseDefault7(CustomItem item) {
        testBaseDefault8(item);
    }

    static void testSimpleDefault7(SimpleCustomItem item) {
        testBaseDefault7(item);
        testSimpleDefault8(item);
    }

    static void testToolDefault7(CustomTool item) {
        testBaseDefault7(item);
        testToolDefault8(item);
    }

    static void testArmorDefault7(CustomArmor item) {
        testToolDefault7(item);
        testArmorDefault8(item);
    }

    static void testHoeDefault7(CustomHoe item) {
        testToolDefault7(item);
        testHoeDefault8(item);
    }

    static void testShearsDefault7(CustomShears item) {
        testToolDefault7(item);
        testShearsDefault8(item);
    }

    static void testBowDefault7(CustomBow item) {
        testToolDefault7(item);
        testBowDefault8(item);
    }
}
