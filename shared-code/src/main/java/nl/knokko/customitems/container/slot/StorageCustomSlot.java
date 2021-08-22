package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Objects;
import java.util.function.Function;

public class StorageCustomSlot implements CustomSlot {

    public static StorageCustomSlot load1(
            BitInput input, Function<String, CustomItem> getItemByName
    ) throws UnknownEncodingException {

        SlotDisplay placeHolder = null;
        if (input.readBoolean()) {
            placeHolder = SlotDisplay.load(input, getItemByName);
        }

        return new StorageCustomSlot(placeHolder);
    }

    private final SlotDisplay placeHolder;

    public StorageCustomSlot(SlotDisplay placeHolder) {
        this.placeHolder = placeHolder;
    }

    public SlotDisplay getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StorageCustomSlot) {
            return Objects.equals(placeHolder, ((StorageCustomSlot) other).placeHolder);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Storage placeholder=" + placeHolder;
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
        // This class doesn't have any mutable properties, so no need for an explicit clone
        return this;
    }

    @Override
    public void save(BitOutput output) {
        save1(output);
    }

    private void save1(BitOutput output) {
        output.addByte(Encodings.STORAGE1);
        output.addBoolean(placeHolder != null);
        if (placeHolder != null) {
            placeHolder.save(output);
        }
    }
}
