package nl.knokko.customitems.editor.test.editmenu.item;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.gui.testing.GuiTestHelper;

public class CreateShearsTest {
	
	public static void create(GuiTestHelper test, String itemName, String textureName, 
			String maxUses, String repairItemCategory, String repairItem, 
			String attackDurLoss, String breakDurLoss, String shearDurLoss,
			String lore1, String lore2,
			String attribute1, String slot1, String op1, String value1, String attribute2, String slot2, String op2,
			String value2, String enchantment1, String level1, String enchantment2, String level2) {
		test.click("Create item");
		test.click("Shear");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Item flags: ", "Texture: ", "", "Change...",
				"None", "Allow enchanting", "Allow anvil actions", "Max uses: ", "Repair item: ",
				"Durability loss on attack:", "Durability loss on block break:", "Durability loss on shearing:",
				"500", "0", "1");
		ItemNameTest.test(test, itemName);
		SimpleTextureTest.test(test, textureName);
		DisplayNameTest.test(test, itemName, 3);
		LoreTest.test(test, lore1, lore2, 11);
		
		CustomItemType customItemType = CustomItemType.SHEARS;
		AttributeModTest.test(test, "generic.movementSpeed", "Offhand", "Add factor", "1.5", 
				attribute1, slot1, op1, value1, attribute2, slot2, op2, value2, 11);
		EnchantmentsTest.test(test, enchantment1, level1, enchantment2, level2, 11);
		CreateToolTest.toolOnly(test, customItemType, maxUses, repairItemCategory, repairItem, attackDurLoss, breakDurLoss, 8);
		
		// Shears only:
		test.clickNearestEdit("Durability loss on shearing:", 8);
		test.backspace(1);
		test.click("Create");
		test.assertComponentWithText("The shear durability loss must be a positive integer");
		test.clickNearestEdit("Durability loss on shearing:", 8);
		test.type(shearDurLoss);
		test.assertComponentWithText(shearDurLoss);
		
		// Now end like we always end
		test.click("Create");
		test.assertComponentsWithTexts("Create item", itemName);
	}
}