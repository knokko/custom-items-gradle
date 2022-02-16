package nl.knokko.customitems.drops;

import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class DropValues extends ModelValues {

    public static DropValues load1(BitInput input, ItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        DropValues result = new DropValues(mutable);
        result.load1(input, itemSet);
        return result;
    }

    public static DropValues load2(BitInput input, ItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        DropValues result = new DropValues(mutable);
        result.load2(input, itemSet);
        return result;
    }

    public static DropValues load(BitInput input, ItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        DropValues result = new DropValues(mutable);
        result.load(input, itemSet);
        return result;
    }

    public static DropValues createQuick(
            OutputTableValues outputTable, boolean cancelNormalDrops, Collection<ItemReference> requiredHeldItems
    ) {
        DropValues result = new DropValues(true);
        result.setOutputTable(outputTable);
        result.setCancelNormalDrops(cancelNormalDrops);
        result.setRequiredHeldItems(requiredHeldItems);
        return result;
    }

    private OutputTableValues outputTable;
    private boolean cancelNormalDrops;
    private Collection<ItemReference> requiredHeldItems;

    public DropValues(boolean mutable) {
        super(mutable);
        this.outputTable = new OutputTableValues(false);
        this.cancelNormalDrops = false;
        this.requiredHeldItems = new ArrayList<>();
    }

    public DropValues(DropValues toCopy, boolean mutable) {
        super(mutable);
        this.outputTable = toCopy.getOutputTable();
        this.cancelNormalDrops = toCopy.shouldCancelNormalDrops();
        this.requiredHeldItems = toCopy.getRequiredHeldItems();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        String itemName = input.readString();
        int minDropAmount = input.readInt();
        int maxDropAmount = input.readInt();
        int rawDropChance = input.readInt() * Chance.ONE_PERCENT;
        this.cancelNormalDrops = input.readBoolean();
        this.requiredHeldItems = new ArrayList<>(0);

        int numAmounts = 1 + maxDropAmount - minDropAmount;
        int rawChancePerAmount = rawDropChance / numAmounts;

        // Resolve rounding errors as best as we can
        int rawLastChance = rawChancePerAmount + rawDropChance - numAmounts * rawChancePerAmount;

        OutputTableValues mutableOutputTable = new OutputTableValues(true);
        Collection<OutputTableValues.Entry> tableEntries = new ArrayList<>(numAmounts);
        for (byte amount = (byte) minDropAmount; amount < maxDropAmount; amount++) {
            CustomItemResultValues mutableResult = new CustomItemResultValues(true);
            mutableResult.setItem(itemSet.getItemReference(itemName));
            mutableResult.setAmount(amount);

            OutputTableValues.Entry mutableEntry = new OutputTableValues.Entry(true);
            mutableEntry.setChance(new Chance(rawChancePerAmount));
            mutableEntry.setResult(mutableResult);

            tableEntries.add(mutableEntry);
        }

        {
            CustomItemResultValues mutableResult = new CustomItemResultValues(true);
            mutableResult.setItem(itemSet.getItemReference(itemName));
            mutableResult.setAmount((byte) maxDropAmount);

            OutputTableValues.Entry mutableEntry = new OutputTableValues.Entry(true);
            mutableEntry.setChance(new Chance(rawLastChance));
            mutableEntry.setResult(mutableResult);

            tableEntries.add(mutableEntry);
        }

        mutableOutputTable.setEntries(tableEntries);
        this.outputTable = mutableOutputTable.copy(false);
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.outputTable = OutputTableValues.load1(input, itemSet);
        this.cancelNormalDrops = input.readBoolean();

        int numRequiredItems = input.readInt();
        this.requiredHeldItems = new ArrayList<>(numRequiredItems);
        for (int counter = 0; counter < numRequiredItems; counter++) {
            this.requiredHeldItems.add(itemSet.getItemReference(input.readString()));
        }
    }

    private void load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("Drop", encoding);

        this.outputTable = OutputTableValues.load(input, itemSet);
        this.cancelNormalDrops = input.readBoolean();

        int numRequiredItems = input.readInt();
        this.requiredHeldItems = new ArrayList<>(numRequiredItems);
        for (int counter = 0; counter < numRequiredItems; counter++) {
            this.requiredHeldItems.add(itemSet.getItemReference(input.readString()));
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        outputTable.save(output);
        output.addBoolean(cancelNormalDrops);
        output.addInt(requiredHeldItems.size());
        for (ItemReference reference : requiredHeldItems) {
            output.addString(reference.get().getName());
        }
    }

    @Override
    public DropValues copy(boolean mutable) {
        return new DropValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == DropValues.class) {
            DropValues otherDrop = (DropValues) other;
            return this.outputTable.equals(otherDrop.outputTable) && this.cancelNormalDrops == otherDrop.cancelNormalDrops
                    && this.requiredHeldItems.equals(otherDrop.requiredHeldItems);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Drop(" + cancelNormalDrops + ", " + requiredHeldItems + ", " + outputTable + ")";
    }

    public OutputTableValues getOutputTable() {
        return outputTable;
    }

    public boolean shouldCancelNormalDrops() {
        return cancelNormalDrops;
    }

    public Collection<ItemReference> getRequiredHeldItems() {
        return new ArrayList<>(requiredHeldItems);
    }

    public void setOutputTable(OutputTableValues newOutputTable) {
        assertMutable();
        Checks.notNull(newOutputTable);
        this.outputTable = newOutputTable.copy(false);
    }

    public void setCancelNormalDrops(boolean cancelNormalDrops) {
        assertMutable();
        this.cancelNormalDrops = cancelNormalDrops;
    }

    public void setRequiredHeldItems(Collection<ItemReference> newRequiredHeldItems) {
        assertMutable();
        Checks.notNull(newRequiredHeldItems);
        this.requiredHeldItems = new ArrayList<>(newRequiredHeldItems);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (outputTable == null) throw new ProgrammingValidationException("No output table");
        Validation.scope("Output table", () -> outputTable.validate(itemSet));
        if (requiredHeldItems == null) throw new ProgrammingValidationException("No required held items");
        for (ItemReference requiredItem : requiredHeldItems) {
            if (requiredItem == null) throw new ProgrammingValidationException("Missing a required held item");
            if (!itemSet.isReferenceValid(requiredItem)) {
                throw new ProgrammingValidationException("Required item " + requiredItem.get().getName() + " is no longer valid");
            }
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        outputTable.validateExportVersion(version);
    }
}
