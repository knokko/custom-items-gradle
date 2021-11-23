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

public class OutputSlotValues extends ContainerSlotValues {

    static OutputSlotValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        OutputSlotValues result = new OutputSlotValues(false);

        if (encoding == Encodings.OUTPUT1) {
            result.load1(input);
            result.initDefaults1();
        } else if (encoding == Encodings.OUTPUT2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("OutputSlot", encoding);
        }

        return result;
    }

    public static OutputSlotValues createQuick(String name, SlotDisplayValues placeholder) {
        OutputSlotValues result = new OutputSlotValues(true);
        result.setName(name);
        result.setPlaceholder(placeholder);
        return result;
    }

    private String name;
    private SlotDisplayValues placeholder;

    public OutputSlotValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.placeholder = null;
    }

    public OutputSlotValues(OutputSlotValues toCopy, boolean mutable) {
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
        output.addByte(Encodings.OUTPUT2);
        output.addString(name);
        output.addBoolean(placeholder != null);
        if (placeholder != null) {
            placeholder.save(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OutputSlotValues) {
            OutputSlotValues otherSlot = (OutputSlotValues) other;
            return this.name.equals(otherSlot.name) && Objects.equals(this.placeholder, otherSlot.placeholder);
        } else {
            return false;
        }
    }

    @Override
    public OutputSlotValues copy(boolean mutable) {
        return new OutputSlotValues(this, mutable);
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
        return false;
    }

    @Override
    public boolean canTakeItems() {
        return true;
    }

    @Override
    public void validate(SItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (otherSlots.stream().anyMatch(slot -> slot instanceof OutputSlotValues && name.equals(((OutputSlotValues) slot).name))) {
            throw new ValidationException("Another output slot has the same name");
        }
        if (placeholder != null) {
            Validation.scope("Placeholder", placeholder::validate, itemSet);
        }
    }
}
