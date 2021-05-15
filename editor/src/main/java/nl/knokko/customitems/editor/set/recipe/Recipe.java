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
import nl.knokko.customitems.editor.set.recipe.ingredient.*;
import nl.knokko.customitems.editor.set.recipe.result.*;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class Recipe {

	public static Result loadResult(BitInput input, ItemSet set) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE)
			return new SimpleVanillaResult(input);
		if (encoding == RecipeEncoding.Result.VANILLA_DATA)
			return new DataVanillaResult(input);
		if (encoding == RecipeEncoding.Result.CUSTOM)
			return new CustomItemResult(input, set);
		if (encoding == RecipeEncoding.Result.COPIED)
			return new CopiedResult(input);
		throw new UnknownEncodingException("Result", encoding);
	}
	
	protected Result result;
	
	public Recipe(Result result) {
		this.result = result;
	}
	
	public Recipe(BitInput input, ItemSet set) throws UnknownEncodingException {
		result = loadResult(input, set);
	}
	
	public final void save(BitOutput output) {
		output.addByte(getClassEncoding());
		result.save(output);
		saveOwn(output);
	}
	
	protected abstract void saveOwn(BitOutput output);
	
	protected abstract byte getClassEncoding();
	
	public abstract boolean requires(CustomItem item);
	
	public abstract boolean hasConflictingShapedIngredients(Ingredient[] ingredients);
	
	public abstract boolean hasConflictingShapelessIngredients(Ingredient[] ingredients);
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(Result newResult) {
		result = newResult;
	}
}