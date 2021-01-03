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
package nl.knokko.customitems.editor.test.editmenu;

import nl.knokko.gui.testing.GuiTestHelper;

public class ExploreEditMenuTest {

	/**
	 * Checks if all menus and buttons are present in the edit menu. This test
	 * should be run from the edit menu.
	 * @param test The test instance
	 */
	public static void test(GuiTestHelper test) {

		// Edit menu buttons
		test.assertComponentsWithTexts("Quit", "Save", "Save and quit", "Export for 1.12", "Textures", 
				"Items", "Recipes", "Export for 1.13", "Export for 1.14");

		// Go to the texture menu and check the buttons there
		test.click("Textures");
		test.assertComponentsWithTexts("Back", "Load texture");

		// And go a little deeper...
		test.click("Load texture");
		test.assertComponentsWithTexts("Back", "Load simple texture", "Load bow texture");

		// Look quickly into loading simple textures
		test.click("Load simple texture");
		test.assertComponentsWithTexts("Cancel", "Create", "Edit...", "Name: ", "Texture: ", "");

		// Now look into loading bow textures
		test.click("Cancel");
		test.click("Load texture");
		test.click("Load bow texture");
		test.assertComponentsWithTexts("Cancel", "Create", "Add pull", "Edit...", "Base texture: ", "Name: ", "",
				"Pull: ", "Texture: ", "0.0", "0.65", "0.9");

		// Now look into the items menu
		test.click("Cancel");
		test.click("Back");
		test.click("Items");
		test.assertComponentsWithTexts("Back", "Create item");

		// Look a little into item creation
		test.click("Create item");
		test.assertComponentsWithTexts("Cancel", "Simple Item", "Sword", "Pickaxe", "Axe", "Shovel", "Hoe", "Shear",
				"Bow", "Helmet", "Chestplate", "Leggings", "Boots");
		
		// And look a little into an actual item creation
		test.click("Simple Item");
		test.assertComponentsWithTexts("Cancel", "Create", "Name: ", "Internal item type: ",
				"Display name: ", "Lore: ", "Attribute modifiers: ",
				"Default enchantments: ", "Texture: ", "", "Diamond hoe", "Change...", "None", 
				"Max stacksize:", "64");
		
		// Now look into recipes
		test.click("Cancel");
		test.click("Back");
		test.click("Recipes");
		test.assertComponentsWithTexts("Back", "Create shaped recipe", "Create shapeless recipe");
		
		// Look into shaped recipes
		test.click("Create shaped recipe");
		test.assertComponentsWithTexts("Cancel", "Create", "empty");
		
		// Look into simple ingredient modification
		test.click("empty");
		test.assertComponentsWithTexts("Back", "Change to", "empty", "Custom Item", 
				"Simple vanilla item", "Vanilla item with datavalue", "Empty");
		test.click("Custom Item");
		
		// It appears that this delay is necessary to make sure things are processed the right way
		test.delay(30);
		test.click("Cancel");
		test.click("empty");
		test.click("Simple vanilla item");
		test.assertComponentsWithTexts("Cancel", "Search:", "");
		test.click("Cancel");
		test.click("empty");
		test.click("Vanilla item with datavalue");
		test.assertComponentsWithTexts("Cancel", "Data value: ", "0", "Search:", "");
		test.click("Cancel");
		test.click("Cancel");
		
		// Also look into shapeless recipes
		test.click("Create shapeless recipe");
		test.assertComponentsWithTexts("Cancel", "Add ingredient", "Result");
		test.click("Add ingredient");
		test.assertComponentsWithTexts("Cancel", "Custom Item", "Simple vanilla item",
				"Vanilla item with datavalue");
		test.click("Cancel");
		test.click("Cancel");
		test.click("Back");
	}
}