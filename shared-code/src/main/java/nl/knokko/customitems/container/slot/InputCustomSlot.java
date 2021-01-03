package nl.knokko.customitems.container.slot;

import java.util.function.Function;

import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class InputCustomSlot implements CustomSlot {
	
	public static InputCustomSlot load1(BitInput input) {
		String name = input.readString();
		
		// Don't give a placeholder because input slots didn't have placeholders
		// in this encoding
		SlotDisplay placeholder = null;
		
		return new InputCustomSlot(name, placeholder);
	}
	
	public static InputCustomSlot load2(
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
		
		return new InputCustomSlot(name, placeholder);
	}
	
	private final String name;
	private final SlotDisplay placeholder;
	
	public InputCustomSlot(String name, SlotDisplay placeholder) {
		this.name = name;
		this.placeholder = placeholder;
	}
	
	@Override
	public void save(BitOutput output) {
		save2(output);
	}
	
	@SuppressWarnings("unused")
	private void save1(BitOutput output) {
		output.addByte(CustomSlot.Encodings.INPUT1);
		output.addString(name);
	}
	
	private void save2(BitOutput output) {
		output.addByte(CustomSlot.Encodings.INPUT2);
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
		return true;
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
		return new InputCustomSlot(name + counter, placeholder);
	}

	private boolean tryName(String inputSlotName, CustomSlot[][] existingSlots) {
		for (CustomSlot[] row : existingSlots) {
			for (CustomSlot slot : row) {
				if (slot instanceof InputCustomSlot && ((InputCustomSlot)slot).getName().equals(inputSlotName)) {
					return false;
				}
			}
		}
		return true;
	}
}
