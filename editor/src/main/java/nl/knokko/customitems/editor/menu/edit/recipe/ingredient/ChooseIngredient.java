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
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectDataVanillaItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectSimpleVanillaItem;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ChooseIngredient extends GuiMenu {
	
	private final IngredientListener listener;
	private final GuiComponent returnMenu;
	private final ItemSet set;
	private final boolean allowEmpty;

	public ChooseIngredient(GuiComponent returnMenu, IngredientListener listener, boolean allowEmpty, ItemSet set) {
		this.listener = listener;
		this.returnMenu = returnMenu;
		this.allowEmpty = allowEmpty;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.2f, 0.3f, 0.35f, 0.4f);
		addComponent(new DynamicTextButton("Custom Item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(returnMenu, (CustomItem item) -> {
				listener.set(new CustomItemIngredient(item));
				//the SelectCustomItem will go the the returnGui automatically
			}, set));
		}), 0.6f, 0.7f, 0.8f, 0.8f);
		addComponent(new DynamicTextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(returnMenu, (CIMaterial material) -> {
				listener.set(new SimpleVanillaIngredient(material));
				//the SelectSimpleVanillaItem will go to the returnGui automatically
			}, false));
		}), 0.6f, 0.55f, 0.8f, 0.65f);
		addComponent(new DynamicTextButton("Vanilla item with datavalue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(returnMenu, (CIMaterial material, byte data) -> {
				listener.set(new DataVanillaIngredient(material, data));
			}));
		}), 0.6f, 0.4f, 0.8f, 0.5f);
		if (allowEmpty) {
			addComponent(new DynamicTextButton("Empty", EditProps.BUTTON, EditProps.HOVER, () -> {
				listener.set(new NoIngredient());
				state.getWindow().setMainComponent(returnMenu);
			}), 0.6f, 0.25f, 0.8f, 0.35f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}