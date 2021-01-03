package nl.knokko.customitems.container.slot.display;

import java.util.function.Function;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public interface SlotDisplayItem {

	void save(BitOutput output);
	
	static SlotDisplayItem load(
			BitInput input, Function<String, CustomItem> itemByName
	) throws UnknownEncodingException {
		byte encoding = input.readByte();
		switch (encoding) {
		case Encodings.CUSTOM1: return CustomItemDisplayItem.load1(input, itemByName);
		case Encodings.DATA_VANILLA1: return DataVanillaDisplayItem.load1(input);
		case Encodings.SIMPLE_VANILLA1: return SimpleVanillaDisplayItem.load1(input);
		default: throw new UnknownEncodingException("SlotDisplayItem", encoding);
		}
	}
	
	static class Encodings {
		
		public static final byte CUSTOM1 = 0;
		public static final byte DATA_VANILLA1 = 1;
		public static final byte SIMPLE_VANILLA1 = 2;
	}
}
