package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;

public class DecorationSlot extends ContainerSlot {

    static DecorationSlot load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        DecorationSlot result = new DecorationSlot(false);

        if (encoding == Encodings.DECORATION1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("DecorationSlot", encoding);
        }

        return result;
    }

    public static DecorationSlot createQuick(SlotDisplay display) {
        DecorationSlot result = new DecorationSlot(true);
        result.setDisplay(display);
        return result;
    }

    private SlotDisplay display;

    public DecorationSlot(boolean mutable) {
        super(mutable);
        this.display = new SlotDisplay(false);
    }

    public DecorationSlot(DecorationSlot toCopy, boolean mutable) {
        super(mutable);
        this.display = toCopy.getDisplay();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.display = SlotDisplay.load(input, itemSet);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.DECORATION1);
        display.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DecorationSlot) {
            return this.display.equals(((DecorationSlot) other).display);
        } else {
            return false;
        }
    }

    @Override
    public DecorationSlot copy(boolean mutable) {
        return new DecorationSlot(this, mutable);
    }

    @Override
    public DecorationSlot nonConflictingCopy(KciContainer container) {
        return this.copy(true);
    }

    public SlotDisplay getDisplay() {
        return display;
    }

    public void setDisplay(SlotDisplay display) {
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (display == null) throw new ProgrammingValidationException("No slot display");
        display.validate(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        display.validateExportVersion(version);
    }
}
