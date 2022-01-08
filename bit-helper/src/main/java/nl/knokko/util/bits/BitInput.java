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

import java.util.HashMap;
import java.util.TreeMap;

public abstract class BitInput {

	public abstract boolean readDirectBoolean();

	public abstract byte readDirectByte();

	public abstract void increaseCapacity(int booleans);

	public abstract void terminate();

	public char readDirectChar() {
		return BitHelper.makeChar(readDirectByte(), readDirectByte());
	}

	public short readDirectShort() {
		return BitHelper.makeShort(readDirectByte(), readDirectByte());
	}

	public int readDirectInt() {
		return BitHelper.makeInt(readDirectByte(), readDirectByte(), readDirectByte(), readDirectByte());
	}

	public float readDirectFloat() {
		return Float.intBitsToFloat(readDirectInt());
	}

	public long readDirectLong() {
		return BitHelper.makeLong(readDirectByte(), readDirectByte(), readDirectByte(), readDirectByte(),
				readDirectByte(), readDirectByte(), readDirectByte(), readDirectByte());
	}

	public double readDirectDouble() {
		return Double.longBitsToDouble(readDirectLong());
	}

	public boolean readBoolean() {
		increaseCapacity(1);
		return readDirectBoolean();
	}

	public boolean[] readBooleans(int amount) {
		boolean[] booleans = new boolean[amount];
		readBooleans(booleans);
		return booleans;
	}
	
	public boolean[] readDirectBooleans(int amount) {
		boolean[] booleans = new boolean[amount];
		readDirectBooleans(booleans);
		return booleans;
	}

	public void readBooleans(boolean[] booleans, int startIndex, int amount) {
		increaseCapacity(amount);
		for (int i = 0; i < amount; i++)
			booleans[startIndex + i] = readDirectBoolean();
	}
	
	public void readDirectBooleans(boolean[] booleans, int startIndex, int amount) {
		for (int i = 0; i < amount; i++)
			booleans[startIndex + i] = readDirectBoolean();
	}

	public void readBooleans(boolean[] booleans) {
		readBooleans(booleans, 0, booleans.length);
	}
	
	public void readDirectBooleans(boolean[] booleans) {
		readDirectBooleans(booleans, 0, booleans.length);
	}

	public boolean[] readBooleanArray() {
		boolean[] booleans = new boolean[readInt()];
		readBooleans(booleans);
		return booleans;
	}

	public byte readByte() {
		increaseCapacity(8);
		return readDirectByte();
	}

	public byte[] readBytes(int amount) {
		byte[] bytes = new byte[amount];
		readBytes(bytes);
		return bytes;
	}

	public void readBytes(byte[] bytes, int startIndex, int amount) {
		increaseCapacity(amount * 8);
		for (int i = 0; i < amount; i++)
			bytes[startIndex + i] = readDirectByte();
	}

	public void readBytes(byte[] bytes) {
		readBytes(bytes, 0, bytes.length);
	}

	public byte[] readByteArray() {
		byte[] bytes = new byte[readInt()];
		readBytes(bytes);
		return bytes;
	}

	public char readChar() {
		increaseCapacity(16);
		return BitHelper.makeChar(readDirectByte(), readDirectByte());
	}

	public char[] readChars(int amount) {
		char[] chars = new char[amount];
		readChars(chars);
		return chars;
	}

	public void readChars(char[] chars, int startIndex, int length) {
		increaseCapacity(length * 16);
		for (int i = 0; i < length; i++)
			chars[startIndex + i] = readDirectChar();
	}

	public void readChars(char[] chars) {
		readChars(chars, 0, chars.length);
	}

	public char[] readCharArray() {
		char[] chars = new char[readInt()];
		readChars(chars);
		return chars;
	}

	public short readShort() {
		increaseCapacity(16);
		return BitHelper.makeShort(readDirectByte(), readDirectByte());
	}

	public short[] readShorts(int amount) {
		short[] shorts = new short[amount];
		readShorts(shorts);
		return shorts;
	}

	public void readShorts(short[] shorts, int startIndex, int length) {
		increaseCapacity(16 * length);
		for (int i = 0; i < length; i++)
			shorts[startIndex + i] = readDirectShort();
	}

	public void readShorts(short[] shorts) {
		readShorts(shorts, 0, shorts.length);
	}

	public short[] readShortArray() {
		short[] shorts = new short[readInt()];
		readShorts(shorts);
		return shorts;
	}

	public int readInt() {
		increaseCapacity(32);
		return BitHelper.makeInt(readDirectByte(), readDirectByte(), readDirectByte(), readDirectByte());
	}

	public int[] readInts(int amount) {
		int[] ints = new int[amount];
		readInts(ints);
		return ints;
	}

	public void readInts(int[] ints, int startIndex, int length) {
		increaseCapacity(32 * length);
		for (int i = 0; i < length; i++)
			ints[startIndex + i] = readDirectInt();
	}

