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

public class ByteArrayBitOutput extends BitOutput {

	private byte[] bytes;

	private int byteIndex;
	private int boolIndex;

	public ByteArrayBitOutput() {
		this(100);
	}

	public ByteArrayBitOutput(int startCapacity) {
		bytes = new byte[startCapacity];
	}

	@Override
	public void addDirectBoolean(boolean value) {
		boolean[] bools = BitHelper.byteToBinary(bytes[byteIndex]);
		bools[boolIndex++] = value;
		bytes[byteIndex] = BitHelper.byteFromBinary(bools);
		if (boolIndex == 8) {
			boolIndex = 0;
			byteIndex++;
		} // 10010101
	}

	@Override
	public void addDirectByte(byte value) {
		if (boolIndex == 0)
			bytes[byteIndex++] = value;
		else {
			boolean[] values = BitHelper.byteToBinary(value);
			int valueIndex = 0;
			boolean[] current = BitHelper.byteToBinary(bytes[byteIndex]);
			boolean[] next = new boolean[8];
			for (; boolIndex < 8; boolIndex++)
				current[boolIndex] = values[valueIndex++];
			boolIndex = 0;
			for (; valueIndex < 8; valueIndex++)
				next[boolIndex++] = values[valueIndex];
			bytes[byteIndex++] = BitHelper.byteFromBinary(current);
			bytes[byteIndex] = BitHelper.byteFromBinary(next);
		}
	}

	@Override
	public void ensureExtraCapacity(int booleans) {
		int extra = booleans / 8;
		if (booleans - extra * 8 + boolIndex >= 8) {
			extra++;
		}
		
		// Add the byteIndex / 5 to prevent doing too many array copies
		int newLength = byteIndex + extra + 1 + byteIndex / 5;
		if (newLength > bytes.length)
			bytes = Arrays.copyOf(bytes, newLength);
	}

	@Override
	public void terminate() {
		bytes = getBytes();
	}

	public byte[] getBackingArray() {
		return bytes;
	}

	public byte[] getBytes() {
		int length = byteIndex;
		if (boolIndex != 0)
			length++;
		return Arrays.copyOf(bytes, length);
	}

	public boolean[] getBooleans() {
		int length = byteIndex * 8 + boolIndex;
		boolean[] bools = new boolean[length];
		int index = 0;
		for (int i = 0; i < byteIndex; i++) {
			boolean[] current = BitHelper.byteToBinary(bytes[i]);
			for (boolean b : current)
				bools[index++] = b;
		}
		if (boolIndex > 0) {
			boolean[] last = BitHelper.byteToBinary(bytes[byteIndex]);
			int lastIndex = 0;
			for (; index < length; index++)
				bools[index] = last[lastIndex++];
		}
		return bools;
	}
}