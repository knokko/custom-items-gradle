package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

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
    public DecorationSlotValues copy(boolean mutable) {
        return new DecorationSlotValues(this, mutable);
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
}
