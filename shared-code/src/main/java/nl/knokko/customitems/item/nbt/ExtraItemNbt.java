package nl.knokko.customitems.item.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ExtraItemNbt {
	
	private static final byte ENCODING_1 = 1;
	
	public static ExtraItemNbt load(BitInput input) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == ENCODING_1) {
			return load1(input);
		} else {
			throw new UnknownEncodingException("ExtraItemNbt", encoding);
		}
	}
	
	private static ExtraItemNbt load1(BitInput input) throws UnknownEncodingException {
		int numPairs = input.readInt();
		Collection<NbtPair> pairs = new ArrayList<>(numPairs);
		for (int counter = 0; counter < numPairs; counter++) {
			pairs.add(new NbtPair(NbtKey.load1(input), NbtValue.load1(input)));
		}
		
		try {
			return new ExtraItemNbt(pairs);
		} catch (ValidationException invalid) {
			throw new IllegalArgumentException("Loaded invalid ExtraItemNbt", invalid);
		}
	}
	
	private final Collection<NbtPair> pairs;

	public ExtraItemNbt(Collection<NbtPair> pairs) throws ValidationException {
		this.pairs = new ArrayList<>(pairs);
		validate();
	}
	
	public ExtraItemNbt() {
		this.pairs = Collections.emptyList();
	}

	@Override
	public String toString() {
		return "ExtraNbt: " + pairs;
	}

	private void validate() throws ValidationException {
		Set<NbtKey> keySet = new HashSet<>(pairs.size());
		for (NbtPair pair : pairs) {
			if (!keySet.add(pair.getKey())) {
				throw new ValidationException("Duplicate key " + pair.getKey());
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ExtraItemNbt) {
			return pairs.equals(((ExtraItemNbt) other).pairs);
		} else {
			return false;
		}
	}
	
	public Collection<NbtPair> getPairs() {
		return new ArrayList<>(pairs);
	}
	
	public void save(BitOutput output) {
		save1(output);
	}
	
	private void save1(BitOutput output) {
		output.addByte(ENCODING_1);
		output.addInt(pairs.size());
		for (NbtPair pair : pairs) {
			pair.getKey().save1(output);
			pair.getValue().save1(output);
		}
	}
}
