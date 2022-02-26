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
package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseResult extends GuiMenu {
	
	private final Consumer<ResultValues> listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	
	public ChooseResult(
			GuiComponent returnMenu, Consumer<ResultValues> listener, ItemSet set
	) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.7f, 0.15f, 0.8f);

		addComponent(
				new DynamicTextComponent("Custom item...", LABEL),
				0.175f, 0.8f, 0.3f, 0.9f
		);
		addComponent(new DynamicTextButton("from this plug-in", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCustomResult(returnMenu, listener::accept, set));
		}), 0.175f, 0.65f, 0.3f, 0.75f);
		addComponent(new DynamicTextButton("from another plug-in with Mimic integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseMimicResult(returnMenu, listener::accept));
		}), 0.175f, 0.5f, 0.5f, 0.6f);
		addComponent(new DynamicTextButton("from another plug-in with ItemBridge integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseItemBridgeResult(returnMenu, listener::accept));
		}), 0.175f, 0.35f, 0.525f, 0.45f);

		addComponent(new DynamicTextComponent("Vanilla item...", LABEL), 0.55f, 0.8f, 0.7f, 0.9f);
		addComponent(new DynamicTextButton("simple", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseSimpleVanillaResult(returnMenu, listener::accept));
		}), 0.55f, 0.65f, 0.65f, 0.75f);
		addComponent(new DynamicTextButton("with data value", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseDataVanillaResult(returnMenu, true, listener::accept));
		}), 0.55f, 0.5f, 0.75f, 0.6f);

		addComponent(new DynamicTextButton("Copied from server", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCopyResult(this, result -> {
				listener.accept(result);
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.775f, 0.8f, 0.9f, 0.9f);

		HelpButtons.addHelpLink(this, "edit menu/recipes/choose result.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}