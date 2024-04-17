package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.IndicatorDomain;
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

public class ProgressIndicatorSlot extends ContainerSlot {

    static ProgressIndicatorSlot load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        ProgressIndicatorSlot result = new ProgressIndicatorSlot(false);

        if (encoding == Encodings.PROGRESS_INDICATOR1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("ProgressIndicatorSlot", encoding);
        }

        return result;
    }

    public static ProgressIndicatorSlot createQuick(
            SlotDisplay display, SlotDisplay placeholder, IndicatorDomain indicatorDomain
    ) {
        ProgressIndicatorSlot result = new ProgressIndicatorSlot(true);
        result.setDisplay(display);
        result.setPlaceholder(placeholder);
        result.setIndicatorDomain(indicatorDomain);
        return result;
    }

    private SlotDisplay display;
    private SlotDisplay placeholder;
    private IndicatorDomain indicatorDomain;

    public ProgressIndicatorSlot(boolean mutable) {
        super(mutable);
        this.display = new SlotDisplay(false);
        this.placeholder = new SlotDisplay(false);
        this.indicatorDomain = new IndicatorDomain();
    }

    public ProgressIndicatorSlot(ProgressIndicatorSlot toCopy, boolean mutable) {
        super(mutable);
        this.display = toCopy.getDisplay();
        this.placeholder = toCopy.getPlaceholder();
        this.indicatorDomain = toCopy.getIndicatorDomain();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.display = SlotDisplay.load(input, itemSet);
        this.placeholder = SlotDisplay.load(input, itemSet);
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
        if (other instanceof ProgressIndicatorSlot) {
            ProgressIndicatorSlot otherSlot = (ProgressIndicatorSlot) other;
            return this.display.equals(otherSlot.display) && this.placeholder.equals(otherSlot.placeholder)
                    && this.indicatorDomain.equals(otherSlot.indicatorDomain);
        } else {
            return false;
        }
    }

    @Override
    public ProgressIndicatorSlot copy(boolean mutable) {
        return new ProgressIndicatorSlot(this, mutable);
    }

    @Override
    public ProgressIndicatorSlot nonConflictingCopy(KciContainer container) {
        return this.copy(true);
    }

    public SlotDisplay getDisplay() {
        return display;
    }

    public SlotDisplay getPlaceholder() {
        return placeholder;
    }

    public IndicatorDomain getIndicatorDomain() {
        return indicatorDomain;
    }

    public void setDisplay(SlotDisplay display) {
        assertMutable();
        Checks.notNull(display);
        this.display = display.copy(false);
    }

    public void setPlaceholder(SlotDisplay placeholder) {
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
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

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Display", () -> display.validateExportVersion(version));
        Validation.scope("Placeholder", () -> placeholder.validateExportVersion(version));
    }
}
