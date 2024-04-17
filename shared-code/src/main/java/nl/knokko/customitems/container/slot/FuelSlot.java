package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.fuel.ContainerFuelRegistry;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.FuelRegistryReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;
import java.util.Objects;

public class FuelSlot extends ContainerSlot {

    static FuelSlot load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        FuelSlot result = new FuelSlot(false);

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

    public static FuelSlot createQuick(String name, FuelRegistryReference fuelRegistry, SlotDisplay placeholder) {
        FuelSlot result = new FuelSlot(true);
        result.setName(name);
        result.setFuelRegistry(fuelRegistry);
        result.setPlaceholder(placeholder);
        return result;
    }

    private String name;
    private FuelRegistryReference fuelRegistry;
    private SlotDisplay placeholder;

    public FuelSlot(boolean mutable) {
        super(mutable);
        this.name = "";
        this.fuelRegistry = null;
        this.placeholder = null;
    }

    public FuelSlot(FuelSlot toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.fuelRegistry = toCopy.getFuelRegistryReference();
        this.placeholder = toCopy.getPlaceholder();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        this.name = input.readString();
        this.fuelRegistry = itemSet.fuelRegistries.getReference(input.readString());
    }

    private void initDefaults1() {
        this.placeholder = null;
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load1(input, itemSet);
        if (input.readBoolean()) {
            this.placeholder = SlotDisplay.load(input, itemSet);
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
        if (other instanceof FuelSlot) {
            FuelSlot otherSlot = (FuelSlot) other;
            return this.name.equals(otherSlot.name) && this.fuelRegistry.equals(otherSlot.fuelRegistry)
                    && Objects.equals(this.placeholder, otherSlot.placeholder);
        } else {
            return false;
        }
    }
    @Override
    public FuelSlot copy(boolean mutable) {
        return new FuelSlot(this, mutable);
    }

    @Override
    public FuelSlot nonConflictingCopy(KciContainer container) {
        int suffixInt = 0;
        String[] pSuffix = {""};
        while (container.createSlotList().stream().anyMatch(
                slot -> slot instanceof FuelSlot && ((FuelSlot) slot).getName().equals(name + pSuffix[0])
        )) {
            suffixInt += 1;
            pSuffix[0] = Integer.toString(suffixInt);
        }
        return createQuick(name + pSuffix[0], fuelRegistry, placeholder);
    }

    public String getName() {
        return name;
    }

    public FuelRegistryReference getFuelRegistryReference() {
        return fuelRegistry;
    }

    public ContainerFuelRegistry getFuelRegistry() {
        return fuelRegistry == null ? null : fuelRegistry.get();
    }

    public SlotDisplay getPlaceholder() {
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

    public void setPlaceholder(SlotDisplay placeholder) {
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (otherSlots.stream().anyMatch(slot -> slot instanceof FuelSlot && name.equals(((FuelSlot) slot).name))) {
            throw new ValidationException("Another fuel slot has the same name");
        }
        if (fuelRegistry == null) throw new ValidationException("You need to choose a fuel registry");
        if (!itemSet.fuelRegistries.isValid(fuelRegistry)) {
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
