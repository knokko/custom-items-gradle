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
package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.item.ItemFlag;
import nl.knokko.customitems.item.SimpleCustomItemValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.List;

public class ItemFlagMenu extends GuiMenu {

	private final GuiComponent returnMenu;
	private final CustomItemValues itemValues;
	private final List<Boolean> currentFlags;

	public ItemFlagMenu(GuiComponent returnMenu, CustomItemValues itemValues) {
		this.returnMenu = returnMenu;
		this.itemValues = itemValues;
		this.currentFlags = itemValues.getItemFlags();
	}

	@Override
	protected void addComponents() {
		ItemFlag[] allFlags = ItemFlag.values();
		for (int index = 0; index < allFlags.length; index++) {
			int rememberIndex = index;
			addComponent(
					new CheckboxComponent(currentFlags.get(index), newValue -> currentFlags.set(rememberIndex, newValue)),
					0.4f, 0.725f - 0.1f * index, 0.425f, 0.75f - 0.1f * index
			);
			addComponent(
					new DynamicTextComponent(allFlags[index].toString(), EditProps.LABEL),
					0.45f, 0.725f - 0.1f * index, 0.65f, 0.8f - 0.1f * index
			);
		}
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.9f, 0.25f, 0.97f);
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			itemValues.setItemFlags(currentFlags);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.13f, 0.25f, 0.2f);
		
		if (itemValues instanceof SimpleCustomItemValues) {
			addComponent(new DynamicTextComponent("Notice: it is recommended for simple custom items to keep the 'hide unbreakable' checked", EditProps.LABEL), 0.05f, 0.025f, 0.95f, 0.1f);
		}
		if (itemValues instanceof CustomToolValues && ((CustomToolValues) itemValues).getMaxDurabilityNew() != null) {
			addComponent(new DynamicTextComponent("Notice: it is recommended for breakable custom tools to keep the 'hide unbreakable' checked", EditProps.LABEL), 0.05f, 0.025f, 0.95f, 0.1f);
		}
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/flags.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}