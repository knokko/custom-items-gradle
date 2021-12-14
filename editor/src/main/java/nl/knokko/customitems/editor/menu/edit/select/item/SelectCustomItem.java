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
package nl.knokko.customitems.editor.menu.edit.select.item;

import java.util.Locale;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class SelectCustomItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final SItemSet set;
	private final Consumer<ItemReference> onSelect;
	
	private final TextEditField searchField;
	private final ItemList itemList;

	public SelectCustomItem(GuiComponent returnMenu, Consumer<ItemReference> onSelect, SItemSet set) {
		this.returnMenu = returnMenu;
		this.onSelect = onSelect;
		this.set = set;
		
		this.searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.itemList = new ItemList();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.1f, 0.5f, 0.2f, 0.6f);
		addComponent(searchField, 0.1f, 0.4f, 0.25f, 0.5f);
		searchField.setFocus();
		
		addComponent(itemList, 0.35f, 0f, 1f, 1f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/custom.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void keyPressed(int key) {
		String prev = searchField.getText();
		super.keyPressed(key);
		String next = searchField.getText();
		if (!prev.equals(next)) {
			itemList.refresh();
		}
	}
	
	@Override
	public void keyPressed(char key) {
		String prev = searchField.getText();
		super.keyPressed(key);
		String next = searchField.getText();
		if (!prev.equals(next)) {
			itemList.refresh();
		}
	}
	
	private class ItemList extends GuiMenu {
		
		protected void refresh() {
			clearComponents();
			addComponents();
		}

		@Override
		protected void addComponents() {
			int index = 0;
			for (ItemReference item : set.getItems().references()) {
				String itemName = item.get().getName();
				if (itemName.toLowerCase(Locale.ROOT).contains(searchField.getText().toLowerCase(Locale.ROOT))) {
					addComponent(new DynamicTextButton(itemName, EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
						onSelect.accept(item);
						state.getWindow().setMainComponent(returnMenu);
					}), 0f, 0.9f - index * 0.1f, Math.min(1f, itemName.length() * 0.022f), 1f - index * 0.1f);
					index++;
				}
			}
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
}
