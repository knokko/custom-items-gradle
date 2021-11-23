package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class OutputTableValues extends ModelValues {

    public static OutputTableValues load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        OutputTableValues result = new OutputTableValues(false);
        int numEntries = input.readByte();
        result.entries = new ArrayList<>(numEntries);

        for (int counter = 0; counter < numEntries; counter++) {
            Entry entry = new Entry(false);
            entry.load1(input, itemSet);
            result.entries.add(entry);
        }
        return result;
    }

    public static OutputTableValues createQuick(Entry... entries) {
        Collection<Entry> entryList = new ArrayList<>(entries.length);
        Collections.addAll(entryList, entries);
        return createQuick(entryList);
    }

    public static OutputTableValues createQuick(Collection<Entry> entries) {
        OutputTableValues result = new OutputTableValues(true);
        result.setEntries(entries);
        return result;
    }

    private Collection<Entry> entries;

    public OutputTableValues(boolean mutable) {
        super(mutable);
        this.entries = new ArrayList<>(1);
    }

    public OutputTableValues(OutputTableValues toCopy, boolean mutable) {
        super(mutable);
        this.entries = toCopy.getEntries();
    }

    public void save1(BitOutput output) {
        output.addByte((byte) entries.size());
        for (Entry entry : entries) {
            entry.save1(output);
        }
    }

    @Override
    public OutputTableValues copy(boolean mutable) {
        return new OutputTableValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == OutputTableValues.class) {
            return this.entries.equals(((OutputTableValues) other).entries);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "OutputTable(" + entries + ")";
    }

    public Collection<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    public int getNothingChance() {
        int chance = 100;
        for (Entry entry : entries) {
            chance -= entry.getChance();
        }
        return chance;
    }

    public ResultValues pickResult(int randomChance) {
        int remaining = randomChance;
        for (Entry entry : entries) {
            if (entry.getChance() > remaining) {
                return entry.getResult();
            }
            remaining -= entry.getChance();
        }

        return null;
    }

    public ResultValues pickResult(Random random) {
        return pickResult(random.nextInt(100));
    }

    public void setEntries(Collection<Entry> newEntries) {
        assertMutable();
        Checks.notNull(newEntries);
        this.entries = Mutability.createDeepCopy(newEntries, false);
    }

    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (entries == null) throw new ProgrammingValidationException("No entries");
        if (entries.isEmpty()) throw new ValidationException("You need at least 1 entry");
        for (Entry entry : entries) {
            if (entry == null) throw new ProgrammingValidationException("Missing an entry");
            Validation.scope("Entry " + entry, () -> entry.validate(itemSet));
        }
        if (getNothingChance() < 0) {
            throw new ValidationException("The total chance can be at most 100%");
        }
    }

    public static class Entry extends ModelValues {

        private static ResultValues createDefaultResult() {
            SimpleVanillaResultValues mutableResult = new SimpleVanillaResultValues(true);
            mutableResult.setMaterial(CIMaterial.GOLD_INGOT);
            return mutableResult.copy(false);
        }

        public static Entry createQuick(ResultValues result, int chance) {
            Entry entry = new Entry(true);
            entry.setResult(result);
            entry.setChance(chance);
            return entry;
        }

        private ResultValues result;
        private int chance;

        public Entry(boolean mutable) {
            super(mutable);
            this.result = createDefaultResult();
            this.chance = 30;
        }

        public Entry(Entry toCopy, boolean mutable) {
            super(mutable);
            this.result = toCopy.getResult();
            this.chance = toCopy.getChance();
        }

        private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
            this.chance = input.readByte();
            this.result = ResultValues.load(input, itemSet);
        }

        public void save1(BitOutput output) {
            output.addByte((byte) chance);
            result.save(output);
        }

        @Override
        public String toString() {
            return chance + "% " + result;
        }

        @Override
        public Entry copy(boolean mutable) {
            return new Entry(this, mutable);
        }

        @Override
        public boolean equals(Object other) {
            if (other.getClass() == Entry.class) {
                Entry otherEntry = (Entry) other;
                return this.result.equals(otherEntry.result) && this.chance == otherEntry.chance;
            } else {
                return false;
            }
        }

        public ResultValues getResult() {
            return result;
        }

        public int getChance() {
            return chance;
        }

        public void setResult(ResultValues newResult) {
            assertMutable();
            Checks.notNull(newResult);
            this.result = newResult.copy(false);
        }

        public void setChance(int newChance) {
            assertMutable();
            this.chance = newChance;
        }

        public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
            if (result == null) throw new ProgrammingValidationException("No result");
            Validation.scope("Result", () -> result.validateComplete(itemSet));
            if (chance <= 0) throw new ValidationException("Chance must be positive");
            if (chance > 100) throw new ValidationException("Chance can be at most 100%");
        }
    }
}
