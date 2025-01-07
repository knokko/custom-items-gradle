package nl.knokko.customitems.serialization;

import nl.knokko.customitems.itemset.ItemSet;
import org.junit.jupiter.api.Test;

import static nl.knokko.customitems.serialization.BackwardHelper.loadItemSet;
import static nl.knokko.customitems.serialization.TestBackward10.testRecipesNew10;
import static nl.knokko.customitems.serialization.TestBackward10.testTexturesOld10;
import static nl.knokko.customitems.serialization.TestBackward11.testEnergyTypesOld11;
import static nl.knokko.customitems.serialization.TestBackward11.testSoundsOld11;
import static nl.knokko.customitems.serialization.TestBackward12.*;
import static nl.knokko.customitems.serialization.TestBackward12.testFancyPantsTextures12;
import static nl.knokko.customitems.serialization.TestBackward13.*;
import static nl.knokko.customitems.serialization.TestBackward8.testArmorTexturesOld8;
import static nl.knokko.customitems.serialization.TestBackward8.testFuelRegistriesOld8;
import static nl.knokko.customitems.serialization.TestBackward9.testTexturesNew9;

public class TestBackward14 {

	@Test
	public void testBackwardCompatibility14() {
		ItemSet[] oldPair = loadItemSet("backward14old", true, false);
		for (ItemSet old13 : oldPair) {
			testExportSettings13Old(old13);
			testTexturesOld10(old13, 4, false);
			testArmorTexturesOld8(old13, 1, false);
			testItemsOld13(old13, 52);
			testEquipmentSetsOld12(old13, 2);
			testDamageSourcesOld12(old13, 2);
			testUpgradesOld12(old13, 1);
			testRecipesOld13(old13, 11);
			testBlockDropsOld13(old13, 5);
			testMobDropsOld13(old13, 4);
			testProjectileCoversOld13(old13, 4, false);
			testProjectilesOld13(old13, 5);
			testFuelRegistriesOld8(old13, 1);
			testContainersOld12(old13, 6);
			testEnergyTypesOld11(old13, 1);
			testSoundsOld11(old13, 1);
			testCombinedResourcepacksOld12(old13, 1, false);
		}

		ItemSet[] newPair = loadItemSet("backward14new", true, false);
		for (ItemSet newSet : newPair) {
			testTexturesNew9(newSet, 2, false);
			testItemsNew13(newSet, 10);
			testRecipesNew10(newSet, 2);
			testCookingRecipesNew13(newSet, 1);
			testContainersNew13(newSet, 2);
			testBlocksNew13(newSet, 5);
			testOreVeinsNew12(newSet, 2);
			testTreesNew12(newSet, 2);
		}

		ItemSet[] fancyPair = loadItemSet("backward14fancy", true, false);
		for (ItemSet fancySet : fancyPair) {
			testFancyPantsTextures12(fancySet, 2);
			testItemsFancy13(fancySet, 3);
			testSmithingRecipesFancy13(fancySet, 1);
			testTreesFancy13(fancySet, 1);
			testCombinedResourcepacksFancy13(fancySet, 1, false);
			testExportSettingsFancy13(fancySet);
		}
	}
}
