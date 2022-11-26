package nl.knokko.customitems.bithelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BooleanArrayBitInput extends BitInput {

	private boolean[] data;

	private int index;
	private final int boundIndex;

	public BooleanArrayBitInput(boolean... data) {
		this(data, 0, data.length);
	}

	public BooleanArrayBitInput(boolean[] data, int startIndex, int length) {
		this.data = data;
		this.index = startIndex;
		this.boundIndex = startIndex + length;
	}

	public BooleanArrayBitInput(byte[] bytes) {
		boundIndex = Math.multiplyExact(bytes.length, 8);
		data = new boolean[boundIndex];
		int i = 0;
		for (byte b : bytes)
			BitHelper.byteToBinary(b, data, 8 * (i++));
	}

	public BooleanArrayBitInput(BooleanArrayBitOutput bits) {
		this(bits.getBooleans());
	}

	public static BooleanArrayBitInput fromFile(File file) throws IOException {
		if (Math.multiplyExact(file.length(), 8) > Integer.MAX_VALUE)
			throw new IOException("File too large! (" + file.length() + ")");
		byte[] bytes = new byte[(int) file.length()];
		FileInputStream input = new FileInputStream(file);
		input.read(bytes);
		input.close();
		return new BooleanArrayBitInput(bytes);
	}

	@Override
	public boolean readDirectBoolean() {
		return data[index++];
	}

	@Override
	public byte readDirectByte() {
		index += 8;
		return BitHelper.byteFromBinary(data, index - 8);
	}

	@Override
	public void increaseCapacity(int booleans) {
		if (Math.addExact(index, booleans) > boundIndex)
			throw new RuntimeException("End of input has been exceeded!");
	}

	@Override
	public void terminate() {
		data = null;
	}

	public boolean[] getAllBits() {
		return data;
	}
}
