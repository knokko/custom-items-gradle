package nl.knokko.customitems.editor.test.editmenu.item;

import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;

public class EnchantmentsTest {
	
	public static void test(GuiTestHelper test, String enchantment1, String level1, 
			String enchantment2, String level2, int numChangeButtons) {
		test.clickNearest("Change...", "Default enchantments: ", numChangeButtons);
		test.assertComponentsWithTexts("Cancel", "Add new", "Apply");
		test.click("Add new");
		test.assertComponentsWithTexts("Durability", "Level: ", "2");
		BufferedImage deleteImage = (BufferedImage) test.getComponentWithText("Level: ").getState().getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete.png").getImage();
		test.assertImageShown(deleteImage);
		test.click("Durability");
		test.click(enchantment1);
		test.backspace(1);
		test.type(level1);
		test.delay(30);
		test.click("Add new");
		test.clickNearestImage("Add new", deleteImage, 2);
		test.assertComponentsWithTexts(enchantment1, level1);
		test.click("Add new");
		test.click("Durability");
		test.click(enchantment2);
		test.clickNearest("2", "Add new", level1.equals("2") ? 2 : 1);
		test.backspace(1);
		test.type(level2);
		test.assertComponentWithText(level2);
		test.click("Apply");
		test.clickNearest("Change...", "Default enchantments: ", numChangeButtons);
		test.assertComponentsWithTexts(enchantment1, level1, enchantment2, level2);
		test.click("Cancel");
	}
}