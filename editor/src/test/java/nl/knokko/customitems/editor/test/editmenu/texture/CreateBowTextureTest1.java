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

public class CreateBowTextureTest1 {

	/**
	 * The first test for creating bow textures. Another less advanced test will be run after the item set 
	 * has been saved and load once. This test should be run from the edit menu. This test will stop at the 
	 * texture overview.
	 * @param test The test instance
	 * @param textureName The name of the bow texture to create
	 */
	public static void test(GuiTestHelper test, String textureName) {
		test.click("Textures");
		test.click("Load texture");
		test.click("Load bow texture");
		test.click("Create");
		test.assertComponentWithText("You need to give this bow a base texture.");
		
		test.createImage("autotest0.png", 16, 15);
		test.createImage("autotest00.png", 30, 30);
		BufferedImage image1 = test.createImage("autotest1.png", 32, 32);
		BufferedImage image2 = test.createImage("autotest2.png", 16, 16);
		BufferedImage image3 = test.createImage("autotest3.png", 64, 64);
		BufferedImage image4 = test.createImage("autotest4.png", 128, 128);
		
		test.clickNearest("Edit...", test.getComponentWithText("Base texture: "), 0, 4);
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.click("Create");
		test.assertComponentWithText("You need to give this bow a base texture.");
		test.clickNearest("Edit...", "Base texture: ", 4);
		test.click("autotest1.png");
		test.click("Select");
		test.assertNearestImage("Base texture: ", image1, 4);
		test.backspace(9);
		test.click("Create");
		test.assertComponentWithText("You can't leave the name empty.");
		test.click("");
		test.type(textureName);
		test.click("Create");
		test.assertComponentWithText("Pull 0.0 doesn't have a texture");
		test.clickNearest("Edit...", "0.0", 4);
		test.click("autotest0.png");
		test.click("Select");
		test.assertComponentWithText("The width (16) of this image should be equal to the height (15)");
		test.clickNearest("Edit...", "0.0", 4);
		test.click("autotest1.png");
		test.click("Select");
		test.assertNearestImage("0.0", image1, 5);
		test.click("Create");
		test.assertComponentWithText("Pull 0.65 doesn't have a texture");
		test.clickNearest("Edit...", "0.65", 4);
		test.click("autotest00.png");
		test.click("Select");
		test.assertComponentWithText("The width and height (30) should be a power of 2");
		test.clickNearest("Edit...", "0.65", 4);
		test.click("autotest2.png");
		test.click("Select");
		test.assertNearestImage("0.65", image2, 6);
		test.click("0.65");
		test.backspace(2);
		test.type("45");
		test.click("0.9");
		test.backspace(1);
		test.type('6');
		
		// For some reason, that extra delay is necessary
		test.assertComponentWithText("0.6");
		test.delay(1000);
		
		test.clickNearest("Edit...", "0.6", 4);
		test.click("autotest3.png");
		test.click("Select");
		test.assertNearestImage("0.6", image3, 7);
		test.click("Add pull");
		test.click("0.3");
		test.backspace(1);
		test.type("95");
		test.clickNearest("Edit...", "0.95", 5);
		test.click("autotest4.png");
		test.click("Select");
		test.assertNearestImage("0.95", image4, 9);
		test.click("Create");
		test.assertComponentsWithTexts("Back", "Load texture", textureName, "Edit", "Delete");
		test.click("Edit");
		test.assertComponentsWithTexts("Cancel", "Apply", "Add pull", "Name: ", "Base texture: ", "Edit...",
				textureName, "0.0", "0.45", "0.6", "0.95");
		test.assertNearestImage("Base texture: ", image1, 9);
		test.assertNearestImage("0.0", image1, 9);
		test.assertNearestImage("0.45", image2, 9);
		test.assertNearestImage("0.6", image3, 9);
		test.assertNearestImage("0.95", image4, 9);
		test.click("0.45");
		test.backspace(2);
		test.type("35");
		test.assertComponentWithText("0.35");
		test.click("Apply");
		test.click("Edit");
		test.assertComponentWithText("0.35");
		test.click("Cancel");
	}
}