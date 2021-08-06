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
package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DataVanillaResult extends Result {
	
	private final CIMaterial type;
	private final byte data;

	public DataVanillaResult(CIMaterial type, byte data, byte amount) {
		super(amount);
		this.type = type;
		this.data = data;
		initInfo();
	}
	
	public DataVanillaResult(BitInput input) {
		super(input);
		type = CIMaterial.valueOf(input.readJavaString());
		data = (byte) input.readNumber((byte) 4, false);
		initInfo();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof DataVanillaResult) {
			DataVanillaResult result = (DataVanillaResult) other;
			return type == result.type && data == result.data && getAmount() == result.getAmount();
		} else {
			return false;
		}
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addJavaString(type.name());
		output.addNumber(data, (byte) 4, false);
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.VANILLA_DATA;
	}

	@Override
	protected String[] createInfo() {
		return new String[] {
				"Vanilla result:",
				"Type: " + NameHelper.getNiceEnumName(type.name()),
				"Data: " + data
		};
	}

	@Override
	public String getString() {
		return NameHelper.getNiceEnumName(type.name()) + "(" + data + ")";
	}

	@Override
	public Result amountClone(byte amount) {
		return new DataVanillaResult(type, data, amount);
	}
	
	public CIMaterial getType() {
		return type;
	}

	public byte getData() {
		return data;
	}
}