	public void readInts(int[] ints) {
		readInts(ints, 0, ints.length);
	}

	public int[] readIntArray() {
		int[] ints = new int[readInt()];
		readInts(ints);
		return ints;
	}

	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public float[] readFloats(int amount) {
		float[] floats = new float[amount];
		readFloats(floats);
		return floats;
	}

	public void readFloats(float[] floats, int startIndex, int length) {
		increaseCapacity(32 * length);
		for (int i = 0; i < length; i++)
			floats[startIndex + i] = readDirectFloat();
	}

	public void readFloats(float[] floats) {
		readFloats(floats, 0, floats.length);
	}

	public float[] readFloatArray() {
		float[] floats = new float[readInt()];
		readFloats(floats);
		return floats;
	}

	public long readLong() {
		increaseCapacity(64);
		return BitHelper.makeLong(readDirectByte(), readDirectByte(), readDirectByte(), readDirectByte(),
				readDirectByte(), readDirectByte(), readDirectByte(), readDirectByte());
	}

	public long[] readLongs(int amount) {
		long[] longs = new long[amount];
		readLongs(longs);
		return longs;
	}

	public void readLongs(long[] longs, int startIndex, int length) {
		increaseCapacity(64 * length);
		for (int i = 0; i < length; i++)
			longs[startIndex + i] = readDirectLong();
	}

	public void readLongs(long[] longs) {
		readLongs(longs, 0, longs.length);
	}

	public long[] readLongArray() {
		long[] longs = new long[readInt()];
		readLongs(longs);
		return longs;
	}

	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	public double[] readDoubles(int amount) {
		double[] doubles = new double[amount];
		readDoubles(doubles);
		return doubles;
	}

	public void readDoubles(double[] doubles, int startIndex, int length) {
		increaseCapacity(64 * length);
		for (int i = 0; i < length; i++)
			doubles[startIndex + i] = readDirectDouble();
	}

	public void readDoubles(double[] doubles) {
		readDoubles(doubles, 0, doubles.length);
	}

	public double[] readDoubleArray() {
		double[] doubles = new double[readInt()];
		readDoubles(doubles);
		return doubles;
	}

	public long readNumber(byte bitCount, boolean allowNegative) {
		byte size = bitCount;
		if (allowNegative)
			size++;
		return BitHelper.numberFromBinary(readBooleans(size), bitCount, allowNegative);
	}
	
	public long readDirectNumber(byte bitCount, boolean allowNegative) {
		byte size = bitCount;
		if (allowNegative)
			size++;
		return BitHelper.numberFromBinary(readDirectBooleans(size), bitCount, allowNegative);
	}

	public long readNumber(boolean allowNegative) {
		return readNumber((byte) readNumber((byte) 6, false), allowNegative);
	}
	
	public long readDirectNumber(boolean allowNegative) {
		return readDirectNumber((byte) readDirectNumber((byte) 6, false), allowNegative);
	}

	public String readJavaString() {
		return readJavaString(1000);
	}

	public String readJavaString(int maxLength) {
		int amount = readInt();
		if (amount == -1)
			return null;
		if (amount > maxLength)
			throw new RuntimeException("amount is " + amount + " and maxLength is " + maxLength);
		byte bitCount = (byte) (readNumber((byte) 4, false) + 1);
		char[] chars = new char[amount];
		for (int i = 0; i < chars.length; i++)
			chars[i] = (char) readNumber(bitCount, false);
		return new String(chars);
	}
	
	public String readString() {
		return readString(1000);
	}
	
	public String readString(int maxLength) {
		int amount1 = readByte() & 0xFF;
		if (amount1 == 0)
			return null;
		int length;
		if (amount1 < 255) {
			length = amount1 - 1;
		} else {
			length = readInt();
		}
		if (length == 0)
			return "";
		increaseCapacity(21);
		char min = readDirectChar();
		byte bitCount = (byte) readDirectNumber((byte) 5, false);
		if (bitCount == 0) {
			char[] result = new char[length];
			for (int index = 0; index < length; index++) {
				result[index] = min;
			}
			return new String(result);
		} else {
			increaseCapacity(bitCount * length);
			char[] result = new char[length];
			for (int index = 0; index < length; index++) {
				result[index] = (char) (min + readDirectNumber(bitCount, false));
			}
			return new String(result);
		}
	}
	
	public HashMap<String,String> readStringHashMap(){
		int size = readInt();
		HashMap<String,String> result = new HashMap<String,String>(size);
		for (int counter = 0; counter < size; counter++) {
			result.put(readString(), readString());
		}
		return result;
	}
	
	public TreeMap<String,String> readStringTreeMap(){
		int size = readInt();
		TreeMap<String,String> result = new TreeMap<String,String>();
		for (int counter = 0; counter < size; counter++) {
			result.put(readString(), readString());
		}
		return result;
	}
}