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
package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.result.*;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class ChooseIngredient extends GuiMenu {
	
	private final Consumer<IngredientValues> listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	private final boolean allowEmpty;

	public ChooseIngredient(GuiComponent returnMenu, Consumer<IngredientValues> listener, boolean allowEmpty, ItemSet set) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.allowEmpty = allowEmpty;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.7f, 0.15f, 0.8f);

		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);

		ResultValues[] pRemaining = {null};
		addComponent(new DynamicTextComponent("Remaining item:", EditProps.LABEL),
				0.15f, 0.15f, 0.34f, 0.25f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseResult(
					this, newRemainingItem -> pRemaining[0] = newRemainingItem, set
			));
		}), 0.35f, 0.15f, 0.45f, 0.25f);
		addComponent(new DynamicTextButton("Clear", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			pRemaining[0] = null;
		}), 0.475f, 0.15f, 0.575f, 0.25f);


		addComponent(
				new DynamicTextComponent("Custom item...", LABEL),
				0.175f, 0.8f, 0.3f, 0.9f
		);
		addComponent(new DynamicTextButton("from this plug-in", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseCustomResult(returnMenu, customResult -> {
				listener.accept(CustomItemIngredientValues.createQuick(
						customResult.getItemReference(), customResult.getAmount(), pRemaining[0]
				));
				state.getWindow().setMainComponent(returnMenu);
			}, set));
		}), 0.175f, 0.65f, 0.3f, 0.75f);
		addComponent(new DynamicTextButton("from another plug-in with Mimic integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseMimicResult(returnMenu, mimicResult -> {
				listener.accept(MimicIngredientValues.createQuick(
						mimicResult.getItemId(), mimicResult.getAmount(), pRemaining[0]
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.175f, 0.5f, 0.5f, 0.6f);
		addComponent(new DynamicTextButton("from another plug-in with ItemBridge integration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseItemBridgeResult(returnMenu, itemBridgeResult -> {
				listener.accept(ItemBridgeIngredientValues.createQuick(
						itemBridgeResult.getItemId(), itemBridgeResult.getAmount(), pRemaining[0]
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.175f, 0.35f, 0.525f, 0.45f);

		addComponent(new DynamicTextComponent("Vanilla item...", LABEL), 0.55f, 0.8f, 0.7f, 0.9f);
		addComponent(new DynamicTextButton("simple", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseSimpleVanillaResult(returnMenu, vanillaResult -> {
				listener.accept(SimpleVanillaIngredientValues.createQuick(
						vanillaResult.getMaterial(), vanillaResult.getAmount(), pRemaining[0]
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.55f, 0.65f, 0.65f, 0.75f);
		addComponent(new DynamicTextButton("with data value", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseDataVanillaResult(returnMenu, true, vanillaResult -> {
				listener.accept(DataVanillaIngredientValues.createQuick(
						vanillaResult.getMaterial(), vanillaResult.getDataValue(), vanillaResult.getAmount(), pRemaining[0]
				));
				state.getWindow().setMainComponent(returnMenu);
			}));
		}), 0.55f, 0.5f, 0.75f, 0.6f);

		if (allowEmpty) {
			addComponent(new DynamicTextButton("Empty", EditProps.BUTTON, EditProps.HOVER, () -> {
				if (pRemaining[0] == null) {
					listener.accept(new NoIngredientValues());
					state.getWindow().setMainComponent(returnMenu);
				} else {
					errorComponent.setText("You can't have a remaining item when selecting Empty");
				}
			}), 0.775f, 0.8f, 0.9f, 0.9f);
		}

		HelpButtons.addHelpLink(this, "edit menu/recipes/choose ingredient.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}