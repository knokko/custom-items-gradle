package nl.knokko.customitems.container.slot;

import java.util.function.Function;

import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DecorationCustomSlot implements CustomSlot {
	
	public static DecorationCustomSlot load1(
			BitInput input, Function<String, CustomItem> itemByName) 
					throws UnknownEncodingException {
		return new DecorationCustomSlot(SlotDisplay.load(input, itemByName));
	}
	
	private final SlotDisplay display;
	
	public DecorationCustomSlot(SlotDisplay display) {
		this.display = display;
	}
	
	public SlotDisplay getDisplay() {
		return display;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.DECORATION1);
		display.save(output);
	}

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return false;
	}

	@Override
	public CustomSlot safeClone(CustomSlot[][] existingSlots) {
		// There is no danger in having 2 identical decoration slots
		return this;
	}
}
