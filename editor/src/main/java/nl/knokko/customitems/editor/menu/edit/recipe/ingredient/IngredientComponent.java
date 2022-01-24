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
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class IngredientComponent extends DynamicTextButton {

	private final ShapedRecipeValues recipe;
	private final int x, y;
	private final GuiComponent menu;
	private final String emptyText;
	private final SItemSet set;

	public IngredientComponent(
			ShapedRecipeValues recipe, int x, int y,
			String emptyText, GuiComponent menu, SItemSet set) {
		super(recipe.getIngredientAt(x, y).toString(emptyText), EditProps.BUTTON, EditProps.HOVER, null);
		this.clickAction = () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(menu, this::setIngredient, true, set));
		};
		this.recipe = recipe;
		this.x = x;
		this.y = y;
		this.emptyText = emptyText;
		this.menu = menu;
		this.set = set;
	}

	public void setIngredient(IngredientValues newIngredient) {
		recipe.setIngredientAt(x, y, newIngredient);
		setText(newIngredient.toString(emptyText));
	}

	@Override
	public void keyPressed(char character) {
		if (state.isMouseOver()) {
			if (character == 'v') {
				state.getWindow().setMainComponent(new SelectSimpleVanillaItem(menu, (CIMaterial material) -> {
					IngredientComponent.this.setIngredient(SimpleVanillaIngredientValues.createQuick(material, 1, null));
					//the SelectSimpleVanillaItem will go to the returnGui automatically
				},false));
			} else if (character == 'c') {
				state.getWindow().setMainComponent(new SelectCustomItem(menu, (ItemReference item) -> {
					IngredientComponent.this.setIngredient(CustomItemIngredientValues.createQuick(item, 1, null));
					//the SelectCustomItem will go the the returnGui automatically
				}, set));
			} else if (character == 'd') {
				state.getWindow().setMainComponent(new SelectDataVanillaItem(menu, (CIMaterial material, byte data) -> {
					IngredientComponent.this.setIngredient(DataVanillaIngredientValues.createQuick(material, data, 1, null));
				}));
			} else if (character == 'e') {
				IngredientComponent.this.setIngredient(new NoIngredientValues());
			}
		}
	}
}