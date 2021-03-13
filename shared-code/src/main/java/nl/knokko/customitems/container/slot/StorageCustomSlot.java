package nl.knokko.customitems.container.slot;

import nl.knokko.util.bits.BitOutput;

public class StorageCustomSlot implements CustomSlot {

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
        // This class doesn't have any properties, so there is no need for a proper clone
        return this;
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.STORAGE1);
        // TODO Add a placeholder
    }
}
