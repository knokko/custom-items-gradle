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

import java.util.List;

public interface CustomRecipe {
    
    /**
     * @return The result of this recipe
     */
    ItemStack getResult();
    
    /**
     * Checks if the specified ingredients are sufficient to craft the result of this recipe. The result
     * will be non-null if the ingredients are sufficient.
     *
     * If the ingredients are sufficient, the result of this method will list which ingredients of this
     * recipe were mapped to which item (this is needed because some ingredients require an amount larger
     * than 1, and the event handler will need to know how much to subtract from the stack sizes of the
     * items).
     *
     * @param ingredients The crafting ingredients the player uses, from left to right and up to down
     * @return A list of ingredient-index entries indicating which ingredient is at which position, or null
     * if the ingredients do not satisfy this recipe.
     */
    List<IngredientEntry> shouldAccept(ItemStack[] ingredients);
}