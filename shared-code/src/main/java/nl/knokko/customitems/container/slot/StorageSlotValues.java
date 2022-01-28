package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;
import java.util.Objects;

public class StorageSlotValues extends ContainerSlotValues {

    static StorageSlotValues load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        StorageSlotValues result = new StorageSlotValues(false);

        if (encoding == Encodings.STORAGE1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("StorageSlot", encoding);
        }

        return result;
    }

    public static StorageSlotValues createQuick(SlotDisplayValues placeholder) {
        StorageSlotValues result = new StorageSlotValues(true);
        result.setPlaceholder(placeholder);
        return result;
    }

    private SlotDisplayValues placeholder;

    public StorageSlotValues(boolean mutable) {
        super(mutable);
        this.placeholder = null;
    }

    public StorageSlotValues(StorageSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.placeholder = toCopy.getPlaceholder();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        if (input.readBoolean()) {
            this.placeholder = SlotDisplayValues.load(input, itemSet);
        } else {
            this.placeholder = null;
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.STORAGE1);
        output.addBoolean(placeholder != null);
        if (placeholder != null) {
            placeholder.save(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StorageSlotValues) {
            return Objects.equals(this.placeholder, ((StorageSlotValues) other).placeholder);
        } else {
            return false;
        }
    }

    @Override
    public StorageSlotValues copy(boolean mutable) {
        return new StorageSlotValues(this, mutable);
    }

    @Override
    public StorageSlotValues nonConflictingCopy(CustomContainerValues container) {
        return this.copy(true);
    }

    public SlotDisplayValues getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(SlotDisplayValues placeholder) {
        assertMutable();
        this.placeholder = placeholder == null ? null : placeholder.copy(false);
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
    public void validate(ItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        // Note: placeholder is optional
        if (placeholder != null) {
            Validation.scope("Placeholder", placeholder::validate, itemSet);
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (placeholder != null) {
            Validation.scope("Placeholder", () -> placeholder.validateExportVersion(version));
        }
    }
}
