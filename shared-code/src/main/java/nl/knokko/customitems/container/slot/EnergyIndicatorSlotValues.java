package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.EnergyTypeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.UUID;

public class EnergyIndicatorSlotValues extends ContainerSlotValues {

    static EnergyIndicatorSlotValues loadEnergy(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EnergyIndicatorSlot", encoding);

        EnergyIndicatorSlotValues result = new EnergyIndicatorSlotValues(false);
        result.energyType = itemSet.getEnergyTypeReference(new UUID(input.readLong(), input.readLong()));
        result.display = SlotDisplayValues.load(input, itemSet);
        result.placeholder = SlotDisplayValues.load(input, itemSet);
        result.indicatorDomain = IndicatorDomain.load(input);
        return result;
    }

    private EnergyTypeReference energyType;
    private SlotDisplayValues display;
    private SlotDisplayValues placeholder;
    private IndicatorDomain indicatorDomain;

    public static EnergyIndicatorSlotValues createQuick(
            EnergyTypeReference energyType, SlotDisplayValues display,
            SlotDisplayValues placeholder, IndicatorDomain indicatorDomain
    ) {
        EnergyIndicatorSlotValues result = new EnergyIndicatorSlotValues(true);
        result.setEnergyType(energyType);
        result.setDisplay(display);
        result.setPlaceholder(placeholder);
        result.setIndicatorDomain(indicatorDomain);
        return result;
    }

    public EnergyIndicatorSlotValues(boolean mutable) {
        super(mutable);
        this.energyType = null;
        this.display = new SlotDisplayValues(false);
        this.placeholder = new SlotDisplayValues(false);
        this.indicatorDomain = new IndicatorDomain();
    }

    public EnergyIndicatorSlotValues(EnergyIndicatorSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.energyType = toCopy.getEnergyTypeReference();
        this.display = toCopy.getDisplay();
        this.placeholder = toCopy.getPlaceholder();
        this.indicatorDomain = toCopy.getIndicatorDomain();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.ENERGY_INDICATOR);
        output.addByte((byte) 1);

        output.addLong(energyType.get().getId().getMostSignificantBits());
        output.addLong(energyType.get().getId().getLeastSignificantBits());
        display.save(output);
        placeholder.save(output);
        indicatorDomain.save(output);
    }

    @Override
    public EnergyIndicatorSlotValues copy(boolean mutable) {
        return new EnergyIndicatorSlotValues(this, mutable);
    }

    @Override
    public EnergyIndicatorSlotValues nonConflictingCopy(CustomContainerValues container) {
        return this.copy(true);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnergyIndicatorSlotValues) {
            EnergyIndicatorSlotValues otherSlot = (EnergyIndicatorSlotValues) other;
            return this.energyType.equals(otherSlot.energyType) && this.display.equals(otherSlot.display)
                    && this.placeholder.equals(otherSlot.placeholder) && this.indicatorDomain.equals(otherSlot.indicatorDomain);
        } else {
            return false;
        }
    }

    @Override
    public boolean canInsertItems() {
        return false;
    }

    @Override
    public boolean canTakeItems() {
        return false;
    }

    public EnergyTypeReference getEnergyTypeReference() {
        return energyType;
    }

    public EnergyTypeValues getEnergyType() {
        return energyType != null ? energyType.get() : null;
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

    public void setEnergyType(EnergyTypeReference energyType) {
        assertMutable();
        Checks.notNull(energyType);
        this.energyType = energyType;
    }

    public void setDisplay(SlotDisplayValues display) {
        Checks.notNull(display);
        assertMutable();
        this.display = display;
    }

    public void setPlaceholder(SlotDisplayValues placeholder) {
        assertMutable();
        Checks.notNull(placeholder);
        this.placeholder = placeholder;
    }

    public void setIndicatorDomain(IndicatorDomain indicatorDomain) {
        assertMutable();
        Checks.notNull(indicatorDomain);
        this.indicatorDomain = indicatorDomain;
    }

    @Override
    public void validate(ItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (energyType == null) throw new ProgrammingValidationException("No energy type");
        if (!itemSet.isReferenceValid(energyType)) throw new ProgrammingValidationException("Energy type is invalid");
        if (display == null) throw new ProgrammingValidationException("No display");
        Validation.scope("Display", display::validate, itemSet);
        if (placeholder == null) throw new ProgrammingValidationException("No placeholder");
        Validation.scope("Placeholder", placeholder::validate, itemSet);
        if (indicatorDomain == null) throw new ProgrammingValidationException("No indicator domain");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Display", () -> display.validateExportVersion(version));
        Validation.scope("Placeholder", () -> placeholder.validateExportVersion(version));
    }
}
