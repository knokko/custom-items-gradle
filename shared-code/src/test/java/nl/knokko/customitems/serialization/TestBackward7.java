package nl.knokko.customitems.serialization;

import nl.knokko.customitems.container.*;
import nl.knokko.customitems.container.fuel.*;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.CustomItemIngredientValues;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredientValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.DataVanillaResultValues;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static nl.knokko.customitems.serialization.TestBackward3.testTextures3;
import static nl.knokko.customitems.serialization.TestBackward6.*;
import static nl.knokko.customitems.serialization.BackwardHelper.listOf;
import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBackward7 {

    @Test
    public void testBackwardCompatibility7() {
        for (ItemSet set7 : loadItemSet("backward7", false)) {
            testTextures3(set7, 3);
            testItemsOld6(set7, 21);
            testRecipesOld6(set7, 3);
            testBlockDropsOld6(set7, 1, false);
            testMobDropsOld6(set7, 2);
            testProjectileCoversOld6(set7, 2);
            testProjectilesOld6(set7, 1);
            testFuelRegistries7(set7, 1);
            testContainers7(set7, 1);
        }
    }

    static void testFuelRegistries7(ItemSet set, int numFuelRegistries) {
        assertEquals(numFuelRegistries, set.getFuelRegistries().size());

        FuelRegistryValues registry1 = set.getFuelRegistry("registry1").get();
        assertEquals("registry1", registry1.getName());
        assertEquals(2, registry1.getEntries().size());
        FuelEntryValues entry1 = registry1.getEntries().get(0);
        FuelEntryValues entry2 = registry1.getEntries().get(1);

        assertEquals(100, entry1.getBurnTime());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.COAL, 1), entry1.getFuel());
        assertEquals(500, entry2.getBurnTime());
        assertEquals(CustomItemIngredientValues.createQuick(set.getItemReference("simple1"), 1), entry2.getFuel());
    }

    static void testContainers7(ItemSet set, int numContainers) {
        assertEquals(numContainers, set.getContainers().size());

        CustomContainerValues container1 = set.getContainer("container1").get();
        assertEquals("container1", container1.getName());
        assertEquals(SlotDisplayValues.createQuick(
            CustomDisplayItemValues.createQuick(set.getItemReference("simple2")),
                "First Container", listOf("Just", "some", "lore"), 3
        ), container1.getSelectionIcon());
        assertEquals(FuelMode.ANY, container1.getFuelMode());
        assertEquals(new CustomContainerHost(VanillaContainerType.ENCHANTING_TABLE), container1.getHost());
        assertEquals(ContainerStorageMode.NOT_PERSISTENT, container1.getStorageMode());

        assertEquals(2, container1.getHeight());
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 2; y++) {
                ContainerSlotValues slot = container1.getSlot(x, y);
                if (x == 0 && y == 0) {
                    assertEquals("input1", ((InputSlotValues) slot).getName());
                } else if (x == 1 && y == 1) {
                    assertEquals("input2", ((InputSlotValues) slot).getName());
                } else if (x == 2 && y == 0) {
                    assertEquals(DecorationSlotValues.createQuick(SlotDisplayValues.createQuick(
                            CustomDisplayItemValues.createQuick(set.getItemReference("wand_one")),
                            "Wand Show-off", new ArrayList<>(0), 1
                    )), slot);
                } else if (x == 3 && y == 1) {
                    assertEquals(FuelSlotValues.createQuick(
                            "fuel1", set.getFuelRegistryReference("registry1"), null), slot);
                } else if (x == 4 && y == 0) {
                    assertEquals(FuelIndicatorSlotValues.createQuick("fuel1", SlotDisplayValues.createQuick(
                            SimpleVanillaDisplayItemValues.createQuick(CIMaterial.LAVA_BUCKET),
                            "", new ArrayList<>(0), 1
                    ), SlotDisplayValues.createQuick(
                            SimpleVanillaDisplayItemValues.createQuick(CIMaterial.BUCKET), "", new ArrayList<>(0), 1
                    ), new IndicatorDomain()), slot);
                } else if (x == 5 && y == 1) {
                    assertEquals(OutputSlotValues.createQuick("output1", null), slot);
                } else if (x == 6 && y == 1) {
                    assertEquals(OutputSlotValues.createQuick("output2", null), slot);
                } else if (x == 6) {
                    assertEquals(ProgressIndicatorSlotValues.createQuick(SlotDisplayValues.createQuick(
                            SimpleVanillaDisplayItemValues.createQuick(CIMaterial.GOLD_AXE), "", new ArrayList<>(0), 1
                    ), SlotDisplayValues.createQuick(
                            SimpleVanillaDisplayItemValues.createQuick(CIMaterial.WOOD_AXE), "", new ArrayList<>(0), 1
                    ), new IndicatorDomain()), slot);
                } else {
                    assertTrue(slot instanceof EmptySlotValues);
                }
            }
        }

        assertEquals(1, container1.getRecipes().size());
        ContainerRecipeValues recipe = container1.getRecipes().get(0);
        assertEquals(60, recipe.getDuration());
        assertEquals(8, recipe.getExperience());

        assertEquals(2, recipe.getInputs().size());
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.WOOD, 1), recipe.getInputs().get("input1"));
        assertEquals(SimpleVanillaIngredientValues.createQuick(CIMaterial.COBBLESTONE, 1), recipe.getInputs().get("input2"));

        assertEquals(2, recipe.getOutputs().size());

        assertEquals(
                OutputTableValues.createQuick(
                        OutputTableValues.Entry.createQuick(
                                CustomItemResultValues.createQuick(set.getItemReference("simple1"), 2),
                                100)
                ),
                recipe.getOutputs().get("output1")
        );

        assertEquals(
                OutputTableValues.createQuick(
                        OutputTableValues.Entry.createQuick(
                                DataVanillaResultValues.createQuick(CIMaterial.LOG, 3, 1), 100
                        )
                ),
                recipe.getOutput("output2")
        );
    }

    static void testBaseDefault7(CustomItemValues item) {
        TestBackward8.testBaseDefault8(item);
    }

    static void testSimpleDefault7(SimpleCustomItemValues item) {
        testBaseDefault7(item);
        TestBackward8.testSimpleDefault8(item);
    }

    static void testToolDefault7(CustomToolValues item) {
        testBaseDefault7(item);
        TestBackward8.testToolDefault8(item);
    }

    static void testArmorDefault7(CustomArmorValues item) {
        testToolDefault7(item);
        TestBackward8.testArmorDefault8(item);
    }

    static void testHoeDefault7(CustomHoeValues item) {
        testToolDefault7(item);
        TestBackward8.testHoeDefault8(item);
    }

    static void testShearsDefault7(CustomShearsValues item) {
        testToolDefault7(item);
        TestBackward8.testShearsDefault8(item);
    }

    static void testBowDefault7(CustomBowValues item) {
        testToolDefault7(item);
        TestBackward8.testBowDefault8(item);
    }
}
