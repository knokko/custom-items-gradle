package nl.knokko.customitems.item.nbt;

import java.util.Arrays;

import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class NbtKey {
	
	static NbtKey load1(BitInput input) {
		int numParts = input.readInt();
		String[] parts = new String[numParts];
		for (int index = 0; index < numParts; index++) {
			parts[index] = input.readString();
		}
		
		try {
			return new NbtKey(parts);
		} catch (ValidationException invalid) {
			throw new RuntimeException("Loaded invalid nbt key", invalid);
		}
	}
	
	private final String[] parts;

	public NbtKey(String... parts) throws ValidationException {
		this.parts = parts;
		
		validate();
	}
	
	private void validate() throws ValidationException {
		// Giving null is considered a programming error
		if (parts == null) {
			throw new NullPointerException("parts");
		}
		
		// Having a key with no parts is not allowed
		if (parts.length == 0) {
			throw new ValidationException("There is an empty nbt key");
		}
		
		for (String part : parts) {
			
			// Null parts are considered programming errors
			if (part == null) {
				throw new RuntimeException("A part of an nbt key is null");
			}
			
			// Empty parts are not allowed
			if (part.isEmpty()) {
				throw new ValidationException("There is an nbt key with an empty part");
			}
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof NbtKey) {
			return Arrays.deepEquals(parts, ((NbtKey) other).parts);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		for (String part : parts) {
			result += part.hashCode();
		}
		return result;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (String part : parts) {
			result += part + "$";
		}
		return result;
	}
	
	public String[] getParts() {
		return Arrays.copyOf(parts, parts.length);
	}
	
	void save1(BitOutput output) {
		output.addInt(parts.length);
		for (String part : parts) {
			output.addString(part);
		}
	}
}
