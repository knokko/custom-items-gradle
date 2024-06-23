package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;
import java.util.Objects;

public class OutputSlot extends ContainerSlot {

    static OutputSlot load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        OutputSlot result = new OutputSlot(false);

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

    public static OutputSlot createQuick(String name, SlotDisplay placeholder) {
        OutputSlot result = new OutputSlot(true);
        result.setName(name);
        result.setPlaceholder(placeholder);
        return result;
    }

    private String name;
    private SlotDisplay placeholder;

    public OutputSlot(boolean mutable) {
        super(mutable);
        this.name = "";
        this.placeholder = null;
    }

    public OutputSlot(OutputSlot toCopy, boolean mutable) {
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

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load1(input);
        if (input.readBoolean()) {
            this.placeholder = SlotDisplay.load(input, itemSet);
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
        if (other instanceof OutputSlot) {
            OutputSlot otherSlot = (OutputSlot) other;
            return this.name.equals(otherSlot.name) && Objects.equals(this.placeholder, otherSlot.placeholder);
        } else {
            return false;
        }
    }

    @Override
    public OutputSlot copy(boolean mutable) {
        return new OutputSlot(this, mutable);
    }

    @Override
    public OutputSlot nonConflictingCopy(KciContainer container) {
        int suffixInt = 0;
        String[] pSuffix = {""};
        while (container.createSlotList().stream().anyMatch(
                slot -> slot instanceof OutputSlot && ((OutputSlot) slot).getName().equals(name + pSuffix[0])
        )) {
            suffixInt += 1;
            pSuffix[0] = Integer.toString(suffixInt);
        }
        return createQuick(name + pSuffix[0], placeholder);
    }

    public String getName() {
        return name;
    }

    public SlotDisplay getPlaceholder() {
        return placeholder;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setPlaceholder(SlotDisplay placeholder) {
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (otherSlots.stream().anyMatch(slot -> slot instanceof OutputSlot && name.equals(((OutputSlot) slot).name))) {
            throw new ValidationException("Another output slot has the same name");
        }
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
