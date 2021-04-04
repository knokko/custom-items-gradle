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

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.result.Result;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class Ingredient implements SCIngredient {

	public static Ingredient loadIngredient(BitInput input, ItemSet set) throws UnknownEncodingException {
		byte encoding = input.readByte();
		byte defaultAmount = 1;
		switch (encoding) {
			case RecipeEncoding.Ingredient.NONE: return new NoIngredient();
			case RecipeEncoding.Ingredient.VANILLA_SIMPLE:
				return new SimpleVanillaIngredient(input, defaultAmount, null);
			case RecipeEncoding.Ingredient.VANILLA_DATA:
				return new DataVanillaIngredient(input, defaultAmount, null);
			case RecipeEncoding.Ingredient.CUSTOM:
				return new CustomItemIngredient(input, set, defaultAmount, null);
			case RecipeEncoding.Ingredient.VANILLA_SIMPLE_2:
				return new SimpleVanillaIngredient(input, input.readByte(), loadRemaining(input, set));
			case RecipeEncoding.Ingredient.VANILLA_DATA_2:
				return new DataVanillaIngredient(input, input.readByte(), loadRemaining(input, set));
			case RecipeEncoding.Ingredient.CUSTOM_2:
				return new CustomItemIngredient(input, set, input.readByte(), loadRemaining(input, set));
			default: throw new UnknownEncodingException("Ingredient", encoding);
		}
	}

	private static Result loadRemaining(BitInput input, ItemSet set) throws UnknownEncodingException {
		if (input.readBoolean()) {
			return Recipe.loadResult(input, set);
		} else {
			return null;
		}
	}

	protected final byte amount;
	protected final Result remaining;

	public Ingredient(byte amount, Result remaining) {
		this.amount = amount;
		this.remaining = remaining;
	}
	
	public abstract void saveSpecifics(BitOutput output);
	
	public abstract byte getID();
	
	public abstract boolean conflictsWith(Ingredient other);
	
	public abstract String toString(String emptyString);

	public final void save(BitOutput output) {
		output.addByte(getID());

		// Don't save amount in the case of NoIngredient
		if (amount != 0) {
			output.addByte(amount);
			output.addBoolean(remaining != null);
			if (remaining != null) {
				remaining.save(output);
			}
		}

		saveSpecifics(output);
	}

	public byte getAmount() {
		return amount;
	}

	public Result getRemainingItem() {
		return remaining;
	}
}