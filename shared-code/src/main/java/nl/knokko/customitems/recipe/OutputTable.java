package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class OutputTable extends ModelValues {

    public static OutputTable load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        OutputTable result = new OutputTable(false);
        int numEntries = input.readByte();
        result.entries = new ArrayList<>(numEntries);

        for (int counter = 0; counter < numEntries; counter++) {
            Entry entry = new Entry(false);
            entry.load1(input, itemSet);
            result.entries.add(entry);
        }
        return result;
    }

    public static OutputTable load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        OutputTable result = new OutputTable(false);

        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("OutputTable", encoding);

        int numEntries = input.readInt();
        result.entries = new ArrayList<>(numEntries);
        for (int counter = 0; counter < numEntries; counter++) {
            Entry entry = new Entry(false);
            entry.load(input, itemSet);
            result.entries.add(entry);
        }

        return result;
    }

    public static OutputTable createQuick(Entry... entries) {
        Collection<Entry> entryList = new ArrayList<>(entries.length);
        Collections.addAll(entryList, entries);
        return createQuick(entryList);
    }

    public static OutputTable createQuick(Collection<Entry> entries) {
        OutputTable result = new OutputTable(true);
        result.setEntries(entries);
        return result;
    }

    private Collection<Entry> entries;

    public OutputTable(boolean mutable) {
        super(mutable);
        this.entries = new ArrayList<>(1);
    }

    public OutputTable(OutputTable toCopy, boolean mutable) {
        super(mutable);
        this.entries = toCopy.getEntries();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(this.entries.size());
        for (Entry entry : this.entries) {
            entry.save(output);
        }
    }

    @Override
    public OutputTable copy(boolean mutable) {
        return new OutputTable(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == OutputTable.class) {
            return this.entries.equals(((OutputTable) other).entries);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + entries + "]";
    }

    public Collection<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    public Chance getNothingChance() {
        Chance remainingChance = Chance.percentage(100);
        for (Entry entry : entries) {
            remainingChance = Chance.subtract(remainingChance, entry.getChance());
            if (remainingChance == null) return null;
        }
        return remainingChance;
    }

    private KciResult pickResult(int rawRandomChance) {
        int rawRemaining = rawRandomChance;
        for (Entry entry : entries) {
            if (entry.getChance().getRawValue() > rawRemaining) {
                return entry.getResult();
            }
            rawRemaining -= entry.getChance().getRawValue();
        }

        return null;
    }

    public KciResult pickResult(Chance chance) {
        return this.pickResult(chance.getRawValue());
    }

    public KciResult pickResult(Random random) {
        return pickResult(random.nextInt(Chance.HUNDRED_PERCENT));
    }

    public void setEntries(Collection<Entry> newEntries) {
        assertMutable();
        Checks.notNull(newEntries);
        this.entries = Mutability.createDeepCopy(newEntries, false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (entries == null) throw new ProgrammingValidationException("No entries");
        if (entries.isEmpty()) throw new ValidationException("You need at least 1 entry");
        for (Entry entry : entries) {
            if (entry == null) throw new ProgrammingValidationException("Missing an entry");
            Validation.scope("Entry " + entry, () -> entry.validate(itemSet));
        }
        if (getNothingChance() == null) {
            throw new ValidationException("The total chance can be at most 100%");
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (Entry entry : entries) {
            entry.result.validateExportVersion(version);
        }
    }

    public static class Entry extends ModelValues {

        private static KciResult createDefaultResult() {
            SimpleVanillaResult mutableResult = new SimpleVanillaResult(true);
            mutableResult.setMaterial(VMaterial.GOLD_INGOT);
            return mutableResult.copy(false);
        }

        public static Entry createQuick(KciResult result, int chancePercentage) {
            Entry entry = new Entry(true);
            entry.setResult(result);
            entry.setChance(Chance.percentage(chancePercentage));
            return entry;
        }

        public static Entry createQuick(KciResult result, Chance chance) {
            Entry entry = new Entry(true);
            entry.setResult(result);
            entry.setChance(chance);
            return entry;
        }

        private KciResult result;
        private Chance chance;

        public Entry(boolean mutable) {
            super(mutable);
            this.result = createDefaultResult();
            this.chance = Chance.percentage(30);
        }

        public Entry(Entry toCopy, boolean mutable) {
            super(mutable);
            this.result = toCopy.getResult();
            this.chance = toCopy.getChance();
        }

        private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
            this.chance = Chance.percentage(input.readByte());
            this.result = KciResult.load(input, itemSet);
        }

        private void load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
            byte encoding = input.readByte();
            if (encoding != 1) throw new UnknownEncodingException("OutputTableEntry", encoding);

            this.chance = Chance.load(input);
            this.result = KciResult.load(input, itemSet);
        }

        private void save(BitOutput output) {
            output.addByte((byte) 1);

            this.chance.save(output);
            this.result.save(output);
        }

        @Override
        public String toString() {
            return chance + " " + result;
        }

        @Override
        public Entry copy(boolean mutable) {
            return new Entry(this, mutable);
        }

        @Override
        public boolean equals(Object other) {
            if (other.getClass() == Entry.class) {
                Entry otherEntry = (Entry) other;
                return this.result.equals(otherEntry.result) && this.chance.equals(otherEntry.chance);
            } else {
                return false;
            }
        }

        public KciResult getResult() {
            return result;
        }

        public Chance getChance() {
            return chance;
        }

        public void setResult(KciResult newResult) {
            assertMutable();
            Checks.notNull(newResult);
            this.result = newResult.copy(false);
        }

        public void setChance(Chance newChance) {
            assertMutable();
            Checks.notNull(newChance);
            this.chance = newChance;
        }

        public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
            if (result == null) throw new ProgrammingValidationException("No result");
            Validation.scope("Result", () -> result.validateComplete(itemSet));
            if (chance == null) throw new ProgrammingValidationException("No chance");
        }
    }
}
