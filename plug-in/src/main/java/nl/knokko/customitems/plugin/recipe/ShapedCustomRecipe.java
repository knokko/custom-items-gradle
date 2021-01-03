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
	public boolean shouldAccept(ItemStack[] ingredients) {
		if (ingredients.length == 9) {
			for(int index = 0; index < 9; index++) 
				if(!this.ingredients[index].accept(ingredients[index]))
					return false;
			return true;
		}
		return false;
	}
}