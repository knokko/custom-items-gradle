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

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class Result {
	
	private final byte amount;
	
	private String[] info;
	
	public Result(byte amount) {
		this.amount = amount;
	}
	
	public Result(BitInput input) {
		amount = (byte) (1 + input.readNumber((byte) 6, false));
	}
	
	protected abstract void saveOwn(BitOutput output);
	
	public abstract byte getID();
	
	protected void initInfo() {
		String[] extraInfo = createInfo();
		info = new String[extraInfo.length + 1];
		System.arraycopy(extraInfo, 0, info, 0, extraInfo.length);
		info[extraInfo.length] = "Amount: " + amount;
	}
	
	protected abstract String[] createInfo();
	
	public abstract String getString();
	
	/**
	 * @param amount The new amount
	 * @return A copy of this result, but with different amount
	 */
	public abstract Result amountClone(byte amount);
	
	@Override
	public String toString() {
		return getString() + " x " + amount;
	}
	
	public String[] getInfo() {
		return info;
	}
	
	public byte getAmount() {
		return amount;
	}
	
	public void save(BitOutput output) {
		output.addByte(getID());
		output.addNumber(amount - 1, (byte) 6, false);
		saveOwn(output);
	}
}