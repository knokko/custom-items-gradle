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

import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptItemStack;

public class ShapedCraftingRecipeWrapper implements CraftingRecipeWrapper {

	private final ShapedRecipeValues recipe;

    public ShapedCraftingRecipeWrapper(ShapedRecipeValues recipe){
    	this.recipe = recipe;
    }

    @Override
	public boolean equals(Object other) {
    	if (other instanceof ShapedCraftingRecipeWrapper) {
    		return this.recipe == ((ShapedCraftingRecipeWrapper) other).recipe;
		} else {
    		return false;
		}
	}

	@Override
	public ItemStack getResult() {
		return convertResultToItemStack(this.recipe.getResult());
	}

	@Override
	public List<IngredientEntry> shouldAccept(ItemStack[] ingredients) {

    	// For the 3x3 crafting grid
		if (ingredients.length == 9) {
		    List<IngredientEntry> result = new ArrayList<>(9);
			for (int index = 0; index < 9; index++) {
				int x = index % 3;
				int y = index / 3;
				if (shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(x, y), ingredients[index])) {
					if (!(this.recipe.getIngredientAt(x, y) instanceof NoIngredientValues)) {
						result.add(new IngredientEntry(this.recipe.getIngredientAt(x, y), index));
					}
				} else {
					return null;
				}
			}
			return result;
		}

		// For the 2x2 crafting grid
		if (ingredients.length == 4) {

			// So we should only accept this if this recipe has no ingredients at x == 2 or y == 2
			for (int x = 0; x < 3; x++) {
				if (!shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(x, 2), null)) return null;
			}
			for (int y = 0; y < 3; y++) {
				if (!shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(2, y), null)) return null;
			}

			// Compare the relevant ingredients (note the weird displacement)
			if (shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(0, 0), ingredients[0])
					&& shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(1, 0), ingredients[1])
					&& shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(0, 1), ingredients[2])
					&& shouldIngredientAcceptItemStack(this.recipe.getIngredientAt(1, 1), ingredients[3])) {

				List<IngredientEntry> result = new ArrayList<>(4);
				if (!(this.recipe.getIngredientAt(0, 0) instanceof NoIngredientValues)) {
					result.add(new IngredientEntry(this.recipe.getIngredientAt(0, 0), 0));
				}
				if (!(this.recipe.getIngredientAt(1, 0) instanceof NoIngredientValues)) {
					result.add(new IngredientEntry(this.recipe.getIngredientAt(1, 0), 1));
				}
				if (!(this.recipe.getIngredientAt(0, 1) instanceof NoIngredientValues)) {
					result.add(new IngredientEntry(this.recipe.getIngredientAt(0, 1), 2));
				}
				if (!(this.recipe.getIngredientAt(1, 1) instanceof NoIngredientValues)) {
					result.add(new IngredientEntry(this.recipe.getIngredientAt(1, 1), 3));
				}

				return result;
			} else {
				return null;
			}
		}
		return null;
	}
}
