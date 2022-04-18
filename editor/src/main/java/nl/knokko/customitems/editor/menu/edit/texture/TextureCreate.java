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
package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.BowTextureValues;
import nl.knokko.customitems.texture.CrossbowTextureValues;
import nl.knokko.customitems.texture.animated.AnimatedTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class TextureCreate extends GuiMenu {
	
	protected final EditMenu menu;

	public TextureCreate(EditMenu menu) {
		this.menu = menu;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getTextureOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new DynamicTextButton("Load simple texture", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextureEdit(menu, null, new BaseTextureValues(true)));
		}), 0.5f, 0.6f, 0.75f, 0.7f);
		addComponent(new DynamicTextButton("Load bow texture", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new BowTextureEdit(menu, null, new BowTextureValues(true)));
		}), 0.5f, 0.45f, 0.75f, 0.55f);
		addComponent(new DynamicTextButton("Load crossbow texture", BUTTON, EditProps.HOVER, () -> {
		    state.getWindow().setMainComponent(new CrossbowTextureEdit(menu, null, new CrossbowTextureValues(true)));
		}), 0.5f, 0.3f, 0.8f, 0.4f);
		addComponent(new DynamicTextButton("Load animated texture", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new AnimatedTextureEdit(menu, null, new AnimatedTextureValues(true)));
		}), 0.5f, 0.15f, 0.8f, 0.25f);

		HelpButtons.addHelpLink(this, "edit menu/textures/type selection.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}