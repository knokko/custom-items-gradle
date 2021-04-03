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
package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomItemIngredient extends Ingredient {
	
	private final CustomItem item;

	public CustomItemIngredient(CustomItem item, byte amount, Result remaining) {
		super(amount, remaining);
		this.item = item;
	}
	
	CustomItemIngredient(BitInput input, ItemSet set, byte amount, Result remaining) {
		super(amount, remaining);
		String name = input.readJavaString();
		
		this.item = set.getCustomItemByName(name);
		if (this.item == null)
			throw new IllegalArgumentException("There is no custom item with name " + name);
	}
	
	public CustomItem getItem() {
		return item;
	}

	@Override
	public void saveSpecifics(BitOutput output) {
		output.addJavaString(item.getName());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.CUSTOM_2;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		return other instanceof CustomItemIngredient && ((CustomItemIngredient)other).item == item;
	}
	
	@Override
	public String toString(String emptyString) {
		return item.getName() + " x" + amount;
	}
	
	@Override
	public String toString() {
		return toString(null);
	}
}