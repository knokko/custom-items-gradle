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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;

public class SelectSimpleVanillaItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<CIMaterial> receiver;
	private final List list;
	
	private final TextEditField filterField;
	
	private final boolean addNoneButton;

	public SelectSimpleVanillaItem(GuiComponent returnMenu, Consumer<CIMaterial> receiver, boolean addNoneButton) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.addNoneButton = addNoneButton;
		this.list = new List();
		
		filterField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
	}
	
	@Override
	public void init() {
		super.init();
		filterField.setFocus();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.1f, 0.525f, 0.25f, 0.625f);
		addComponent(filterField, 0.1f, 0.4f, 0.25f, 0.5f);
		addComponent(list, 0.35f, 0f, 1f, 1f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/simple%20vanilla.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void keyPressed(int key) {
		String prev = filterField.getText();
		super.keyPressed(key);
		String next = filterField.getText();
		if (!prev.equals(next)) {
			list.refreshMaterials();
		}
	}
	
	@Override
	public void keyPressed(char key) {
		String prev = filterField.getText();
		super.keyPressed(key);
		String next = filterField.getText();
		if (!prev.equals(next)) {
			list.refreshMaterials();
		}
	}
	
	private class List extends GuiMenu {
		
		private ArrayList<DynamicTextButton> buttons;
		private DynamicTextButton none;
		
		private List() {
			CIMaterial[] materials = CIMaterial.values();
			Arrays.sort(materials, Comparator.comparing(Enum::name));
			buttons = new ArrayList<>(materials.length);
			for (CIMaterial material : materials) {
				buttons.add(new DynamicTextButton(material.toString(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
					receiver.accept(material);
					state.getWindow().setMainComponent(returnMenu);
				}));
			}
			if (addNoneButton) {
				none = new DynamicTextButton("None", EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
					receiver.accept(null);
					state.getWindow().setMainComponent(returnMenu);
				});
			} else {
				none = null;
			}
		}
		
		private void addNoneButton() {
			if (addNoneButton) {
				addComponent(none, 0f, 0.9f, 1f, 1f);
			}
		}
		
		private void addMaterials() {
			int index = addNoneButton ? 1 : 0;
			for (DynamicTextButton button : buttons) {
				if (button.getText().toLowerCase(Locale.ROOT).contains(filterField.getText().toLowerCase(Locale.ROOT))) {
					addComponent(button, 0f, 0.9f - index * 0.1f, Math.min(1f, button.getText().length() * 0.05f), 1f - index * 0.1f);
					index++;
				}
			}
		}
		
		private void refreshMaterials() {
			clearComponents();
			addNoneButton();
			addMaterials();
		}

		@Override
		protected void addComponents() {
			setBaseScrollSpeed(13f);
			addNoneButton();
			addMaterials();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
}