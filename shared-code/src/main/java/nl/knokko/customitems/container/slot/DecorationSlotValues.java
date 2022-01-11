package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;

public class DecorationSlotValues extends ContainerSlotValues {

    static DecorationSlotValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        DecorationSlotValues result = new DecorationSlotValues(false);

        if (encoding == Encodings.DECORATION1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("DecorationSlot", encoding);
        }

        return result;
    }

    public static DecorationSlotValues createQuick(SlotDisplayValues display) {
        DecorationSlotValues result = new DecorationSlotValues(true);
        result.setDisplay(display);
        return result;
    }

    private SlotDisplayValues display;

    public DecorationSlotValues(boolean mutable) {
        super(mutable);
        this.display = new SlotDisplayValues(false);
    }

    public DecorationSlotValues(DecorationSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.display = toCopy.getDisplay();
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.display = SlotDisplayValues.load(input, itemSet);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.DECORATION1);
        display.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DecorationSlotValues) {
            return this.display.equals(((DecorationSlotValues) other).display);
        } else {
            return false;
        }
    }

    @Override
    public DecorationSlotValues copy(boolean mutable) {
        return new DecorationSlotValues(this, mutable);
    }

    @Override
    public DecorationSlotValues nonConflictingCopy(ContainerSlotValues[][] currentSlots) {
        return this.copy(true);
    }

    public SlotDisplayValues getDisplay() {
        return display;
    }

    public void setDisplay(SlotDisplayValues display) {
        assertMutable();
        Checks.notNull(display);
        this.display = display.copy(false);
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
    public void validate(SItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (display == null) throw new ProgrammingValidationException("No slot display");
        display.validate(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        display.validateExportVersion(version);
    }
}
