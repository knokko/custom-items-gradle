package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;
import java.util.Objects;

public class StorageSlot extends ContainerSlot {

    static StorageSlot load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        StorageSlot result = new StorageSlot(false);

        if (encoding == Encodings.STORAGE1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("StorageSlot", encoding);
        }

        return result;
    }

    public static StorageSlot createQuick(SlotDisplay placeholder) {
        StorageSlot result = new StorageSlot(true);
        result.setPlaceholder(placeholder);
        return result;
    }

    private SlotDisplay placeholder;

    public StorageSlot(boolean mutable) {
        super(mutable);
        this.placeholder = null;
    }

    public StorageSlot(StorageSlot toCopy, boolean mutable) {
        super(mutable);
        this.placeholder = toCopy.getPlaceholder();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        if (input.readBoolean()) {
            this.placeholder = SlotDisplay.load(input, itemSet);
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
        if (other instanceof StorageSlot) {
            return Objects.equals(this.placeholder, ((StorageSlot) other).placeholder);
        } else {
            return false;
        }
    }

    @Override
    public StorageSlot copy(boolean mutable) {
        return new StorageSlot(this, mutable);
    }

    @Override
    public StorageSlot nonConflictingCopy(KciContainer container) {
        return this.copy(true);
    }

    public SlotDisplay getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(SlotDisplay placeholder) {
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
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
