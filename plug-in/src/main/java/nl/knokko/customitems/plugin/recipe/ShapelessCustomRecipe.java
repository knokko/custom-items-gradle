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
package nl.knokko.customitems.plugin.recipe;

import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class ShapelessCustomRecipe implements CustomRecipe {
	
	private final Ingredient[] ingredients;
	private final ItemStack result;

	public ShapelessCustomRecipe(Ingredient[] ingredients, ItemStack result) {
		this.ingredients = ingredients;
		this.result = result;
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public boolean shouldAccept(ItemStack[] ingredients) {
		boolean[] has = new boolean[this.ingredients.length];
		outerLoop:
		for (ItemStack ingredient : ingredients) {
			if (!ItemHelper.getMaterialName(ingredient).equals(CIMaterial.AIR.name())) {
				for (int index = 0; index < has.length; index++) {
					if (!has[index] && this.ingredients[index].accept(ingredient)) {
						has[index] = true;
						continue outerLoop;
					}
				}
				// When we reach this code, we don't need that ingredient
				return false;
			}
		}
		
		// Now see if we have all necessary ingredients
		for (boolean b : has)
			if (!b)
				return false;
		
		// We have exactly what we need
		return true;
	}
}