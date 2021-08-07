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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShapelessCustomRecipe implements CustomRecipe {
	
	private final Ingredient[] ingredients;
	private final ItemStack result;

	public ShapelessCustomRecipe(Ingredient[] ingredients, ItemStack result) {
		this.ingredients = ingredients;
		this.result = result;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ShapelessCustomRecipe) {
			ShapelessCustomRecipe recipe = (ShapelessCustomRecipe) other;
			return Arrays.equals(ingredients, recipe.ingredients) && result.equals(recipe.result);
		} else {
			return false;
		}
	}

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public List<IngredientEntry> shouldAccept(ItemStack[] ingredients) {

		boolean[] has = new boolean[this.ingredients.length];
		List<IngredientEntry> result = new ArrayList<>(this.ingredients.length);

		outerLoop:
		for (int ingredientIndex = 0; ingredientIndex < ingredients.length; ingredientIndex++) {
		    ItemStack ingredient = ingredients[ingredientIndex];
			if (!ItemHelper.getMaterialName(ingredient).equals(CIMaterial.AIR.name())) {
				for (int index = 0; index < has.length; index++) {
					if (!has[index] && this.ingredients[index].accept(ingredient)) {
						has[index] = true;
						result.add(new IngredientEntry(this.ingredients[index], ingredientIndex));
						continue outerLoop;
					}
				}
				// When we reach this code, we don't need that ingredient
				return null;
			}
		}
		
		// Now see if we have all necessary ingredients
		for (boolean b : has)
			if (!b)
				return null;
		
		// We have exactly what we need
		return result;
	}

	public Ingredient[] getIngredients() {
		return ingredients;
	}
}