package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.IndicatorDomain;
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

public class ProgressIndicatorSlotValues extends ContainerSlotValues {

    static ProgressIndicatorSlotValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        ProgressIndicatorSlotValues result = new ProgressIndicatorSlotValues(false);

        if (encoding == Encodings.PROGRESS_INDICATOR1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("ProgressIndicatorSlot", encoding);
        }

        return result;
    }

    public static ProgressIndicatorSlotValues createQuick(
            SlotDisplayValues display, SlotDisplayValues placeholder, IndicatorDomain indicatorDomain
    ) {
        ProgressIndicatorSlotValues result = new ProgressIndicatorSlotValues(true);
        result.setDisplay(display);
        result.setPlaceholder(placeholder);
        result.setIndicatorDomain(indicatorDomain);
        return result;
    }

    private SlotDisplayValues display;
    private SlotDisplayValues placeholder;
    private IndicatorDomain indicatorDomain;

    public ProgressIndicatorSlotValues(boolean mutable) {
        super(mutable);
        this.display = new SlotDisplayValues(false);
        this.placeholder = new SlotDisplayValues(false);
        this.indicatorDomain = new IndicatorDomain();
    }

    public ProgressIndicatorSlotValues(ProgressIndicatorSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.display = toCopy.getDisplay();
        this.placeholder = toCopy.getPlaceholder();
        this.indicatorDomain = toCopy.getIndicatorDomain();
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.display = SlotDisplayValues.load(input, itemSet);
        this.placeholder = SlotDisplayValues.load(input, itemSet);
        this.indicatorDomain = IndicatorDomain.load(input);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.PROGRESS_INDICATOR1);
        display.save(output);
        placeholder.save(output);
        indicatorDomain.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ProgressIndicatorSlotValues) {
            ProgressIndicatorSlotValues otherSlot = (ProgressIndicatorSlotValues) other;
            return this.display.equals(otherSlot.display) && this.placeholder.equals(otherSlot.placeholder)
                    && this.indicatorDomain.equals(otherSlot.indicatorDomain);
        } else {
            return false;
        }
    }

    @Override
    public ProgressIndicatorSlotValues copy(boolean mutable) {
        return new ProgressIndicatorSlotValues(this, mutable);
    }

    public SlotDisplayValues getDisplay() {
        return display;
    }

    public SlotDisplayValues getPlaceholder() {
        return placeholder;
    }

    public IndicatorDomain getIndicatorDomain() {
        return indicatorDomain;
    }

    public void setDisplay(SlotDisplayValues display) {
        assertMutable();
        Checks.notNull(display);
        this.display = display.copy(false);
    }

    public void setPlaceholder(SlotDisplayValues placeholder) {
        assertMutable();
        Checks.notNull(placeholder);
        this.placeholder = placeholder.copy(false);
    }

    public void setIndicatorDomain(IndicatorDomain indicatorDomain) {
        assertMutable();
        Checks.notNull(indicatorDomain);
        this.indicatorDomain = indicatorDomain;
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
        if (display == null) throw new ProgrammingValidationException("No display");
        Validation.scope("Display", display::validate, itemSet);
        if (placeholder == null) throw new ProgrammingValidationException("No placeholder");
        Validation.scope("Placeholder", placeholder::validate, itemSet);
        if (indicatorDomain == null) throw new ProgrammingValidationException("No indicator domain");
        if (indicatorDomain.getBegin() < 0) throw new ValidationException("Indicator begin can't be negative");
        if (indicatorDomain.getEnd() > IndicatorDomain.MAX) {
            throw new ValidationException("Indicator end can be at most " + IndicatorDomain.MAX);
        }
    }
}
