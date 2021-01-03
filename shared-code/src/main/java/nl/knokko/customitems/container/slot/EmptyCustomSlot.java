package nl.knokko.customitems.container.slot;

import nl.knokko.util.bits.BitOutput;

public class EmptyCustomSlot implements CustomSlot {

	@Override
	public boolean canInsertItems() {
		return false;
	}

	@Override
	public boolean canTakeItems() {
		return false;
	}
	
	@Override
	public void save(BitOutput output) {
		output.addByte(CustomSlot.Encodings.EMPTY);
	}

	@Override
	public CustomSlot safeClone(CustomSlot[][] existingSlots) {
		// It doesn't matter if 2 slots point to the same EmptyCustomSlot instance
		return this;
	}
}
