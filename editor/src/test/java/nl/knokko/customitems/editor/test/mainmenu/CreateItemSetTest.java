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
package nl.knokko.customitems.editor.test.mainmenu;

import java.io.File;
import java.io.IOException;

import nl.knokko.customitems.editor.Editor;
import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.gui.testing.GuiTestHelper;
import nl.knokko.gui.testing.TestException;

public class CreateItemSetTest {

	/**
	 * A small test to create a new item set. If there is already an item set with the given name, it will be
	 * deleted first. It should be run from the main menu and ends in the main menu.
	 * @param test The test instance
	 * @param name The name of the item set to create
	 */
	public static void test(GuiTestHelper test, String name) {
		
		// If the file already exists, delete it!
		File maybe = new File(EditorFileManager.FOLDER + "/" + name + ".cisb");
		maybe.delete();
		
		// Also test that it forbids to create an item set with the same name as an existing item set
		try {
			new File(EditorFileManager.FOLDER + "/" + name + "full.cisb").createNewFile();
		} catch (IOException e) {
			throw new TestException("Failed to create a dumb test file");
		}
		
		test.click("New item set");
		
		test.type(name + "full");
		test.click("Create");
		
		test.click(name + "full");
		test.backspace(4);
		
		test.assertComponentWithText(name);
		test.click("Create");
		test.assertComponentWithText("Save and quit");
	}
}