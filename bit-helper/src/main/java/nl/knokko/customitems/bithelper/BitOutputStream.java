package nl.knokko.customitems.bithelper;

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
