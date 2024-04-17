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

public class FuelIndicatorSlot extends ContainerSlot {

    static FuelIndicatorSlot load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        FuelIndicatorSlot result = new FuelIndicatorSlot(false);

        if (encoding == Encodings.FUEL_INDICATOR1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("FuelIndicatorSlot", encoding);
        }

        return result;
    }

    public static FuelIndicatorSlot createQuick(
            String fuelSlotName, SlotDisplay display, SlotDisplay placeholder, IndicatorDomain indicatorDomain
    ) {
        FuelIndicatorSlot result = new FuelIndicatorSlot(true);
        result.setFuelSlotName(fuelSlotName);
        result.setDisplay(display);
        result.setPlaceholder(placeholder);
        result.setIndicatorDomain(indicatorDomain);
        return result;
    }

    private String fuelSlotName;
    private SlotDisplay display;
    private SlotDisplay placeholder;
    private IndicatorDomain indicatorDomain;

    public FuelIndicatorSlot(boolean mutable) {
        super(mutable);
        this.fuelSlotName = "";
        this.display = new SlotDisplay(false);
        this.placeholder = new SlotDisplay(false);
        this.indicatorDomain = new IndicatorDomain();
    }

    public FuelIndicatorSlot(FuelIndicatorSlot toCopy, boolean mutable) {
        super(mutable);
        this.fuelSlotName = toCopy.getFuelSlotName();
        this.display = toCopy.getDisplay();
        this.placeholder = toCopy.getPlaceholder();
        this.indicatorDomain = toCopy.getIndicatorDomain();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.fuelSlotName = input.readString();
        this.display = SlotDisplay.load(input, itemSet);
        this.placeholder = SlotDisplay.load(input, itemSet);
        this.indicatorDomain = IndicatorDomain.load(input);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.FUEL_INDICATOR1);
        output.addString(fuelSlotName);
        display.save(output);
        placeholder.save(output);
        indicatorDomain.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FuelIndicatorSlot) {
            FuelIndicatorSlot otherSlot = (FuelIndicatorSlot) other;
            return this.fuelSlotName.equals(otherSlot.fuelSlotName) && this.display.equals(otherSlot.display)
                    && this.placeholder.equals(otherSlot.placeholder) && this.indicatorDomain.equals(otherSlot.indicatorDomain);
        } else {
            return false;
        }
    }

    @Override
    public FuelIndicatorSlot copy(boolean mutable) {
        return new FuelIndicatorSlot(this, mutable);
    }

    @Override
    public FuelIndicatorSlot nonConflictingCopy(KciContainer container) {
        return this.copy(true);
    }

    public String getFuelSlotName() {
        return fuelSlotName;
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

    public void setFuelSlotName(String fuelSlotName) {
        assertMutable();
        Checks.notNull(fuelSlotName);
        this.fuelSlotName = fuelSlotName;
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
        if (fuelSlotName == null) throw new ProgrammingValidationException("No fuel slot name");
        if (fuelSlotName.isEmpty()) throw new ValidationException("Fuel slot name can't be empty");
        if (otherSlots.stream().noneMatch(slot -> slot instanceof FuelSlot && fuelSlotName.equals(((FuelSlot) slot).getName()))) {
            throw new ValidationException("No fuel slot has name '" + fuelSlotName + "'");
        }
        if (display == null) throw new ProgrammingValidationException("No display");
        Validation.scope("Display", () -> display.validate(itemSet));
        if (placeholder == null) throw new ProgrammingValidationException("No placeholder");
        Validation.scope("Placeholder", () -> placeholder.validate(itemSet));
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
