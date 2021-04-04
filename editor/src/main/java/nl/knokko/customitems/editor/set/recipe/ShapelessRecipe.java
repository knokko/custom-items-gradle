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

public class ShapelessRecipe extends Recipe {
	
	private Ingredient[] ingredients;

	public ShapelessRecipe(Result result, Ingredient[] ingredients) {
		super(result);
		this.ingredients = ingredients;
	}

	public ShapelessRecipe(BitInput input, ItemSet set) throws UnknownEncodingException {
		super(input, set);
		byte ingredientCount = (byte) input.readNumber((byte) 4, false);
		ingredients = new Ingredient[ingredientCount];
		for (int counter = 0; counter < ingredientCount; counter++)
			ingredients[counter] = Ingredient.loadIngredient(input, set);
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addNumber(ingredients.length, (byte) 4, false);
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
	public byte getClassEncoding() {
		return RecipeEncoding.SHAPELESS_RECIPE;
	}
	
	public Ingredient[] getIngredients(){
		return ingredients;
	}
	
	public void setIngredients(Ingredient[] newIngredients) {
		ingredients = newIngredients;
	}
	
	/**
	 * Always returns false because shaped recipes simply have more priority
	 */
	@Override
	public boolean hasConflictingShapedIngredients(Ingredient[] ingredients) {
		return false;
	}

	@Override
	public boolean hasConflictingShapelessIngredients(Ingredient[] ingredients) {
		if (ingredients.length != this.ingredients.length)
			return false;
		boolean[] has = new boolean[ingredients.length];
		for (Ingredient ingredient : ingredients) {
			for (int index = 0; index < has.length; index++) {
				if (!has[index] && ingredient.conflictsWith(this.ingredients[index])) {
					has[index] = true;
					break;
				}
			}
		}
		for (boolean h : has)
			if (!h)
				return false;
		return true;
	}
}