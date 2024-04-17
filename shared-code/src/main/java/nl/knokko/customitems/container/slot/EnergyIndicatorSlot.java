package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.EnergyTypeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;
import java.util.UUID;

public class EnergyIndicatorSlot extends ContainerSlot {

    static EnergyIndicatorSlot loadEnergy(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EnergyIndicatorSlot", encoding);

        EnergyIndicatorSlot result = new EnergyIndicatorSlot(false);
        result.energyType = itemSet.energyTypes.getReference(new UUID(input.readLong(), input.readLong()));
        result.display = SlotDisplay.load(input, itemSet);
        result.placeholder = SlotDisplay.load(input, itemSet);
        result.indicatorDomain = IndicatorDomain.load(input);
        return result;
    }

    private EnergyTypeReference energyType;
    private SlotDisplay display;
    private SlotDisplay placeholder;
    private IndicatorDomain indicatorDomain;

    public static EnergyIndicatorSlot createQuick(
            EnergyTypeReference energyType, SlotDisplay display,
            SlotDisplay placeholder, IndicatorDomain indicatorDomain
    ) {
        EnergyIndicatorSlot result = new EnergyIndicatorSlot(true);
        result.setEnergyType(energyType);
        result.setDisplay(display);
        result.setPlaceholder(placeholder);
        result.setIndicatorDomain(indicatorDomain);
        return result;
    }

    public EnergyIndicatorSlot(boolean mutable) {
        super(mutable);
        this.energyType = null;
        this.display = new SlotDisplay(false);
        this.placeholder = new SlotDisplay(false);
        this.indicatorDomain = new IndicatorDomain();
    }

    public EnergyIndicatorSlot(EnergyIndicatorSlot toCopy, boolean mutable) {
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
    public EnergyIndicatorSlot copy(boolean mutable) {
        return new EnergyIndicatorSlot(this, mutable);
    }

    @Override
    public EnergyIndicatorSlot nonConflictingCopy(KciContainer container) {
        return this.copy(true);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnergyIndicatorSlot) {
            EnergyIndicatorSlot otherSlot = (EnergyIndicatorSlot) other;
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

    public EnergyType getEnergyType() {
        return energyType != null ? energyType.get() : null;
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

    public void setEnergyType(EnergyTypeReference energyType) {
        assertMutable();
        Checks.notNull(energyType);
        this.energyType = energyType;
    }

    public void setDisplay(SlotDisplay display) {
        Checks.notNull(display);
        assertMutable();
        this.display = display;
    }

    public void setPlaceholder(SlotDisplay placeholder) {
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (energyType == null) throw new ProgrammingValidationException("No energy type");
        if (!itemSet.energyTypes.isValid(energyType)) throw new ProgrammingValidationException("Energy type is invalid");
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
