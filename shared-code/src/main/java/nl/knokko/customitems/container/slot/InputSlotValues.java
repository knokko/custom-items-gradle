package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Collection;
import java.util.Objects;

public class InputSlotValues extends ContainerSlotValues {

    static InputSlotValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        InputSlotValues result = new InputSlotValues(false);

        if (encoding == Encodings.INPUT1) {
            result.load1(input);
            result.initDefaults1();
        } else if (encoding == Encodings.INPUT2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("InputSlot", encoding);
        }

        return result;
    }

    private String name;
    private SlotDisplayValues placeholder;

    public InputSlotValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.placeholder = null;
    }

    public InputSlotValues(InputSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.placeholder = toCopy.getPlaceholder();
    }

    private void load1(BitInput input) {
        this.name = input.readString();
    }

    private void initDefaults1() {
        this.placeholder = null;
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load1(input);
        if (input.readBoolean()) {
            this.placeholder = SlotDisplayValues.load(input, itemSet);
        } else {
            this.placeholder = null;
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.INPUT2);
        output.addString(name);
        output.addBoolean(placeholder != null);
        if (placeholder != null) {
            placeholder.save(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof InputSlotValues) {
            InputSlotValues otherSlot = (InputSlotValues) other;
            return this.name.equals(otherSlot.name) && Objects.equals(this.placeholder, otherSlot.placeholder);
        } else {
            return false;
        }
    }
    @Override
    public InputSlotValues copy(boolean mutable) {
        return new InputSlotValues(this, mutable);
    }

    public String getName() {
        return name;
    }

    public SlotDisplayValues getPlaceholder() {
        return placeholder;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
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
    public void validate(SItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (otherSlots.stream().anyMatch(slot -> slot instanceof InputSlotValues && name.equals(((InputSlotValues) slot).name))) {
            throw new ValidationException("Another input slot has the same name");
        }
        // Note: placeholder is optional
        if (placeholder != null) {
            Validation.scope("Placeholder", () -> placeholder.validate(itemSet));
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (placeholder != null) {
            Validation.scope("Placeholder", () -> placeholder.validateExportVersion(version));
        }
    }
}
