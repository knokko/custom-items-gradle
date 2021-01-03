package nl.knokko.customitems.container.slot.display;

import java.util.function.Function;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomItemDisplayItem implements SlotDisplayItem {
	
	public static CustomItemDisplayItem load1(BitInput input, 
			Function<String, CustomItem> itemByName) {
		CustomItem item = itemByName.apply(input.readString());
		return new CustomItemDisplayItem(item);
	}

	private final CustomItem item;
	
	public CustomItemDisplayItem(CustomItem item) {
		this.item = item;
	}
	
	@Override
	public String toString() {
		return item.getName();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof CustomItemDisplayItem && 
				((CustomItemDisplayItem)other).item == item;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(SlotDisplayItem.Encodings.CUSTOM1);
		output.addString(item.getName());
	}
	
	public CustomItem getItem() {
		return item;
	}
}
