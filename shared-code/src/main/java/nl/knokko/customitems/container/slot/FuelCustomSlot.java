package nl.knokko.customitems.container.slot;

import java.util.Objects;
import java.util.function.Function;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FuelCustomSlot implements CustomSlot {
	
	public static FuelCustomSlot load1(
			BitInput input, 
			Function<String, CustomFuelRegistry> fuelRegistryByName) {
		
		String name = input.readString();
		CustomFuelRegistry fuelRegistry = fuelRegistryByName.apply(input.readString());
		
		// Use an empty placeholder by default, because there were no placeholders
		// in the first fuel encoding
		SlotDisplay placeholder = null;
		return new FuelCustomSlot(name, fuelRegistry, placeholder);
	}
	
	public static FuelCustomSlot load2(
			BitInput input, 
			Function<String, CustomFuelRegistry> fuelRegistryByName,
			Function<String, CustomItem> itemByName
	) throws UnknownEncodingException {
		
		String name = input.readString();
		CustomFuelRegistry fuelRegistry = fuelRegistryByName.apply(input.readString());
		SlotDisplay placeholder;
		if (input.readBoolean()) {
			placeholder = SlotDisplay.load(input, itemByName);
		} else {
			placeholder = null;
		}
		
		return new FuelCustomSlot(name, fuelRegistry, placeholder);
	}
	
	private final String name;
	
	private final CustomFuelRegistry fuelRegistry;
	
	private final SlotDisplay placeholder;
	
	public FuelCustomSlot(String name, CustomFuelRegistry fuelRegistry, SlotDisplay placeholder) {
		this.name = name;
		this.fuelRegistry = fuelRegistry;
		this.placeholder = placeholder;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof FuelCustomSlot) {
			FuelCustomSlot slot = (FuelCustomSlot) other;
			return name.equals(slot.name) && fuelRegistry == slot.fuelRegistry && Objects.equals(placeholder, slot.placeholder);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "FuelSlot " + name + " registry " + fuelRegistry + " placeholder " + placeholder;
	}
	
	@Override
	public void save(BitOutput output) {
		save2(output);
	}
	
	@SuppressWarnings("unused")
	private void save1(BitOutput output) {
		output.addByte(CustomSlot.Encodings.FUEL1);
		output.addString(name);
		output.addString(fuelRegistry.getName());
	}
	
	private void save2(BitOutput output) {
		output.addByte(CustomSlot.Encodings.FUEL2);
		output.addString(name);
		output.addString(fuelRegistry.getName());
		output.addBoolean(placeholder != null);
		if (placeholder != null) {
			placeholder.save(output);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public CustomFuelRegistry getRegistry() {
		return fuelRegistry;
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
		return new FuelCustomSlot(name + counter, fuelRegistry, placeholder);
	}

	private boolean tryName(String fuelSlotName, CustomSlot[][] existingSlots) {
		for (CustomSlot[] row : existingSlots) {
			for (CustomSlot slot : row) {
				if (slot instanceof FuelCustomSlot && ((FuelCustomSlot)slot).getName().equals(fuelSlotName)) {
					return false;
				}
			}
		}
		return true;
	}
}
