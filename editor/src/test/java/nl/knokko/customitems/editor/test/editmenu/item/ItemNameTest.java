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

import nl.knokko.gui.testing.GuiTestHelper;

public class ItemNameTest {
	
	/**
	 * Tests the functionality of the item name edit field and tests if the restrictions are applied
	 * correctly. This test should be run from an item edit menu and should finish in that same item
	 * edit menu.
	 * @param test The test instance
	 * @param itemName The item name that item should get
	 */
	public static void test(GuiTestHelper test, String itemName) {
		String badName = itemName.replace('_', ' ');
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.clickNearest("", "Name: ", 3);
		test.type(badName);
		test.click("Create");
		test.assertComponentWithText("The _ character is the only special character that is allowed in names.");
		test.click(badName);
		test.backspace(badName.length());
		test.type(itemName);
	}
}