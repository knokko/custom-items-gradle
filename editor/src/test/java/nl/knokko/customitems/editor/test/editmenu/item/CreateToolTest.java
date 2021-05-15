package nl.knokko.customitems.editor.test.editmenu.item;

import java.text.DecimalFormat;
import java.util.Locale;

import nl.knokko.customitems.item.CustomItemDamage;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomToolDurability;
import nl.knokko.gui.testing.GuiTestHelper;

public class CreateToolTest {
	
	/**
	 * The test for creating a custom tool. It should be started from the Item Overview and it will end in
	 * the Item Overview.
	 * @param test The test instance
	 * @param toolType The tool type of the item to create, it should match one of the buttons you can
	 * click after clicking on 'Create item'.
	 * @param itemName The name that should be given to the item to create
	 * @param textureName The name of the texture the item to create should get
	 * @param maxUses The maximum number of uses the item to create has
	 * @param repairItemCategory The category of the repair item. It should be one of the following:
	 * 'Custom Item', 'Simple vanilla item', 'Vanilla item with datavalue', 'Empty'
	 * @param repairItem The name of the repair item, it will be ignored if the repairItemCategory is empty
	 * @param attackDurLoss The amount of durability that the tool will lose upon attacking creatures
	 * @param breakDurLoss The amount of durability that the tool will lose upon breaking blocks
	 * @param lore1 The first line of lore of the tool to create
	 * @param lore2 The second line of lore of the tool to create
	 * @param attribute1 The first attribute modifier name of the tool to create
	 * @param slot1 The first attribute modifier slot of the tool to create
	 * @param op1 The first attribute modifier operation of the tool to create
	 * @param value1 The first attribute modifier value of the tool to create
	 * @param attribute2 The second attribute modifier name of the tool to create
	 * @param slot2 The second attribute modifier slot of the tool to create
	 * @param op2 The second attribute modifier operation of the tool to create
	 * @param value2 The second attribute modifier value of the tool to create
	 * @param enchantment1 The first enchantment name of the tool to create
	 * @param level1 The first enchantment level of the tool to create
	 * @param enchantment2 The second enchantment name of the tool to create
	 * @param level2 The second enchantment level of the tool to create
	 */
	public static void create(GuiTestHelper test, String toolType, String itemName, String textureName, 
			String maxUses, String repairItemCategory, String repairItem, String attackDurLoss, String breakDurLoss,
			String lore1, String lore2,
			String attribute1, String slot1, String op1, String value1, String attribute2, String slot2, String op2,
			String value2, String enchantment1, String level1, String enchantment2, String level2) {
		test.click("Create item");
		test.click(toolType);
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Item flags: ", "Texture: ", "", "Change...",
				"None", "Allow enchanting", "Allow anvil actions", "Max uses: ", "Repair item: ",
				"Durability loss on attack:", "Durability loss on block break:", "500", "2", "1");
		
		
		ItemNameTest.test(test, itemName);
		SimpleTextureTest.test(test, textureName);
		DisplayNameTest.test(test, itemName, 3);
		LoreTest.test(test, lore1, lore2, 11);
		
		// Not my most pretty solution ever, but will do the trick
		CustomItemType customItemType = CustomItemType.valueOf("IRON_" + toolType.toUpperCase(Locale.ROOT));
		AttributeModTest.test(test, "generic.attackDamage", "Mainhand", "Add", 
				new DecimalFormat("#.############").format(CustomItemDamage.getDefaultAttackDamage(customItemType)), 
				attribute1, slot1, op1, value1, attribute2, slot2, op2, value2, 11);
		EnchantmentsTest.test(test, enchantment1, level1, enchantment2, level2, 11);
		
		toolOnly(test, customItemType, maxUses, repairItemCategory, repairItem, attackDurLoss, breakDurLoss, 7);

		test.click("Create");
		test.assertComponentsWithTexts("Create item", itemName);
		test.assertComponentWithText("Create item");
	}
	
	public static void repairItem(GuiTestHelper test, String repairItemCategory, String repairItem) {
		test.assertComponentWithText("Repair item: ");
		test.click("None");
		test.assertComponentsWithTexts("Cancel", "Empty", "Custom Item",
				"Simple vanilla item", "Vanilla item with datavalue");
		test.click(repairItemCategory);
		if (!repairItemCategory.equals("Empty")) {
			test.click(repairItem);
			if (repairItemCategory.equals("Vanilla item with datavalue")) {
				test.click("OK");
			}
		}
		if (repairItemCategory.equals("Vanilla item with datavalue")) {
			
			// Exclude the version suffix, if there is one (for instance 1.12 to 1.16)
			int indexBracket = repairItem.indexOf(" (");
			if (indexBracket != -1) {
				test.assertComponentWithText(repairItem.substring(0, indexBracket) + "(0) x1");
			} else {
				test.assertComponentWithText(repairItem + "(0) x1");
			}
		} else if (repairItem != null){
			
			// Exclude the version suffix, if there is one (for instance 1.12 to 1.16)
			int indexBracket = repairItem.indexOf(" (");
			if (indexBracket != -1)
				test.assertComponentWithText(repairItem.substring(0, indexBracket) + " x1");
			else
				test.assertComponentWithText(repairItem + " x1");
		} else {
			test.assertComponentWithText("None");
		}
	}
	
	public static void attackDurabilityLoss(GuiTestHelper test, CustomItemType type, String attackDurLoss, int numberOfEditFields) {
		int defaultAttackDurLoss = CustomToolDurability.defaultEntityHitDurabilityLoss(type);
		test.clickNearestEdit("Durability loss on attack:", numberOfEditFields);
		test.backspace(Integer.toString(defaultAttackDurLoss).length());
		test.click("Create");
		test.assertComponentWithText("The entity hit durability loss should be a positive integer");
		test.clickNearest("", "Max uses: ", 2);
		test.type(attackDurLoss);
	}
	
	public static void breakDurabilityLoss(GuiTestHelper test, CustomItemType type, String breakDurLoss, int numberOfEditFields) {
		int defaultBreakDurLoss = CustomToolDurability.defaultBlockBreakDurabilityLoss(type);
		test.clickNearestEdit("Durability loss on block break:", numberOfEditFields);
		test.backspace(Integer.toString(defaultBreakDurLoss).length());
		test.click("Create");
		test.assertComponentWithText("The block break durability loss should be a positive integer");
		test.clickNearest("", "Max uses: ", 2);
		test.type(breakDurLoss);
	}
	
	public static void maxUses(GuiTestHelper test, String currentMaxUses, String newMaxUses) {
		test.click(currentMaxUses);
		
		// Better too much backspace than too little
		test.backspace(15);
		test.click("Create");
		test.assertComponentWithText("The durability must be an integer");
		test.clickNearest("", "Max uses: ", 2);
		test.type(newMaxUses);
	}
	
	public static void toolOnly(GuiTestHelper test, CustomItemType type, String maxUses, String repairItemCategory, 
			String repairItem, String attackDurLoss, String breakDurLoss, int numberOfEditFields) {
		test.uncheck("Allow enchanting", 2);
		test.uncheck("Allow anvil actions", 2);
		test.delay(30);
		test.check("Allow anvil actions", 2);
	
		maxUses(test, "500", maxUses);
		repairItem(test, repairItemCategory, repairItem);
		attackDurabilityLoss(test, type, attackDurLoss, numberOfEditFields);
		breakDurabilityLoss(test, type, breakDurLoss, numberOfEditFields);
	}
}