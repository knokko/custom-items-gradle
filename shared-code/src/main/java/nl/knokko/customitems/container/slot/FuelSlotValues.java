package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.FuelRegistryReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Collection;
import java.util.Objects;

public class FuelSlotValues extends ContainerSlotValues {

    static FuelSlotValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        FuelSlotValues result = new FuelSlotValues(false);

        if (encoding == Encodings.FUEL1) {
            result.load1(input, itemSet);
            result.initDefaults1();
        } else if (encoding == Encodings.FUEL2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("FuelSlot", encoding);
        }

        return result;
    }

    public static FuelSlotValues createQuick(String name, FuelRegistryReference fuelRegistry, SlotDisplayValues placeholder) {
        FuelSlotValues result = new FuelSlotValues(true);
        result.setName(name);
        result.setFuelRegistry(fuelRegistry);
        result.setPlaceholder(placeholder);
        return result;
    }

    private String name;
    private FuelRegistryReference fuelRegistry;
    private SlotDisplayValues placeholder;

    public FuelSlotValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.fuelRegistry = null;
        this.placeholder = null;
    }

    public FuelSlotValues(FuelSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.fuelRegistry = toCopy.getFuelRegistryReference();
        this.placeholder = toCopy.getPlaceholder();
    }

    private void load1(BitInput input, SItemSet itemSet) {
        this.name = input.readString();
        this.fuelRegistry = itemSet.getFuelRegistryReference(input.readString());
    }

    private void initDefaults1() {
        this.placeholder = null;
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load1(input, itemSet);
        if (input.readBoolean()) {
            this.placeholder = SlotDisplayValues.load(input, itemSet);
        } else {
            this.placeholder = null;
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.FUEL2);
        output.addString(name);
        output.addString(fuelRegistry.get().getName());
        output.addBoolean(placeholder != null);
        if (placeholder != null) {
            placeholder.save(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FuelSlotValues) {
            FuelSlotValues otherSlot = (FuelSlotValues) other;
            return this.name.equals(otherSlot.name) && this.fuelRegistry.equals(otherSlot.fuelRegistry)
                    && Objects.equals(this.placeholder, otherSlot.placeholder);
        } else {
            return false;
        }
    }
    @Override
    public FuelSlotValues copy(boolean mutable) {
        return new FuelSlotValues(this, mutable);
    }

    public String getName() {
        return name;
    }

    public FuelRegistryReference getFuelRegistryReference() {
        return fuelRegistry;
    }

    public FuelRegistryValues getFuelRegistry() {
        return fuelRegistry == null ? null : fuelRegistry.get();
    }

    public SlotDisplayValues getPlaceholder() {
        return placeholder;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setFuelRegistry(FuelRegistryReference fuelRegistry) {
        assertMutable();
        Checks.notNull(fuelRegistry);
        this.fuelRegistry = fuelRegistry;
    }

    public void setPlaceholder(SlotDisplayValues placeholder) {
        assertMutable();
        this.placeholder = placeholder == null ? null : placeholder.copy(false);
    }

    @Override
    public boolean canInsertItems() {
        return true;
    }

    @Override
    public boolean canTakeItems() {
        return true;
    }

    @Override
    public void validate(SItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (otherSlots.stream().anyMatch(slot -> slot instanceof FuelSlotValues && name.equals(((FuelSlotValues) slot).name))) {
            throw new ValidationException("Another fuel slot has the same name");
        }
        if (fuelRegistry == null) throw new ValidationException("You need to choose a fuel registry");
        if (!itemSet.isReferenceValid(fuelRegistry)) {
            throw new ProgrammingValidationException("Fuel registry is no longer valid");
        }
        // Note: placeholder is optional
        if (placeholder != null) {
            Validation.scope("Placeholder", () -> placeholder.validate(itemSet));
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (placeholder != null) {
            Validation.scope("Placeholder", () -> placeholder.validateExportVersion(version));
        }
    }
}
