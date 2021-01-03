/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
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
package nl.knokko.util.bits;

import java.util.Arrays;

public class BooleanArrayBitOutput extends BitOutput {

	private boolean[] booleans;

	private int writeIndex;

	public BooleanArrayBitOutput() {
		this(800);
	}

	public BooleanArrayBitOutput(int startCapacity) {
		booleans = new boolean[startCapacity];
	}

	@Override
	public void addDirectBoolean(boolean value) {
		booleans[writeIndex++] = value;
	}

	@Override
	public void addDirectByte(byte value) {
		BitHelper.byteToBinary(value, booleans, writeIndex);
		writeIndex += 8;
	}

	@Override
	public void addDirectBooleans(boolean... bools) {
		System.arraycopy(bools, 0, booleans, writeIndex, bools.length);
		writeIndex += bools.length;
	}

	@Override
	public void ensureExtraCapacity(int booleans) {
		
		// Add the writeIndex / 5 to prevent doing too many array copies
		int newLength = writeIndex + booleans;
		if (newLength > this.booleans.length)
			this.booleans = Arrays.copyOf(this.booleans, newLength + writeIndex / 5);
	}

	@Override
	public void terminate() {
		
		// Trim
		booleans = getBooleans();
	}

	public boolean[] getBackingArray() {
		return booleans;
	}

	public boolean[] getBooleans() {
		return Arrays.copyOf(booleans, writeIndex);
	}

	public byte[] getBytes() {
		int size = writeIndex / 8;
		int safeSize = size;
		boolean addHalfByte = 8 * size < writeIndex;
		if (addHalfByte)
			size++;// rounding upwards
		byte[] bytes = new byte[size];
		for (int i = 0; i < safeSize; i++)
			bytes[i] = BitHelper.byteFromBinary(booleans, i * 8);
		if (addHalfByte) {
			boolean[] last = new boolean[8];
			int index = safeSize * 8;
			int lastIndex = 0;
			for (; index < writeIndex; index++)
				last[lastIndex++] = booleans[index];
			bytes[safeSize] = BitHelper.byteFromBinary(last);
		}
		return bytes;
	}

	public void setWriteIndex(int newIndex) {
		writeIndex = newIndex;
	}
}
