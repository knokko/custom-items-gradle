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

public class FuelRegistryValues extends ModelValues {

    private static class Encodings {

        static final byte ENCODING1 = 1;
    }

    public static FuelRegistryValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        FuelRegistryValues result = new FuelRegistryValues(false);

        if (encoding == Encodings.ENCODING1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("FuelRegistry", encoding);
        }

        return result;
    }

    public static FuelRegistryValues createQuick(String name, List<FuelEntryValues> entries) {
        FuelRegistryValues result = new FuelRegistryValues(true);
        result.setName(name);
        result.setEntries(entries);
        return result;
    }

    private String name;
    private List<FuelEntryValues> entries;

    public FuelRegistryValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.entries = new ArrayList<>(0);
    }

    public FuelRegistryValues(FuelRegistryValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.entries = toCopy.getEntries();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.name = input.readString();
        int numEntries = input.readInt();
        this.entries = new ArrayList<>(numEntries);
        for (int counter = 0; counter < numEntries; counter++) {
            this.entries.add(FuelEntryValues.load1(input, itemSet));
        }
    }

    public void save(BitOutput output) {
        output.addByte(Encodings.ENCODING1);
        output.addString(name);
        output.addInt(entries.size());
        for (FuelEntryValues entry : entries) {
            entry.save1(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == FuelRegistryValues.class) {
            FuelRegistryValues otherRegistry = (FuelRegistryValues) other;
            return this.name.equals(otherRegistry.name) && this.entries.equals(otherRegistry.entries);
        } else {
            return false;
        }
    }

    @Override
    public FuelRegistryValues copy(boolean mutable) {
        return new FuelRegistryValues(this, mutable);
    }

    public String getName() {
        return name;
    }

    public List<FuelEntryValues> getEntries() {
        return new ArrayList<>(entries);
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setEntries(List<FuelEntryValues> entries) {
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
        for (FuelEntryValues entry : entries) {
            if (entry == null) throw new ProgrammingValidationException("Missing an entry");
            Validation.scope("Fuel entry", entry::validate, itemSet);
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (FuelEntryValues entry : entries) {
            Validation.scope("Fuel entry", () -> entry.getFuel().validateExportVersion(version));
        }
    }
}
