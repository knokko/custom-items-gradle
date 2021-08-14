package nl.knokko.customitems.container.slot;

import java.util.Objects;
import java.util.function.Function;

import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class OutputCustomSlot implements CustomSlot {
	
	public static OutputCustomSlot load1(BitInput input) {
		String name = input.readString();
		
		// There were no placeholders in this encoding
		SlotDisplay placeholder = null;
		
		return new OutputCustomSlot(name, placeholder);
	}
	
	public static OutputCustomSlot load2(
			BitInput input,
			Function<String, CustomItem> itemByName
	) throws UnknownEncodingException {
		String name = input.readString();
		SlotDisplay placeholder;
		if (input.readBoolean()) {
			placeholder = SlotDisplay.load(input, itemByName);
		} else {
			placeholder = null;
		}
		
		return new OutputCustomSlot(name, placeholder);
	}
	
	private final String name;
	private final SlotDisplay placeholder;
	
	public OutputCustomSlot(String name, SlotDisplay placeholder) {
		this.name = name;
		this.placeholder = placeholder;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof OutputCustomSlot) {
			OutputCustomSlot slot = (OutputCustomSlot) other;
			return name.equals(slot.name) && Objects.equals(placeholder, slot.placeholder);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return name + " with placeholder " + placeholder;
	}

	@Override
	public void save(BitOutput output) {
		save2(output);
	}
	
	@SuppressWarnings("unused")
	private void save1(BitOutput output) {
		output.addByte(CustomSlot.Encodings.OUTPUT1);
		output.addString(name);
	}
	
	private void save2(BitOutput output) {
		output.addByte(CustomSlot.Encodings.OUTPUT2);
		output.addString(name);
		output.addBoolean(placeholder != null);
		if (placeholder != null) {
			placeholder.save(output);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public SlotDisplay getPlaceholder() {
		return placeholder;
	}

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return true;
	}

	@Override
	public CustomSlot safeClone(CustomSlot[][] existingSlots) {
		
		// This can only return true if this slot no longer exists
		if (tryName(name, existingSlots)) {
			return this;
		}
		
		// Try name0, name1, name2... until it finds a free name
		int counter = 0;
		while (!tryName(name + counter, existingSlots)) {
			counter++;
		}
		return new OutputCustomSlot(name + counter, placeholder);
	}

	private boolean tryName(String outputSlotName, CustomSlot[][] existingSlots) {
		for (CustomSlot[] row : existingSlots) {
			for (CustomSlot slot : row) {
				if (slot instanceof OutputCustomSlot && ((OutputCustomSlot)slot).getName().equals(outputSlotName)) {
					return false;
				}
			}
		}
		return true;
	}
}
