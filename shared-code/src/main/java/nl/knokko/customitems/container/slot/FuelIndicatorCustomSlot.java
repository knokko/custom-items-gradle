package nl.knokko.customitems.container.slot;

import java.util.function.Function;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FuelIndicatorCustomSlot implements CustomSlot {
	
	public static FuelIndicatorCustomSlot load1(
			BitInput input, Function<String, CustomItem> itemByName) 
					throws UnknownEncodingException {
		
		String fuelSlotName = input.readString();
		SlotDisplay display = SlotDisplay.load(input, itemByName);
		SlotDisplay placeholder = SlotDisplay.load(input, itemByName);
		IndicatorDomain domain = IndicatorDomain.load(input);
		
		return new FuelIndicatorCustomSlot(fuelSlotName, display, placeholder, domain);
	}
	
	private final String fuelSlotName;
	
	private final SlotDisplay display;
	private final SlotDisplay placeholder;
	private final IndicatorDomain domain;
	
	public FuelIndicatorCustomSlot(String fuelSlotName, SlotDisplay display,
			SlotDisplay placeholder, IndicatorDomain domain) {
		this.fuelSlotName = fuelSlotName;
		this.display = display;
		this.placeholder = placeholder;
		this.domain = domain;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.FUEL_INDICATOR1);
		output.addString(fuelSlotName);
		display.save(output);
		placeholder.save(output);
		domain.save(output);
	}
	
	public String getFuelSlotName() {
		return fuelSlotName;
	}
	
	public SlotDisplay getDisplay() {
		return display;
	}
	
	public SlotDisplay getPlaceholder() {
		return placeholder;
	}
	
	public IndicatorDomain getDomain() {
		return domain;
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
		// Its allowed to have two identical fuel indicator slots
		return this;
	}
}
