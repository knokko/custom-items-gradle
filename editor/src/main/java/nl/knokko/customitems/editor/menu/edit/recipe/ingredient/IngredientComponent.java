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
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class IngredientComponent extends DynamicTextButton {
	
	private Ingredient current;
	private final GuiComponent menu;
	private final String emptyText;
	private final ItemSet set;

	public IngredientComponent(String emptyText, Ingredient original, GuiComponent menu, ItemSet set) {
		super(original.toString(emptyText), EditProps.BUTTON, EditProps.HOVER, null);
		this.clickAction = () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(menu, this::setIngredient, true, set));
		};
		current = original;
		this.emptyText = emptyText;
		this.menu = menu;
		this.set = set;
	}

	public void setIngredient(Ingredient ingredient) {
		current = ingredient;
		setText(current.toString(emptyText));
	}
	
	public Ingredient getIngredient() {
		return current;
	}
	
	public GuiComponent getMenu() {
		return menu;
	}
	
	@Override
	public void keyPressed(char character) {
		if (state.isMouseOver()) {
			if (character == 'v') {
				state.getWindow().setMainComponent(new SelectSimpleVanillaItem(getMenu(), (CIMaterial material) -> {
					IngredientComponent.this.setIngredient(new SimpleVanillaIngredient(material, (byte) 1, null));
					//the SelectSimpleVanillaItem will go to the returnGui automatically
				},false));
			} else if (character == 'c') {
				state.getWindow().setMainComponent(new SelectCustomItem(getMenu(), (CustomItem item) -> {
					IngredientComponent.this.setIngredient(new CustomItemIngredient(item, (byte) 1, null));
					//the SelectCustomItem will go the the returnGui automatically
				}, set));
			} else if (character == 'd') {
				state.getWindow().setMainComponent(new SelectDataVanillaItem(getMenu(), (CIMaterial material, byte data) -> {
					IngredientComponent.this.setIngredient(new DataVanillaIngredient(material, data, (byte) 1, null));
				}));
			} else if (character == 'e') {
				IngredientComponent.this.setIngredient(new NoIngredient());
			}
		}
	}
}