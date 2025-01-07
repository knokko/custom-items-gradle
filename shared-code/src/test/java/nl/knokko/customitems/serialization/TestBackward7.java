package nl.knokko.customitems.serialization;

import nl.knokko.customitems.container.*;
import nl.knokko.customitems.container.fuel.*;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.recipe.result.DataVanillaResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static nl.knokko.customitems.serialization.TestBackward1.testExportSettings1;
import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward6.*;
import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBackward7 {

    @Test
    public void testBackwardCompatibility7() {
        for (ItemSet set7 : loadItemSet("backward7", false, true)) {
            testExportSettings1(set7);
            testTextures3(set7, 3, true);
            testItemsOld6(set7, 21);
            testRecipesOld6(set7, 3);
            testBlockDropsOld6(set7, 1, false);
            testMobDropsOld6(set7, 2);
            testProjectileCoversOld6(set7, 2, true);
            testProjectilesOld6(set7, 1);
            testFuelRegistries7(set7, 1);
            testContainers7(set7, 1);
        }
    }

    static void testFuelRegistries7(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.fuelRegistries.size());

        ContainerFuelRegistry registry1 = set.fuelRegistries.get("registry1").get();
        assertEquals("registry1", registry1.getName());
        assertEquals(2, registry1.getEntries().size());
        ContainerFuelEntry entry1 = registry1.getEntries().get(0);
        ContainerFuelEntry entry2 = registry1.getEntries().get(1);

        assertEquals(100, entry1.getBurnTime());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.COAL, 1), entry1.getFuel());
        assertEquals(500, entry2.getBurnTime());
        assertEquals(CustomItemIngredient.createQuick(set.items.getReference("simple1"), 1), entry2.getFuel());
    }

    static void testContainers7(ItemSet set, int numContainers) {
        assertEquals(numContainers, set.containers.size());

        KciContainer container1 = set.containers.get("container1").get();
        assertEquals("container1", container1.getName());
        assertEquals(SlotDisplay.createQuick(
            CustomDisplayItem.createQuick(set.items.getReference("simple2")),
                "First Container", listOf("Just", "some", "lore"), 3
        ), container1.getSelectionIcon());
        assertEquals(FuelMode.ANY, container1.getFuelMode());
        assertEquals(new ContainerHost(VContainerType.ENCHANTING_TABLE), container1.getHost());
        assertEquals(ContainerStorageMode.NOT_PERSISTENT, container1.getStorageMode());

        assertEquals(2, container1.getHeight());
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 2; y++) {
                ContainerSlot slot = container1.getSlot(x, y);
                if (x == 0 && y == 0) {
                    assertEquals("input1", ((InputSlot) slot).getName());
                } else if (x == 1 && y == 1) {
                    assertEquals("input2", ((InputSlot) slot).getName());
                } else if (x == 2 && y == 0) {
                    assertEquals(DecorationSlot.createQuick(SlotDisplay.createQuick(
                            CustomDisplayItem.createQuick(set.items.getReference("wand_one")),
                            "Wand Show-off", new ArrayList<>(0), 1
                    )), slot);
                } else if (x == 3 && y == 1) {
                    assertEquals(FuelSlot.createQuick(
                            "fuel1", set.fuelRegistries.getReference("registry1"), null), slot);
                } else if (x == 4 && y == 0) {
                    assertEquals(FuelIndicatorSlot.createQuick("fuel1", SlotDisplay.createQuick(
                            SimpleVanillaDisplayItem.createQuick(VMaterial.LAVA_BUCKET),
                            "", new ArrayList<>(0), 1
                    ), SlotDisplay.createQuick(
                            SimpleVanillaDisplayItem.createQuick(VMaterial.BUCKET), "", new ArrayList<>(0), 1
                    ), new IndicatorDomain()), slot);
                } else if (x == 5 && y == 1) {
                    assertEquals(OutputSlot.createQuick("output1", null), slot);
                } else if (x == 6 && y == 1) {
                    assertEquals(OutputSlot.createQuick("output2", null), slot);
                } else if (x == 6) {
                    assertEquals(ProgressIndicatorSlot.createQuick(SlotDisplay.createQuick(
                            SimpleVanillaDisplayItem.createQuick(VMaterial.GOLD_AXE), "", new ArrayList<>(0), 1
                    ), SlotDisplay.createQuick(
                            SimpleVanillaDisplayItem.createQuick(VMaterial.WOOD_AXE), "", new ArrayList<>(0), 1
                    ), new IndicatorDomain()), slot);
                } else {
                    assertTrue(slot instanceof EmptySlot);
                }
            }
        }

        assertEquals(1, container1.getRecipes().size());
        ContainerRecipe recipe = container1.getRecipes().get(0);
        assertEquals(60, recipe.getDuration());
        assertEquals(8, recipe.getExperience());

        assertEquals(2, recipe.getInputs().size());
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.WOOD, 1), recipe.getInputs().get("input1"));
        assertEquals(SimpleVanillaIngredient.createQuick(VMaterial.COBBLESTONE, 1), recipe.getInputs().get("input2"));

        assertEquals(2, recipe.getOutputs().size());

        assertEquals(
                OutputTable.createQuick(
                        OutputTable.Entry.createQuick(
                                CustomItemResult.createQuick(set.items.getReference("simple1"), 2),
                                100)
                ),
                recipe.getOutputs().get("output1")
        );

        assertEquals(
                OutputTable.createQuick(
                        OutputTable.Entry.createQuick(
                                DataVanillaResult.createQuick(VMaterial.LOG, 3, 1), 100
                        )
                ),
                recipe.getOutput("output2")
        );
    }

    static void testBaseDefault7(KciItem item) {
        TestBackward8.testBaseDefault8(item);
    }

    static void testSimpleDefault7(KciSimpleItem item) {
        testBaseDefault7(item);
        TestBackward8.testSimpleDefault8(item);
    }

    static void testToolDefault7(KciTool item) {
        testBaseDefault7(item);
        TestBackward8.testToolDefault8(item);
    }

    static void testArmorDefault7(KciArmor item) {
        testToolDefault7(item);
        TestBackward8.testArmorDefault8(item);
    }

    static void testHoeDefault7(KciHoe item) {
        testToolDefault7(item);
        TestBackward8.testHoeDefault8(item);
    }

    static void testShearsDefault7(KciShears item) {
        testToolDefault7(item);
        TestBackward8.testShearsDefault8(item);
    }

    static void testBowDefault7(KciBow item) {
        testToolDefault7(item);
        TestBackward8.testBowDefault8(item);
    }
}
