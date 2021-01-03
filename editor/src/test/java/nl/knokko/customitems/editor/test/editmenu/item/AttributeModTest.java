/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.test.editmenu.item;

import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;

public class AttributeModTest {
	
	/**
	 * Tests the functionality of the attribute modifiers submenu in an item edit menu. The attribute modifiers
	 * that should be set eventually are the parameters of this method.
	 * This test should be run from an item edit menu and should end in that same menu.
	 * @param test The test instance
	 * @param attribute1 The attribute name of the first attribute modifier
	 * @param slot1 The slot of the first attribute modifier
	 * @param op1 The operation of the first attribute modifier
	 * @param value1 The value (as string) of the first attribute modifier
	 * @param attribute2 The attribute name of the second attribute modifier
	 * @param slot2 The slot of the second attribute modifier
	 * @param op2 The operation of the second attribute modifier
	 * @param value2 The value (as string) of the second attribute modifier
	 */
	public static void test(GuiTestHelper test, 
			String defaultAttribute, String defaultSlot, String defaultOp, String defaultValue,
			String attribute1, String slot1, String op1, String value1,
			String attribute2, String slot2, String op2, String value2, int numChangeButtons) {
		test.clickNearest("Change...", "Attribute modifiers: ", numChangeButtons);
		test.assertComponentsWithTexts("Cancel", "Add new", "Apply");
		test.click("Add new");
		BufferedImage deleteImage = (BufferedImage) test.getComponentWithText(defaultSlot).getState().getWindow().getTextureLoader().loadTexture("nl/knokko/gui/images/icons/delete.png").getImage();
		test.assertImageShown(deleteImage);
		test.assertComponentsWithTexts(defaultAttribute, defaultSlot, defaultOp, "Value: ", defaultValue);
		test.click(defaultAttribute);
		test.click(attribute1);
		test.click(defaultSlot);
		test.delay(30);
		test.click(slot1);
		test.click(defaultOp);
		test.delay(30);
		test.click(op1);
		test.backspace(defaultValue.length());
		test.type(value1);
		test.click("Add new");
		
		// Click nearest to make sure we edit the right attribute
		test.clickNearest(defaultAttribute, "Add new", attribute1.equals(defaultAttribute) ? 2 : 1);
		test.click(attribute2);
		test.clickNearest(defaultSlot, "Add new", slot1.equals(defaultSlot) ? 2 : 1);
		test.delay(30);
		test.click(slot2);
		test.clickNearest(defaultOp, "Add new", op1.equals(defaultOp) ? 2 : 1);
		test.delay(30);
		test.click(op2);
		test.clickNearest(defaultValue, "Add new", value1.equals(defaultValue) ? 2 : 1);
		
		// Better too many backspaces than too few
		test.backspace(15);
		test.type(value2);
		test.assertComponentWithText(value2);
		test.click("Apply");
		test.clickNearest("Change...", "Attribute modifiers: ", numChangeButtons);
		test.assertComponentsWithTexts("Cancel", "Add new", "Apply", attribute1, slot1, op1, value1,
				attribute2, slot2, op2, value2);
		test.click(value2);
		test.backspace(1);
		test.type("1");
		String modifiedValue2 = value2.substring(0, value2.length() - 1) + "1";
		test.assertComponentWithText(modifiedValue2);
		test.click("Apply");
		test.clickNearest("Change...", "Attribute modifiers: ", numChangeButtons);
		test.assertComponentsWithTexts("Cancel", "Add new", "Apply");
		test.click(modifiedValue2);
		test.backspace(1);
		test.type(value2.charAt(value2.length() - 1));
		test.assertComponentWithText(value2);
		test.click("Apply");
	}
}