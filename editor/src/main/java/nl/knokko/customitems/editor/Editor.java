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
package nl.knokko.customitems.editor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import nl.knokko.customitems.editor.SystemTests.SystemTestResult;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.gui.window.AWTGuiWindow;
import nl.knokko.gui.window.GuiWindow;

public class Editor {
	
	private static GuiWindow window;
	
	public static GuiWindow getWindow() {
		return window;
	}

	public static void main(String[] args) {
		EditorFileManager.startLogging();

		window = new AWTGuiWindow();

		SystemTestResult systemTestResult = SystemTests.performTests();
		if (systemTestResult == SystemTestResult.SUCCESS) {
			System.out.println("All system tests succeeded");
			window.setMainComponent(MainMenu.INSTANCE);
		} else {
			System.err.println("The system tests failed: " + systemTestResult);
			window.setMainComponent(new SystemTestFailureMenu(systemTestResult, 1));
		}

		BufferedImage icon = null;
		String version = "?";
		try {
			InputStream iconStream = Editor.class.getClassLoader().getResourceAsStream("nl/knokko/customitems/editor/icon.png");
			if (iconStream != null) {
				icon = ImageIO.read(iconStream);
				iconStream.close();
			} else {
				System.err.println("Couldn't find Editor icon");
			}

			InputStream versionStream = Editor.class.getClassLoader().getResourceAsStream("plugin.yml");
			if (versionStream != null) {
				Scanner versionScanner = new Scanner(versionStream);
				while (versionScanner.hasNextLine()) {
					String currentLine = versionScanner.nextLine();
					if (currentLine.startsWith("version: ")) {
						version = currentLine.substring("version: ".length()).replaceAll("\"", "");
					}
				}
				versionScanner.close();
			} else {
				System.out.println("Couldn't determine Editor version");
			}
		} catch (IOException io) {
			System.err.println("Failed to find Editor icon and/or version:");
			io.printStackTrace();
		}
		window.open("Custom Items Editor " + version, true, icon);
		window.run(30);
	}
}