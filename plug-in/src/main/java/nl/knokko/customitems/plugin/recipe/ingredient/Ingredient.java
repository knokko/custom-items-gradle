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
package nl.knokko.customitems.plugin.recipe.ingredient;

import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.recipe.SCIngredient;

public abstract class Ingredient implements SCIngredient {

    protected final byte amount;
    protected final ItemStack remainingItem;

    Ingredient(byte amount, ItemStack remainingItem) {
        this.amount = amount;
        this.remainingItem = remainingItem;
    }

    public final boolean accept(ItemStack item) {
        if (remainingItem == null) {

            // If there is no remaining item, we can accept if the amount is large enough
            if (item == null || item.getAmount() >= amount) {
                return acceptSpecific(item);
            } else {
                return false;
            }
        } else {

            // If there is a remaining item, it must be consumed ENTIRELY to make space for the remaining item
            if (item == null || item.getAmount() == amount) {
                return acceptSpecific(item);
            } else {
                return false;
            }
        }
    }

    public abstract boolean acceptSpecific(ItemStack item);

    public byte getAmount() {
        return amount;
    }

    public ItemStack getRemainingItem() {
        return remainingItem;
    }

    public ItemStack cloneRemainingItem() {
        if (remainingItem == null) {
            return null;
        } else {
            return remainingItem.clone();
        }
    }
}