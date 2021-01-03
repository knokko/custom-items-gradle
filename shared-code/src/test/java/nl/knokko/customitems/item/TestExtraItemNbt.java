package nl.knokko.customitems.item;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.item.nbt.NbtKey;
import nl.knokko.customitems.item.nbt.NbtPair;
import nl.knokko.customitems.item.nbt.NbtValue;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class TestExtraItemNbt {
	
	private static final byte[] SAVED_1 = {
			1, 2, 0, 0, 0, 1, 0, 0, 0, 7, 101, 0, 19, -19, 73, -65, -13, -13, -5, 
			-5, -5, -21, -5, -5, -5, 69, -13, 4, 108, -96, -69, 98, 109, -33, -2, 
			-23, -81, 19, -51, -124, 13, 3, -39, -97, -1, 65, -90, -65, -65, 82, 
			-3, -3, 121, 83, -65, 79, -44, -83, -46, -33, 16, 103, 82, -38, 86, 
			-6, -74, 51, -104
	};

	@Test
	public void testSaveLoad() throws ValidationException, UnknownEncodingException {
		
		// Construct an instance to test with
		NbtPair singleKey = new NbtPair(new NbtKey("single"), new NbtValue(1));
		NbtPair doubleKey = new NbtPair(new NbtKey("double1", "double2"), new NbtValue("22"));
		
		Collection<NbtPair> pairs = new ArrayList<>(2);
		pairs.add(singleKey);
		pairs.add(doubleKey);
		
		// Save and load that instance, and check that the loaded one is equivalent
		ExtraItemNbt nbt = new ExtraItemNbt(pairs);
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		nbt.save(output);
		output.addString("no outer corruption");
		
		BitInput input = new ByteArrayBitInput(output.getBytes());
		ExtraItemNbt loaded = ExtraItemNbt.load(input);
		assertEquals(nbt, loaded);
		assertEquals("no outer corruption", input.readString());
		
		// Also test backward compatibility
		input = new ByteArrayBitInput(SAVED_1);
		loaded = ExtraItemNbt.load(input);
		assertEquals(nbt, loaded);
		assertEquals("no outer corruption", input.readString());
	}

}
