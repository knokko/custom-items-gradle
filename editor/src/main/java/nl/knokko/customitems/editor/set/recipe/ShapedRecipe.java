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
package nl.knokko.customitems.editor.set.recipe;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Arrays;

public class ShapedRecipe extends Recipe {
	
	private final Ingredient[] ingredients;

	public ShapedRecipe(Ingredient[] ingredients, Result result) {
		super(result);
		this.ingredients = ingredients;
	}
	
	public ShapedRecipe(BitInput input, ItemSet set) throws UnknownEncodingException {
		super(input, set);
		ingredients = new Ingredient[9];
		for (int index = 0; index < ingredients.length; index++)
			ingredients[index] = Ingredient.loadIngredient(input, set);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ShapedRecipe) {
			ShapedRecipe recipe = (ShapedRecipe) other;
			return result.equals(recipe.result) && Arrays.equals(ingredients, recipe.ingredients);
		} else {
			return false;
		}
	}

	@Override
	protected void saveOwn(BitOutput output) {
		for (Ingredient ingredient : ingredients)
			ingredient.save(output);
	}
	
	@Override
	public boolean requires(CustomItem item) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient instanceof CustomItemIngredient && ((CustomItemIngredient) ingredient).getItem() == item)
				return true;
			if (ItemSet.hasRemainingCustomItem(ingredient, item)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasConflictingShapedIngredients(Ingredient[] ingredients) {
		for (int index = 0; index < 9; index++)
			if (!ingredients[index].conflictsWith(this.ingredients[index]))
				return false;
		return true;
	}
	
	@Override
	protected byte getClassEncoding() {
		return RecipeEncoding.SHAPED_RECIPE;
	}
	
	public Ingredient getIngredient(int x, int y) {
		return ingredients[x + y * 3];
	}
	
	/**
	 * Don't modify this array!
	 * @return The array containing the ingredients of this shaped recipe
	 */
	public Ingredient[] getIngredients() {
		return ingredients;
	}
	
	public void setIngredients(Ingredient[] ingredients) {
		System.arraycopy(ingredients, 0, this.ingredients, 0, 9);
	}
	
	/**
	 * Shaped recipes simply have more priority than shapeless recipes
	 */
	@Override
	public boolean hasConflictingShapelessIngredients(Ingredient[] ingredients) {
		return false;
	}
}