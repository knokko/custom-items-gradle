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
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectDataVanillaItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectSimpleVanillaItem;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.DataVanillaResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.component.text.TextEditField;

import java.util.function.Consumer;

public class ChooseResult extends GuiMenu {
	
	private final Consumer<ResultValues> listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	
	private final DynamicTextComponent errorComponent;
	private final TextEditField amountField;
	
	private ResultValues current;
	private Consumer<Byte> setAmount;

	public ChooseResult(GuiComponent returnMenu, Consumer<ResultValues> listener,
			ItemSet set) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.set = set;
		errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		amountField = new TextEditField("1", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.2f, 0.3f, 0.35f, 0.4f);
		addComponent(new DynamicTextButton("Custom Item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (ItemReference item) -> {
				// Fix the amount with the Choose button
				current = CustomItemResultValues.createQuick(item, 1);
				setAmount = ((CustomItemResultValues)current)::setAmount;
			}, set));
		}), 0.6f, 0.7f, 0.8f, 0.8f);
		addComponent(new DynamicTextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(this, (CIMaterial material) -> {
				// Fix the amount with the Choose button
				current = SimpleVanillaResultValues.createQuick(material, 1);
				setAmount = ((SimpleVanillaResultValues)current)::setAmount;
			}, false));
		}), 0.6f, 0.55f, 0.8f, 0.65f);
		addComponent(new DynamicTextButton("Vanilla item with datavalue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(this, (CIMaterial material, byte data) -> {
				// Fix the amount with the Choose button
				current = DataVanillaResultValues.createQuick(material, data, 1);
				setAmount = ((DataVanillaResultValues)current)::setAmount;
			}));
		}), 0.6f, 0.4f, 0.8f, 0.5f);
		addComponent(new DynamicTextButton("Copy from server", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCopyResult(this, chosenResult -> {
				listener.accept(chosenResult);
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.6f, 0.25f, 0.8f, 0.35f);
		addComponent(new DynamicTextComponent("Amount: ", EditProps.LABEL), 0.4f, 0.1f, 0.55f, 0.2f);
		addComponent(amountField, 0.6f, 0.1f, 0.7f, 0.2f);
		addComponent(new ConditionalTextButton("Select", EditProps.BUTTON, EditProps.HOVER, () -> {
			try {
				int amount = Integer.parseInt(amountField.getText());
				if (amount > 0 && amount <= 64) {
					setAmount.accept((byte) amount);
					listener.accept(current);
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText("The amount must be between 1 and 64");
				}
			} catch (NumberFormatException nfe) {
				errorComponent.setText("The amount must be an integer.");
			}
		}, () -> current != null && setAmount != null
		), 0.2f, 0.1f, 0.35f, 0.2f);
		addComponent(errorComponent, 0.05f, 0.85f, 0.95f, 0.95f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/output%20type%20select.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}