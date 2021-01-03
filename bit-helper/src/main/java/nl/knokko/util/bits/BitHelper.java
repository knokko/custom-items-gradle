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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class BitHelper {

	private static final long[] POWERS = new long[63];

	private static final boolean[][] BOOLEANS = new boolean[256][8];

	static {
		setPowers();
		setBooleans();
	}

	private static void setPowers() {
		long l = 1;
		for (byte index = 0; index < POWERS.length; index++) {
			POWERS[index] = l;
			l *= 2;
		}
	}

	private static void setBooleans() {
		for (short i = 0; i < 256; i++) {
			byte b = (byte) i;
			if (b >= 0)
				BOOLEANS[i][7] = true;
			else {
				b++;
				b *= -1;
			}
			if (b >= 64) {
				BOOLEANS[i][0] = true;
				b -= 64;
			}
			if (b >= 32) {
				BOOLEANS[i][1] = true;
				b -= 32;
			}
			if (b >= 16) {
				BOOLEANS[i][2] = true;
				b -= 16;
			}
			if (b >= 8) {
				BOOLEANS[i][3] = true;
				b -= 8;
			}
			if (b >= 4) {
				BOOLEANS[i][4] = true;
				b -= 4;
			}
			if (b >= 2) {
				BOOLEANS[i][5] = true;
				b -= 2;
			}
			if (b >= 1)
				BOOLEANS[i][6] = true;
		}
		/*
		 * for(short b = 0; b < 256; b++){ short c = b; for(byte t = 0; t < 8; t++){
		 * if(c >= BYTES[t]){ BOOLEANS[b][t] = true; c -= BYTES[t]; } } }
		 */
	}

	private static void checkBitCount(byte bits) {
		if (bits < 0)
			throw new IllegalArgumentException("Number of bits ( + " + bits + ") can't be negative!");
		if (bits >= 64)
			throw new IllegalArgumentException("Number of bits ( + " + bits + ") can't be greater than 63!");
	}

	private static void checkOverflow(long number, byte bits) {
		if (bits != 63 && (POWERS[bits] <= number || POWERS[bits] < -number))// I can't check when bits = 63 because
																				// that would require the long 2^63
			throw new IllegalArgumentException(
					"You need more than " + bits + " bits to store the number " + number + "!");
	}

	public static boolean[] byteToBinary(byte number) {
		boolean[] copy = BOOLEANS[number & 0xFF];
		boolean[] result = new boolean[8];
		result[0] = copy[0];
		result[1] = copy[1];
		result[2] = copy[2];
		result[3] = copy[3];
		result[4] = copy[4];
		result[5] = copy[5];
		result[6] = copy[6];
		result[7] = copy[7];// hardcoding the copy method might seem ugly, but performance is about 50 times
							// better
		return result;
	}

	public static void byteToBinary(byte number, boolean[] dest) {
		boolean[] copy = BOOLEANS[number & 0xFF];
		dest[0] = copy[0];
		dest[1] = copy[1];
		dest[2] = copy[2];
		dest[3] = copy[3];
		dest[4] = copy[4];
		dest[5] = copy[5];
		dest[6] = copy[6];
		dest[7] = copy[7];// I am really wondering why the byteToBinary(byte) method is about 20 times
							// faster than this one
	}

	public static void byteToBinary(byte number, boolean[] dest, int startIndex) {
		boolean[] copy = BOOLEANS[number & 0xFF];
		dest[startIndex++] = copy[0];
		dest[startIndex++] = copy[1];
		dest[startIndex++] = copy[2];
		dest[startIndex++] = copy[3];
		dest[startIndex++] = copy[4];
		dest[startIndex++] = copy[5];
		dest[startIndex++] = copy[6];
		dest[startIndex++] = copy[7];
	}

	public static boolean[] numberToBinary(long number, byte bits, boolean allowNegative) {
		checkBitCount(bits);
		checkOverflow(number, bits);
		byte neg = (byte) (allowNegative ? 1 : 0);
		boolean[] bools = new boolean[bits + neg];
		if (allowNegative) {
			if (number >= 0)
				bools[0] = true;
			else {
				// bools[0] will stay false
				number = -number;
				number--;
			}
		}
		for (byte b = 0; b < bits; b++) {
			if (number >= POWERS[bits - b - 1]) {
				number -= POWERS[bits - b - 1];
				bools[b + neg] = true;
			}
		}
		return bools;
	}
	
	// Ripped from java.nio.Bits

	public static byte char1(char x) {
		return (byte) (x >> 8);
	}

	public static byte char0(char x) {
		return (byte) (x);
	}

	public static byte short1(short x) {
		return (byte) (x >> 8);
	}

	public static byte short0(short x) {
		return (byte) (x);
	}

	public static byte int3(int x) {
		return (byte) (x >> 24);
	}

	public static byte int2(int x) {
		return (byte) (x >> 16);
	}

	public static byte int1(int x) {
		return (byte) (x >> 8);
	}

	public static byte int0(int x) {
		return (byte) (x);
	}

	public static byte long7(long x) {
		return (byte) (x >> 56);
	}

	public static byte long6(long x) {
		return (byte) (x >> 48);
	}

	public static byte long5(long x) {
		return (byte) (x >> 40);
	}

	public static byte long4(long x) {
		return (byte) (x >> 32);
	}

	public static byte long3(long x) {
		return (byte) (x >> 24);
	}

	public static byte long2(long x) {
		return (byte) (x >> 16);
	}

	public static byte long1(long x) {
		return (byte) (x >> 8);
	}

	public static byte long0(long x) {
		return (byte) (x);
	}
	
	// End of ripped content

	public static byte byteFromBinary(boolean[] source) {
		/*
		 * short number = 0; if(source[0]) number += 128; if(source[1]) number += 64;
		 * if(source[2]) number += 32; if(source[3]) number += 16; if(source[4]) number
		 * += 8; if(source[5]) number += 4; if(source[6]) number += 2; if(source[7])
		 * number++; return (byte) number;
		 */
		byte number = 0;
		if (source[0])
			number += 64;
		if (source[1])
			number += 32;
		if (source[2])
			number += 16;
		if (source[3])
			number += 8;
		if (source[4])
			number += 4;
		if (source[5])
			number += 2;
		if (source[6])
			number++;
		if (!source[7]) {
			number *= -1;
			number--;
		}
		return number;
	}

	public static byte byteFromBinary(boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5,
			boolean b6, boolean b7) {
		byte number = 0;
		if (b0)
			number += 64;
		if (b1)
			number += 32;
		if (b2)
			number += 16;
		if (b3)
			number += 8;
		if (b4)
			number += 4;
		if (b5)
			number += 2;
		if (b6)
			number++;
		if (!b7) {
			number *= -1;
			number--;
		}
		return number;
	}

	public static byte byteFromBinary(boolean[] source, int startIndex) {
		/*
		 * short number = 0; if(source[startIndex++]) number += 128;
		 * if(source[startIndex++]) number += 64; if(source[startIndex++]) number += 32;
		 * if(source[startIndex++]) number += 16; if(source[startIndex++]) number += 8;
		 * if(source[startIndex++]) number += 4; if(source[startIndex++]) number += 2;
		 * if(source[startIndex++]) number++; return (byte) number;
		 */
		byte number = 0;
		if (source[startIndex++])
			number += 64;
		if (source[startIndex++])
			number += 32;
		if (source[startIndex++])
			number += 16;
		if (source[startIndex++])
			number += 8;
		if (source[startIndex++])
			number += 4;
		if (source[startIndex++])
			number += 2;
		if (source[startIndex++])
			number++;
		if (!source[startIndex++]) {
			number *= -1;
			number--;
		}
		return number;
	}

	public static long numberFromBinary(boolean[] bools, byte bits, boolean allowNegative) {
		checkBitCount(bits);
		long number = 0;
		byte neg = (byte) (allowNegative ? 1 : 0);
		for (byte b = 0; b < bits; b++)
			if (bools[b + neg])
				number += POWERS[bits - b - 1];
		if (allowNegative && !bools[0]) {
			number = -number;
			number--;
		}
		return number;
	}

	public static long get2Power(int index) {
		return POWERS[index];
	}
	
	// Ripped from java.nio.Bits

	public static char makeChar(byte b0, byte b1) {
		return (char) ((b1 << 8) | (b0 & 0xff));
	}

	public static short makeShort(byte b0, byte b1) {
		return (short) ((b1 << 8) | (b0 & 0xff));
	}

	public static int makeInt(byte b0, byte b1, byte b2, byte b3) {
		return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
	}

	public static long makeLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
		return ((((long) b7) << 56) | (((long) b6 & 0xff) << 48) | (((long) b5 & 0xff) << 40)
				| (((long) b4 & 0xff) << 32) | (((long) b3 & 0xff) << 24) | (((long) b2 & 0xff) << 16)
				| (((long) b1 & 0xff) << 8) | (((long) b0 & 0xff)));
	}
	
	// End of ripped content
	
	public static byte[] readFile(File file) throws IOException {
		if (file.length() > 2000000000)
			throw new IOException("File too large (" + file.length() + ")");
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		DataInputStream input = new DataInputStream(Files.newInputStream(file.toPath()));
		input.readFully(bytes);
		input.close();
		return bytes;
	}
}