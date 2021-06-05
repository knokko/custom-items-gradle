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

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicActivatableTextButton;

public class SelectDataVanillaItem extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	private final TextEditField dataField;
	private final TextEditField filterField;
	private final DynamicTextComponent errorComponent;
	private final List list;
	
	private CIMaterial selected;

	public SelectDataVanillaItem(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.dataField = new TextEditField("0", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.filterField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		this.list = new List();
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
		filterField.setFocus();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.1f, 0.75f, 0.25f, 0.85f);
		addComponent(new DynamicTextComponent("Data value: ", EditProps.LABEL), 0.1f, 0.55f, 0.25f, 0.65f);
		addComponent(dataField, 0.1f, 0.425f, 0.25f, 0.525f);
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.1f, 0.325f, 0.25f, 0.4f);
		addComponent(filterField, 0.1f, 0.2f, 0.25f, 0.3f);
		addComponent(errorComponent, 0.05f, 0.89f, 0.95f, 0.99f);
		addComponent(new ConditionalTextButton("OK", EditProps.BUTTON, EditProps.HOVER, () -> {
			try {
				int data = Integer.parseInt(dataField.getText());
				if (data >= 0 && data < 16) {
					receiver.onSelect(selected, (byte) data);
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText("The data value should be between 0 and 15");
				}
			} catch (NumberFormatException nfe) {
				errorComponent.setText("The data value should be an integer");
			}
		}, () -> {
			return selected != null;
		}), 0.1f, 0.05f, 0.2f, 0.15f);
		addComponent(list, 0.35f, 0f, 1f, 1f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/vanilla%20data.html");
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
		
		private List() {
			CIMaterial[] materials = CIMaterial.values();
			Arrays.sort(materials, Comparator.comparing(Enum::name));
			buttons = new ArrayList<>(materials.length);
			for (CIMaterial material : materials) {
			    if (material.firstVersion <= MCVersions.VERSION1_12) {
					buttons.add(new DynamicActivatableTextButton(
							material.toString(),
							EditProps.SELECT_BASE, EditProps.SELECT_HOVER, EditProps.SELECT_ACTIVE,
							() -> selected = material, () -> selected == material)
					);
				}
			}
		}
		
		private void addMaterials() {
			int index = 0;
			for (DynamicTextButton button : buttons) {
				if (button.getText().toLowerCase(Locale.ROOT).contains(filterField.getText().toLowerCase(Locale.ROOT))) {
					addComponent(button, 0f, 0.9f - index * 0.1f, Math.min(1f, button.getText().length() * 0.05f), 1f - index * 0.1f);
					index++;
				}
			}
		}
		
		private void refreshMaterials() {
			clearComponents();
			addMaterials();
		}

		@Override
		protected void addComponents() {
			setBaseScrollSpeed(13f);
			addMaterials();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
	}
	
	public static interface Receiver {
		
		void onSelect(CIMaterial material, byte data);
	}
}