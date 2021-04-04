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

import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShapedCustomRecipe implements CustomRecipe {
    
    private final Ingredient[] ingredients;
    private final ItemStack result;
    
    public ShapedCustomRecipe(ItemStack result, Ingredient[] ingredients){
        this.ingredients = ingredients;
        this.result = result;
    }

	@Override
	public ItemStack getResult() {
		return result;
	}

	@Override
	public List<IngredientEntry> shouldAccept(ItemStack[] ingredients) {

    	// For the 3x3 crafting grid
		if (ingredients.length == 9) {
		    List<IngredientEntry> result = new ArrayList<>(9);
			for (int index = 0; index < 9; index++) {
				if (this.ingredients[index].accept(ingredients[index])) {
					if (this.ingredients[index].getAmount() > 0) {
						result.add(new IngredientEntry(this.ingredients[index], index));
					}
				} else {
					return null;
				}
			}
			return result;
		}

		// For the 2x2 crafting grid
		if (ingredients.length == 4) {
			// In the 2x2 shape, the ingredient indices should be:
			// [0] [1] (2)
			// [3] [4] (5)
			// (6) (7) (8)

			// So we should only accept this if this recipe has no ingredients at index 2, 5, 6, 7, and 8
			if (!this.ingredients[2].accept(null)) {
				return null;
			}
			for (int index = 5; index < 9; index++) {
				if (!this.ingredients[index].accept(null)) {
					return null;
				}
			}



			// Compare the relevant ingredients (note the weird displacement)
			if (this.ingredients[0].accept(ingredients[0])
					&& this.ingredients[1].accept(ingredients[1])
					&& this.ingredients[3].accept(ingredients[2])
					&& this.ingredients[4].accept(ingredients[3])) {

				List<IngredientEntry> result = new ArrayList<>(4);
				if (this.ingredients[0].getAmount() > 0) {
					result.add(new IngredientEntry(this.ingredients[0], 0));
				}
				if (this.ingredients[1].getAmount() > 0) {
					result.add(new IngredientEntry(this.ingredients[1], 1));
				}
				if (this.ingredients[3].getAmount() > 0) {
					result.add(new IngredientEntry(this.ingredients[3], 2));
				}
				if (this.ingredients[4].getAmount() > 0) {
					result.add(new IngredientEntry(this.ingredients[3], 3));
				}

				return result;
			} else {
				return null;
			}
		}
		return null;
	}
}