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

import nl.knokko.util.bits.BitInput;
import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.util.ItemUtils;

import java.util.Objects;

public class DataVanillaIngredient extends Ingredient {
	
	private final CIMaterial type;
	private final byte data;

	public DataVanillaIngredient(BitInput input, byte amount, ItemStack remainingItem) {
		super(amount, remainingItem);
		this.type = CIMaterial.valueOf(input.readJavaString());
		this.data = (byte) input.readNumber((byte) 4, false);
	}

	public DataVanillaIngredient(CIMaterial type, byte data, byte amount, ItemStack remainingItem) {
		super(amount, remainingItem);
		this.type = type;
		this.data = data;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof DataVanillaIngredient) {
			DataVanillaIngredient ingredient = (DataVanillaIngredient) other;
			return type == ingredient.type && data == ingredient.data && amount == ingredient.amount
					&& Objects.equals(remainingItem, ingredient.remainingItem);
		} else {
			return false;
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean acceptSpecific(ItemStack item) {
		if (type == CIMaterial.AIR) {
			return ItemUtils.isEmpty(item);
		} else {
			return !ItemUtils.isEmpty(item)
					&& !ItemUtils.isCustom(item)
					&& ItemHelper.getMaterialName(item).equals(type.name()) 
					&& item.getData().getData() == data;
		}
	}
	
	@Override
	public String toString() {
		return type.name() + "[" + data + "]";
	}

	public CIMaterial getType() {
		return type;
	}

	public byte getData() {
		return data;
	}
}