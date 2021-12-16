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

import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.TestException;

public class LoreTest {
	
	/**
	 * Tests the functionality of the lore sub menu. This method will test if you can add lines and
	 * remove lines and if the saving and positioning works correctly.
	 * @param test The test instance
	 * @param lore1 The first line of lore
	 * @param lore2 The second line of lore
	 */
	public static void test(GuiTestHelper test, String lore1, String lore2, int numChangeButtons) {
		test.clickNearest("Change...", "Lore: ", numChangeButtons);
		test.assertComponentsWithTexts("Cancel", "Add line", "Apply");
		test.click("Add line");
		test.click("Text...");
		test.backspace(7);
		test.type(lore1);
		
		// Appears necessary to delay briefly
		test.delay(80);
		test.click("Add line");
		test.click("Text...");
		test.backspace(7);
		test.type(lore2);
		test.clickNearestImage(lore1, TextListEditMenu.ADD_IMAGE, 4);
		test.delay(50);
		float y1 = test.getComponentWithText(lore1).getState().getMinY();
		if (test.getComponentWithText("").getState().getMinY() < y1) {
			throw new TestException("The new line should be higher");
		}
		test.clickNearestImage(lore1, TextListEditMenu.DELETE_IMAGE, 6);
		test.delay(50);
		if (test.getComponentWithText(lore2).getState().getMinY() != y1){
			throw new TestException("The minY of the second component should equal the previous minY of the first lore");
		}
		test.click("Apply");
		test.clickNearest("Change...", "Lore: ", numChangeButtons);
		test.assertComponentsWithTexts("Cancel", "Add line", "Apply", lore2, "");
		if (test.getComponentWithText(lore2).getState().getMinY() != y1){
			throw new TestException("The minY of the second component should not change after applying and coming back");
		}
		test.click("Cancel");
	}
}