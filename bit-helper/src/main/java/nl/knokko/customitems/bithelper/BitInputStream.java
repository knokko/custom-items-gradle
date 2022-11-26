package nl.knokko.customitems.bithelper;

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
