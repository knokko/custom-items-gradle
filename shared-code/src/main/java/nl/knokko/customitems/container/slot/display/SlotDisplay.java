package nl.knokko.customitems.container.slot.display;

import java.util.Arrays;
import java.util.function.Function;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public final class SlotDisplay {
	
	public static SlotDisplay load(
			BitInput input, 
			Function<String, CustomItem> itemByName) throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case Encodings.ENCODING1: return load1(input, itemByName);
		default: throw new UnknownEncodingException("SlotDisplay", encoding);
		}
	}
	
	private static SlotDisplay load1(
			BitInput input, Function<String, CustomItem> itemByName
	) throws UnknownEncodingException {
		
		String displayName = input.readString();
		String[] lore = new String[input.readInt()];
		for (int lineIndex = 0; lineIndex < lore.length; lineIndex++) {
			lore[lineIndex] = input.readString();
		}
		int amount = input.readInt();
		SlotDisplayItem item = SlotDisplayItem.load(input, itemByName);
		
		return new SlotDisplay(item, displayName, lore, amount);
	}
	
	protected final SlotDisplayItem item;

	protected final String displayName;
	protected final String[] lore;
	
	protected final int amount;
	
	public SlotDisplay(SlotDisplayItem item, String displayName, String[] lore, int amount) {
		this.item = item;
		this.displayName = displayName;
		this.lore = lore;
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return item.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SlotDisplay) {
			
			SlotDisplay display = (SlotDisplay) other;
			return display.item.equals(item) && display.displayName.equals(displayName) &&
					Arrays.equals(display.lore, lore) && display.amount == amount;
		} else {
			return false;
		}
	}
	
	public void save(BitOutput output) {
		save1(output);
	}
	
	private void save1(BitOutput output) {
		output.addByte(Encodings.ENCODING1);
		output.addString(displayName);
		output.addInt(lore.length);
		for (String line : lore) {
			output.addString(line);
		}
		output.addInt(amount);
		item.save(output);
	}
	
	public SlotDisplayItem getItem() {
		return item;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String[] getLore() {
		return lore;
	}
	
	public int getAmount() {
		return amount;
	}
	
	static class Encodings {
		
		static final byte ENCODING1 = 1;
	}
}
