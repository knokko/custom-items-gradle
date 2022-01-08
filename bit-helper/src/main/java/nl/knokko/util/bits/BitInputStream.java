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

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends BitInput {

	private InputStream input;

	private boolean[] leftBits;
	private int leftIndex;

	public BitInputStream(InputStream input) {
		if (input == null)
			throw new NullPointerException();
		this.input = input;
	}

	@Override
	public boolean readDirectBoolean() {
		try {
			if (leftIndex != 0) {
				boolean result = leftBits[leftIndex];
				leftIndex++;
				if (leftIndex == 8) {
					leftIndex = 0;
					leftBits = null;
				}
				return result;
			}
			int next = input.read();
			if (next == -1)
				throw new IllegalStateException("End of input stream has been reached!");
			leftBits = BitHelper.byteToBinary((byte) next);
			leftIndex = 1;
			return leftBits[0];
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public byte readDirectByte() {
		try {
			if (leftIndex == 0)
				return (byte) input.read();
			boolean[] newBools = BitHelper.byteToBinary((byte) input.read());
			boolean[] result = new boolean[8];
			int index = 0;
			for (; leftIndex < 8; leftIndex++)
				result[index++] = leftBits[leftIndex];
			leftIndex = 0;
			for (; index < 8; index++)
				result[index] = newBools[leftIndex++];
			leftBits = newBools;
			return BitHelper.byteFromBinary(result);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void increaseCapacity(int booleans) {
	}

	@Override
	public void terminate() {
		try {
			input.close();
			input = null;
			leftBits = null;
			leftIndex = -1;
		} catch (IOException ignored) {
		}
	}

	@Override
	public void readBytes(byte[] bytes, int startIndex, int amount) {
		try {
			if (leftIndex == 0)
				input.read(bytes, startIndex, amount);
			else
				super.readBytes(bytes, startIndex, amount);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
