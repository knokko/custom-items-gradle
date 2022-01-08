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

import java.io.File;
import java.io.IOException;

public class ByteArrayBitInput extends BitInput {

	private int byteIndex;
	private int booleanIndex;

	private final int boundIndex;
	private final byte[] bytes;

	public static ByteArrayBitInput fromFile(File file) throws IOException {
		return new ByteArrayBitInput(BitHelper.readFile(file));
	}

	public ByteArrayBitInput(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

	public ByteArrayBitInput(byte[] bytes, int startIndex, int boundIndex) {
		this.bytes = bytes;
		this.byteIndex = startIndex;
		this.boundIndex = boundIndex;
	}

	@Override
	public boolean readDirectBoolean() {
		if (byteIndex >= boundIndex)
			throw new IndexOutOfBoundsException(
					"Current byte index is " + byteIndex + " and bound index is " + boundIndex);
		if (booleanIndex == 7) {
			booleanIndex = 0;
			// return BitHelper.byteToBinary(bytes[byteIndex++])[7];
			// The line below has the same result as the line above, but is much simpler
			return bytes[byteIndex++] >= 0;
		}
		return BitHelper.byteToBinary(bytes[byteIndex])[booleanIndex++];
	}

	@Override
	public byte readDirectByte() {
		if (byteIndex >= boundIndex)
			throw new IndexOutOfBoundsException(
					"Current byte index is " + byteIndex + " and bound index is " + boundIndex);
		if (booleanIndex == 0) {
			return bytes[byteIndex++];
		}
		if (byteIndex + 1 >= boundIndex)
			throw new IndexOutOfBoundsException(
					"Last byte index is " + (byteIndex + 1) + " and bound index is " + boundIndex);
		boolean[] bools = new boolean[16];
		BitHelper.byteToBinary(bytes[byteIndex++], bools, 0);
		BitHelper.byteToBinary(bytes[byteIndex], bools, 8);// do not increaese the byteIndex because this byte is not
															// yet finished
		// booleanIndex should not change
		return BitHelper.byteFromBinary(bools, booleanIndex);
	}

	@Override
	public void increaseCapacity(int booleans) {
	}

	@Override
	public void terminate() {
		byteIndex = -1;
		booleanIndex = -1;
	}
}