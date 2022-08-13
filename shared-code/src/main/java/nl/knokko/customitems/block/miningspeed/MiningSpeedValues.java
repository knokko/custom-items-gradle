package nl.knokko.customitems.block.miningspeed;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class MiningSpeedValues extends ModelValues {

    static void validateValue(int value) throws ValidationException {
        if (value < -4) throw new ValidationException("Value must be at least -2");
        if (value > 25) throw new ValidationException("Value can be at most 25");
    }

    public static MiningSpeedValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("MiningSpeed", encoding);

        MiningSpeedValues result = new MiningSpeedValues(false);
        result.defaultValue = input.readInt();

        int numVanillaEntries = input.readInt();
        result.vanillaEntries = new ArrayList<>(numVanillaEntries);
        for (int counter = 0; counter < numVanillaEntries; counter++) {
            result.vanillaEntries.add(VanillaMiningSpeedEntry.load(input));
        }

        int numCustomEntries = input.readInt();
        result.customEntries = new ArrayList<>(numCustomEntries);
        for (int counter = 0; counter < numCustomEntries; counter++) {
            result.customEntries.add(CustomMiningSpeedEntry.load(input, itemSet));
        }

        return result;
    }

    private int defaultValue;

    private Collection<VanillaMiningSpeedEntry> vanillaEntries;
    private Collection<CustomMiningSpeedEntry> customEntries;

    public MiningSpeedValues(boolean mutable) {
        super(mutable);

        this.defaultValue = 0;
        this.vanillaEntries = new ArrayList<>();
        this.customEntries = new ArrayList<>();
    }

    public MiningSpeedValues(MiningSpeedValues toCopy, boolean mutable) {
        super(mutable);

        this.defaultValue = toCopy.getDefaultValue();
        this.vanillaEntries = toCopy.getVanillaEntries();
        this.customEntries = toCopy.getCustomEntries();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(defaultValue);

        output.addInt(vanillaEntries.size());
        for (VanillaMiningSpeedEntry vanillaEntry : vanillaEntries) {
            vanillaEntry.save(output);
        }

        output.addInt(customEntries.size());
        for (CustomMiningSpeedEntry customEntry : customEntries) {
            customEntry.save(output);
        }
    }

    @Override
    public MiningSpeedValues copy(boolean mutable) {
        return new MiningSpeedValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MiningSpeedValues) {
            MiningSpeedValues otherSpeed = (MiningSpeedValues) other;
            return this.defaultValue == otherSpeed.defaultValue && this.vanillaEntries.equals(otherSpeed.vanillaEntries)
                    && this.customEntries.equals(otherSpeed.customEntries);
        } else {
            return false;
        }
    }

    public int getSpeedFor(CIMaterial material, CustomItemValues item) {

        if (item != null) {
            for (CustomMiningSpeedEntry customEntry : customEntries) {
                if (customEntry.getItem().getName().equals(item.getName())) {
                    return customEntry.getValue();
                }
            }
        }

        for (VanillaMiningSpeedEntry vanillaEntry : vanillaEntries) {
            if (vanillaEntry.getMaterial() == material && (vanillaEntry.shouldAcceptCustomItems() || item == null)) {
                return vanillaEntry.getValue();
            }
        }

        return defaultValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public Collection<VanillaMiningSpeedEntry> getVanillaEntries() {
        return vanillaEntries;
    }

    public Collection<CustomMiningSpeedEntry> getCustomEntries() {
        return customEntries;
    }

    public void setDefaultValue(int defaultValue) {
        assertMutable();
        this.defaultValue = defaultValue;
    }

    public void setVanillaEntries(Collection<VanillaMiningSpeedEntry> vanillaEntries) {
        assertMutable();
        Checks.nonNull(vanillaEntries);
        this.vanillaEntries = Mutability.createDeepCopy(vanillaEntries, false);
    }

    public void setCustomEntries(Collection<CustomMiningSpeedEntry> customEntries) {
        assertMutable();
        Checks.nonNull(customEntries);
        this.customEntries = Mutability.createDeepCopy(customEntries, false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateValue(defaultValue);

        if (vanillaEntries == null) throw new ProgrammingValidationException("No vanilla entries");
        for (VanillaMiningSpeedEntry vanillaEntry : vanillaEntries) {
            if (vanillaEntry == null) throw new ProgrammingValidationException("Missing a vanilla entry");
            Validation.scope("Vanilla entry", vanillaEntry::validate);
        }

        if (customEntries == null) throw new ProgrammingValidationException("No custom entries");
        for (CustomMiningSpeedEntry customEntry : customEntries) {
            if (customEntry == null) throw new ProgrammingValidationException("Missing a custom entry");
            Validation.scope("Custom entry", customEntry::validate, itemSet);
        }
    }
}
