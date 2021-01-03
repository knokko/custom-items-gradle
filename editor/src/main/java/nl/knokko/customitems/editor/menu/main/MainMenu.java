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
package nl.knokko.customitems.editor.menu.main;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class MainMenu extends GuiMenu {
	
	public static final MainMenu INSTANCE = new MainMenu();
	
	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("New item set", EditProps.BUTTON, EditProps.HOVER, () ->  {
			state.getWindow().setMainComponent(CreateMenu.INSTANCE);
		}), 0.3f, 0.8f, 0.7f, 0.95f);
		addComponent(new DynamicTextButton("Edit item set", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(LoadMenu.INSTANCE);
		}), 0.3f, 0.6f, 0.7f, 0.75f);
		addComponent(new DynamicTextButton("Combine item sets", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(CombineMenu.getInstance());
		}), 0.3f, 0.4f, 0.7f, 0.55f);
		addComponent(new DynamicTextButton("Exit editor", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().stopRunning();
		}), 0.3f, 0.15f, 0.7f, 0.3f);

		addComponent(new DynamicTextComponent("For help, visit the discord server:", EditProps.LABEL), 0.05f, 0.7f, 0.25f, 0.75f);
		addComponent(new DynamicTextButton("Copy invite link", EditProps.BUTTON, EditProps.HOVER, () -> {
					CommandBlockHelpOverview.setClipboard("https://discordapp.com/invite/bmF3Zvu");
		}), 0.05f, 0.65f, 0.145f, 0.7f);
		addComponent(new DynamicTextButton("Open invite link", EditProps.BUTTON, EditProps.HOVER, () -> {
				URL url = null;
				try {
					url = new URL("https://discordapp.com/invite/bmF3Zvu");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				openWebpage(url);
		}), 0.155f, 0.65f, 0.25f, 0.7f);
		addComponent(new DynamicTextComponent("Or read the tutorial:", EditProps.LABEL), 0.05f, 0.59f, 0.18f, 0.64f);
		addComponent(new DynamicTextButton("Click here to open the tutorial", EditProps.BUTTON, EditProps.HOVER, () -> {
				URL url = null;
				try {
					url = new URL("https://knokko.github.io/custom%20items/tutorials/basic%20tools.html");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				openWebpage(url);
		}), 0.05f, 0.53f, 0.25f, 0.58f);
		
		HelpButtons.addHelpLink(this, "main%20menu/index.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static boolean openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return false;
	}

	public static boolean openWebpage(URL url) {
	    try {
	        return openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
}