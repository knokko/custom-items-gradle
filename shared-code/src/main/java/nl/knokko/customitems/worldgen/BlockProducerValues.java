package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;

public class BlockProducerValues extends ModelValues {

    public static BlockProducerValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("BlockProducer", encoding);

        BlockProducerValues result = new BlockProducerValues(false);

        int numEntries = input.readInt();
        result.entries = new ArrayList<>(numEntries);
        for (int counter = 0; counter < numEntries; counter++) {
            result.entries.add(Entry.load(input, itemSet));
        }

        return result;
    }

    public static BlockProducerValues createQuick(Entry... entries) {
        BlockProducerValues result = new BlockProducerValues(true);
        List<Entry> entryList = new ArrayList<>(entries.length);
        Collections.addAll(entryList, entries);
        result.setEntries(entryList);
        return result;
    }

    public static BlockProducerValues createQuick(List<Entry> entries) {
        BlockProducerValues result = new BlockProducerValues(true);
        result.setEntries(entries);
        return result;
    }

    private List<Entry> entries;

    public BlockProducerValues(boolean mutable) {
        super(mutable);
        this.entries = new ArrayList<>(1);
        this.entries.add(new Entry(false));
    }

    public BlockProducerValues(BlockProducerValues toCopy, boolean mutable) {
        super(mutable);
        this.entries = toCopy.getEntries();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(entries.size());
        for (Entry entry : entries) {
            entry.save(output);
        }
    }

    @Override
    public BlockProducerValues copy(boolean mutable) {
        return new BlockProducerValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BlockProducerValues && this.entries.equals(((BlockProducerValues) other).entries);
    }

    @Override
    public String toString() {
        return "BlockProducer( " + entries + ")";
    }

    public Chance getNothingChance() {
        Chance remainingChance = Chance.percentage(100);
        for (Entry entry : entries) {
            remainingChance = Chance.subtract(remainingChance, entry.getChance());
            if (remainingChance == null) return null;
        }
        return remainingChance;
    }

    private ProducedBlock produce(int rawRandomChance) {
        int rawRemaining = rawRandomChance;
        for (Entry entry : entries) {
            if (entry.getChance().getRawValue() > rawRemaining) {
                return entry.block;
            }
            rawRemaining -= entry.getChance().getRawValue();
        }

        return null;
    }

    public ProducedBlock produce(Chance chance) {
        return this.produce(chance.getRawValue());
    }

    public ProducedBlock produce(Random random) {
        return produce(random.nextInt(Chance.HUNDRED_PERCENT));
    }

    public List<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    public void setEntries(List<Entry> entries) {
        assertMutable();
        this.entries = Mutability.createDeepCopy(entries, false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (entries == null) throw new ProgrammingValidationException("No entries");
        for (Entry entry : entries) {
            if (entry == null) throw new ProgrammingValidationException("Missing an entry");
            entry.validate(itemSet);
        }
        if (getNothingChance() == null) {
            throw new ValidationException("Total chance can be at most 100%");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        for (Entry entry : entries) {
            entry.validateExportVersion(version);
        }
    }

    public static class Entry extends ModelValues {

        public static Entry load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
            byte encoding = input.readByte();
            if (encoding != 1) throw new UnknownEncodingException("BlockProducerEntry", encoding);

            Entry result = new Entry(false);
            result.block = ProducedBlock.load(input, itemSet);
            result.chance = Chance.load(input);
            return result;
        }

        public static Entry createQuick(ProducedBlock block, Chance chance) {
            Entry result = new Entry(true);
            result.setBlock(block);
            result.setChance(chance);
            return result;
        }

        private ProducedBlock block;
        private Chance chance;

        public Entry(boolean mutable) {
            super(mutable);
            this.block = new ProducedBlock(CIMaterial.COAL_ORE);
            this.chance = Chance.percentage(100);
        }

        public Entry(Entry toCopy, boolean mutable) {
            super(mutable);
            this.block = toCopy.getBlock();
            this.chance = toCopy.getChance();
        }

        public void save(BitOutput output) {
            output.addByte((byte) 1);

            block.save(output);
            chance.save(output);
        }

        @Override
        public Entry copy(boolean mutable) {
            return new Entry(this, mutable);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Entry) {
                Entry otherEntry = (Entry) other;
                return this.block.equals(otherEntry.block) && this.chance.equals(otherEntry.chance);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "(" + chance + " for " + block + ")";
        }

        public ProducedBlock getBlock() {
            return block;
        }

        public Chance getChance() {
            return chance;
        }

        public void setBlock(ProducedBlock block) {
            assertMutable();
            this.block = Objects.requireNonNull(block);
        }

        public void setChance(Chance chance) {
            assertMutable();
            this.chance = Objects.requireNonNull(chance);
        }

        public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
            if (block == null) throw new ProgrammingValidationException("No block");
            Validation.scope("Block", block::validate, itemSet);
            if (chance == null) throw new ProgrammingValidationException("No chance");
        }

        public void validateExportVersion(int version) throws ValidationException {
            block.validateExportVersion(version);
        }
    }
}
