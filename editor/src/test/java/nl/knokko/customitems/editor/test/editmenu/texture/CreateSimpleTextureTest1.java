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
package nl.knokko.customitems.editor.test.editmenu.texture;

import java.awt.image.BufferedImage;

import nl.knokko.gui.testing.GuiTestHelper;

public class CreateSimpleTextureTest1 {

	/**
	 * A test to check if it is possible to load simple textures. A quicker test will be run after saving and
	 * loading to check if saving went well. This test should be run from the texture overview and it will 
	 * end at the edit menu.
	 * @param test
	 */
	public static void test(GuiTestHelper test, String textureName) {
		test.click("Load texture");
		test.click("Load simple texture");
		test.click("Create");
		test.assertComponentWithText("You have to select an image before you can create this.");
		test.click("Edit...");
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.click("Edit...");
		test.click("autotest00.png");
		test.click("Select");
		test.assertComponentWithText("The width and height (30) should be a power of 2");
		test.click("Create");
		test.assertComponentWithText("You have to select an image before you can create this.");
		BufferedImage image5 = test.createImage("autotest5.png", 64, 64);
		test.click("Edit...");
		test.click("autotest5.png");
		test.click("Select");
		test.assertImageShown(image5);
		test.backspace(9);
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.click("");
		test.type(textureName);
		test.click("Create");
		test.assertComponentsWithTexts("Back", "Load texture", "Edit", "Delete", textureName);
		test.clickNearest("Edit", textureName, 2);
		test.assertImageShown(image5);
		test.type("_one");
		test.assertComponentWithText(textureName + "_one");
		test.click("Apply");
		test.assertComponentWithText(textureName + "_one");
		test.clickNearest("Edit", textureName + "_one", 2);
		test.assertComponentWithText("Edit...");
		test.assertComponentWithText(textureName + "_one");
		test.click("Cancel");
		test.click("Back");
	}
}