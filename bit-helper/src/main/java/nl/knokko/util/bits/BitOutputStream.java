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
import java.io.OutputStream;

public class BitOutputStream extends BitOutput {

	protected boolean[] subData;
	protected int subIndex;

	protected OutputStream output;

	public BitOutputStream(OutputStream output) {
		this.output = output;
	}

	@Override
	public void addDirectBoolean(boolean value) {
		if (subData != null) {
			subData[subIndex++] = value;
			if (subIndex == 8) {
				subIndex = 0;
				try {
					output.write(BitHelper.byteFromBinary(subData));
				} catch (IOException ex) {
					throw new IllegalStateException(ex);
				}
				subData = null;
			}
		} else {
			subData = new boolean[] { value, false, false, false, false, false, false, false };
			subIndex = 1;
		}
	}

	@Override
	public void addDirectByte(byte value) {
		try {
			if (subIndex == 0)
				output.write(value);
			else {
				boolean[] bValue = BitHelper.byteToBinary(value);
				int index = 0;
				for (; subIndex < 8; subIndex++)
					subData[subIndex] = bValue[index++];
				output.write(BitHelper.byteFromBinary(subData));
				subIndex = 0;
				subData = new boolean[8];
				for (; index < 8; index++)
					subData[subIndex++] = bValue[index];
			}
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void addDirectBytes(byte... value) {
		if (subIndex == 0) {
			try {
				output.write(value);
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		} else
			super.addDirectBytes(value);
	}

	@Override
	public void ensureExtraCapacity(int booleans) {
	}

	@Override
	public void terminate() {
		try {
			if (subData != null) {
				output.write(BitHelper.byteFromBinary(subData));// don't forget to send the last bits
				subData = null;
			}
			subIndex = 0;
			output.flush();
			output.close();
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
}
