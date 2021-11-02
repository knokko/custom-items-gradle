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

public class FuelIndicatorSlotValues extends ContainerSlotValues {

    static FuelIndicatorSlotValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        FuelIndicatorSlotValues result = new FuelIndicatorSlotValues(false);

        if (encoding == Encodings.FUEL_INDICATOR1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("FuelIndicatorSlot", encoding);
        }

        return result;
    }

    private String fuelSlotName;
    private SlotDisplayValues display;
    private SlotDisplayValues placeholder;
    private IndicatorDomain indicatorDomain;

    public FuelIndicatorSlotValues(boolean mutable) {
        super(mutable);
        this.fuelSlotName = "";
        this.display = new SlotDisplayValues(false);
        this.placeholder = new SlotDisplayValues(false);
        this.indicatorDomain = new IndicatorDomain();
    }

    public FuelIndicatorSlotValues(FuelIndicatorSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.fuelSlotName = toCopy.getFuelSlotName();
        this.display = toCopy.getDisplay();
        this.placeholder = toCopy.getPlaceholder();
        this.indicatorDomain = toCopy.getIndicatorDomain();
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.fuelSlotName = input.readString();
        this.display = SlotDisplayValues.load(input, itemSet);
        this.placeholder = SlotDisplayValues.load(input, itemSet);
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
    public FuelIndicatorSlotValues copy(boolean mutable) {
        return new FuelIndicatorSlotValues(mutable);
    }

    public String getFuelSlotName() {
        return fuelSlotName;
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

    public void setFuelSlotName(String fuelSlotName) {
        assertMutable();
        Checks.notNull(fuelSlotName);
        this.fuelSlotName = fuelSlotName;
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
        if (fuelSlotName == null) throw new ProgrammingValidationException("No fuel slot name");
        if (fuelSlotName.isEmpty()) throw new ValidationException("Fuel slot name can't be empty");
        if (otherSlots.stream().noneMatch(slot -> slot instanceof FuelSlotValues && fuelSlotName.equals(((FuelSlotValues) slot).getName()))) {
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
}
