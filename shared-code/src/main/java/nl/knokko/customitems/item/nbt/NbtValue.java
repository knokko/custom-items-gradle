package nl.knokko.customitems.item.nbt;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class NbtValue {
	
	static NbtValue load1(BitInput input) throws UnknownEncodingException {
		byte typeOrdinal = input.readByte();
		NbtValueType type = NbtValueType.values()[typeOrdinal];
		if (type == NbtValueType.INTEGER) {
			return new NbtValue(input.readInt());
		} else if (type == NbtValueType.STRING) {
			return new NbtValue(input.readString());
		} else {
			throw new UnknownEncodingException("NbtValue.Type", typeOrdinal);
		}
	}
	
	private final Object value;
	private final NbtValueType type;

	public NbtValue(Object value) {
		this.value = value;
		if (value instanceof Integer) {
			type = NbtValueType.INTEGER;
		} else if (value instanceof String) {
			type = NbtValueType.STRING;
		} else {
			throw new IllegalArgumentException("Unsupported value class: " + value.getClass());
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof NbtValue) {
			NbtValue otherValue = (NbtValue) other;
			return value.equals(otherValue.value) && type == otherValue.type;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return value.toString();
	}
	
	public NbtValueType getType() {
		return type;
	}

	public int getIntValue() {
		if (type != NbtValueType.INTEGER) {
			throw new UnsupportedOperationException("This can only be used on integer values");
		}
		return (Integer) value;
	}
	
	public String getStringValue() {
		if (type != NbtValueType.STRING) {
			throw new UnsupportedOperationException("This can only be used on string values");
		}
		return (String) value;
	}
	
	void save1(BitOutput output) {
		output.addByte((byte) type.ordinal());
		if (type == NbtValueType.INTEGER) {
			output.addInt(getIntValue());
		} else if (type == NbtValueType.STRING) {
			output.addString(getStringValue());
		} else {
			throw new Error("Unknown NbtValueType: " + type);
		}
	}
}
