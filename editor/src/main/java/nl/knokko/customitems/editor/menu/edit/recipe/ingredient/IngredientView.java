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
import nl.knokko.customitems.editor.menu.edit.select.item.*;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ingredient.*;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class IngredientView extends GuiMenu {
	
	private final IngredientComponent component;
	private final ItemSet set;
	private final String emptyString;

	public IngredientView(String emptyString, IngredientComponent component, ItemSet set) {
		this.emptyString = emptyString;
		this.component = component;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		String[] info = component.getIngredient().getInfo(emptyString);
		for (int index = 0; index < info.length; index++)
			addComponent(new DynamicTextComponent(info[index], EditProps.LABEL), 0.2f, 0.8f - index * 0.15f, 0.5f, 0.9f - index * 0.15f);
		
		
		addComponent(new DynamicTextComponent("Change to", EditProps.LABEL), 
				0.75f, 0.75f, 0.9f, 0.85f);
		addComponent(new DynamicTextButton("Custom Item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectCustomItem(component.getMenu(), (CustomItem item) -> {
				component.setIngredient(new CustomItemIngredient(item));
				//the SelectCustomItem will go the the returnGui automatically
			}, set));
		}), 0.75f, 0.6f, 0.95f, 0.7f);
		addComponent(new DynamicTextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(component.getMenu(), (CIMaterial material) -> {
				component.setIngredient(new SimpleVanillaIngredient(material));
				//the SelectSimpleVanillaItem will go to the returnGui automatically
			}, false));
		}), 0.75f, 0.45f, 0.95f, 0.55f);
		addComponent(new DynamicTextButton("Vanilla item with datavalue", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(component.getMenu(), (CIMaterial material, byte data) -> {
				component.setIngredient(new DataVanillaIngredient(material, data));
			}));
		}), 0.75f, 0.3f, 0.95f, 0.4f);
		addComponent(new DynamicTextButton("Empty", EditProps.BUTTON, EditProps.HOVER, () -> {
			component.setIngredient(new NoIngredient());
			state.getWindow().setMainComponent(component.getMenu());
		}), 0.75f, 0.15f, 0.95f, 0.25f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/input%20type%20select.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}