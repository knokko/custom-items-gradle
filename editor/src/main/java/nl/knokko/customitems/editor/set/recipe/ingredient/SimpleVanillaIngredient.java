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
package nl.knokko.customitems.editor.set.recipe.ingredient;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleVanillaIngredient extends Ingredient {
	
	private final CIMaterial type;
	
	public SimpleVanillaIngredient(CIMaterial material, byte amount, Result remaining) {
		super(amount, remaining);
		this.type = material;
	}
	
	SimpleVanillaIngredient(BitInput input, byte amount, Result remaining) {
		super(amount, remaining);
		type = CIMaterial.valueOf(input.readJavaString());
	}

	public CIMaterial getType() {
		return type;
	}

	@Override
	public void saveSpecifics(BitOutput output) {
		output.addJavaString(type.name());
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Ingredient.VANILLA_SIMPLE_2;
	}

	@Override
	public boolean conflictsWith(Ingredient other) {
		if (other instanceof SimpleVanillaIngredient)
			return ((SimpleVanillaIngredient) other).type == type;
		if (other instanceof DataVanillaIngredient)
			return ((DataVanillaIngredient) other).getType() == type;
		return false;
	}
	
	@Override
	public String toString(String emptyString) {
		return NameHelper.getNiceEnumName(type.name()) + amountToString() + remainingToString();
	}
	
	@Override
	public String toString() {
		return toString(null);
	}
}