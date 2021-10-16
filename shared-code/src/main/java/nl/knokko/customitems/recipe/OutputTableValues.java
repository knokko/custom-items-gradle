package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.result.SResult;
import nl.knokko.customitems.recipe.result.SSimpleVanillaResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class OutputTableValues extends ModelValues {

    public static OutputTableValues load1(BitInput input, SItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        OutputTableValues result = new OutputTableValues(mutable);
        result.load1(input, itemSet);
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

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        int numEntries = input.readByte();
        this.entries = new ArrayList<>(numEntries);

        for (int counter = 0; counter < numEntries; counter++) {
            Entry entry = new Entry(false);
            entry.load1(input, itemSet);
            this.entries.add(entry);
        }
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

        private static SResult createDefaultResult() {
            SSimpleVanillaResult mutableResult = new SSimpleVanillaResult(true);
            mutableResult.setMaterial(CIMaterial.GOLD_INGOT);
            return mutableResult.copy(false);
        }

        private SResult result;
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
            this.result = SResult.load(input, itemSet);
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

        public SResult getResult() {
            return result;
        }

        public int getChance() {
            return chance;
        }

        public void setResult(SResult newResult) {
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
