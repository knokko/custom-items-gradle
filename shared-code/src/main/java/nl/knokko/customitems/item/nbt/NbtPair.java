package nl.knokko.customitems.item.nbt;

public class NbtPair {
	
	private final NbtKey key;
	private final NbtValue value;

	public NbtPair(NbtKey key, NbtValue value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof NbtPair) {
			NbtPair otherPair = (NbtPair) other;
			return key.equals(otherPair.key) && value.equals(otherPair.value);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return key + " = " + value;
	}

	public NbtKey getKey() {
		return key;
	}
	
	public NbtValue getValue() {
		return value;
	}
}
