package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class ContainerFuelRegistry extends ModelValues {

    private static class Encodings {

        static final byte ENCODING1 = 1;
    }

    public static ContainerFuelRegistry load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        ContainerFuelRegistry result = new ContainerFuelRegistry(false);

        if (encoding == Encodings.ENCODING1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("FuelRegistry", encoding);
        }

        return result;
    }

    public static ContainerFuelRegistry createQuick(String name, List<ContainerFuelEntry> entries) {
        ContainerFuelRegistry result = new ContainerFuelRegistry(true);
        result.setName(name);
        result.setEntries(entries);
        return result;
    }

    private String name;
    private List<ContainerFuelEntry> entries;

    public ContainerFuelRegistry(boolean mutable) {
        super(mutable);
        this.name = "";
        this.entries = new ArrayList<>(0);
    }

    public ContainerFuelRegistry(ContainerFuelRegistry toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.entries = toCopy.getEntries();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.name = input.readString();
        int numEntries = input.readInt();
        this.entries = new ArrayList<>(numEntries);
        for (int counter = 0; counter < numEntries; counter++) {
            this.entries.add(ContainerFuelEntry.load1(input, itemSet));
        }
    }

    public void save(BitOutput output) {
        output.addByte(Encodings.ENCODING1);
        output.addString(name);
        output.addInt(entries.size());
        for (ContainerFuelEntry entry : entries) {
            entry.save1(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == ContainerFuelRegistry.class) {
            ContainerFuelRegistry otherRegistry = (ContainerFuelRegistry) other;
            return this.name.equals(otherRegistry.name) && this.entries.equals(otherRegistry.entries);
        } else {
            return false;
        }
    }

    @Override
    public ContainerFuelRegistry copy(boolean mutable) {
        return new ContainerFuelRegistry(this, mutable);
    }

    public String getName() {
        return name;
    }

    public List<ContainerFuelEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setEntries(List<ContainerFuelEntry> entries) {
        assertMutable();
        Checks.nonNull(entries);
        this.entries = Mutability.createDeepCopy(entries, false);
    }

    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (!name.equals(oldName) && itemSet.fuelRegistries.get(name).isPresent()) {
            throw new ValidationException("Another fuel registry already has this name");
        }

        if (entries == null) throw new ProgrammingValidationException("No entries");
        for (ContainerFuelEntry entry : entries) {
            if (entry == null) throw new ProgrammingValidationException("Missing an entry");
            Validation.scope("Fuel entry", entry::validate, itemSet);
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (ContainerFuelEntry entry : entries) {
            Validation.scope("Fuel entry", () -> entry.getFuel().validateExportVersion(version));
        }
    }
}
