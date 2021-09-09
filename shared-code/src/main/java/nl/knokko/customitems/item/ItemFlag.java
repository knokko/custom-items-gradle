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
package nl.knokko.customitems.item;

import nl.knokko.customitems.NameHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A ItemFlag can hide some Attributes from ItemStacks.
 * Copied straight from Bukkit, but separated from the Bukkit API to ensure that the order
 * in which the constants occur won't change and so that the Editor doesn't need to import the
 * entire Bukkit API.
 */
public enum ItemFlag {

    /**
     * Setting to show/hide enchants
     */
    HIDE_ENCHANTS,
    /**
     * Setting to show/hide Attributes like Damage
     */
    HIDE_ATTRIBUTES,
    /**
     * Setting to show/hide the unbreakable State
     */
    HIDE_UNBREAKABLE,
    /**
     * Setting to show/hide what the ItemStack can break/destroy
     */
    HIDE_DESTROYS,
    /**
     * Setting to show/hide where this ItemStack can be build/placed on
     */
    HIDE_PLACED_ON,
    /**
     * Setting to show/hide potion effects on this ItemStack
     */
    HIDE_POTION_EFFECTS;
	
	public static boolean[] getDefaultValues() {
		return new boolean[]{false, false, true, false, false, false};
	}

	public static List<Boolean> getDefaultValuesList() {
	    List<Boolean> result = new ArrayList<>(6);
	    for (boolean defaultValue : getDefaultValues()) {
	        result.add(defaultValue);
        }
	    return result;
    }
	
	@Override
	public String toString() {
		return NameHelper.getNiceEnumName(name());
	}
}