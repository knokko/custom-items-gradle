package nl.knokko.customitems.drops;

import nl.knokko.customitems.block.drop.RequiredItems;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class KciDrop extends ModelValues {

    public static KciDrop load1(BitInput input, ItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        KciDrop result = new KciDrop(mutable);
        result.load1(input, itemSet);
        return result;
    }

    public static KciDrop load2(BitInput input, ItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        KciDrop result = new KciDrop(mutable);
        result.load2(input, itemSet);
        return result;
    }

    public static KciDrop load(BitInput input, ItemSet itemSet, boolean mutable) throws UnknownEncodingException {
        KciDrop result = new KciDrop(mutable);
        result.load(input, itemSet);
        return result;
    }

    public static KciDrop createQuick(
            OutputTable outputTable, boolean cancelNormalDrops,
            RequiredItems requiredHeldItems, AllowedBiomes allowedBiomes
    ) {
        KciDrop result = new KciDrop(true);
        result.setOutputTable(outputTable);
        result.setCancelNormalDrops(cancelNormalDrops);
        result.setRequiredHeldItems(requiredHeldItems);
        result.setAllowedBiomes(allowedBiomes);
        return result;
    }

    private OutputTable outputTable;
    private boolean cancelNormalDrops;
    private RequiredItems requiredHeldItems;
    private AllowedBiomes allowedBiomes;

    public KciDrop(boolean mutable) {
        super(mutable);
        this.outputTable = new OutputTable(false);
        this.cancelNormalDrops = false;
        this.requiredHeldItems = new RequiredItems(false);
        this.allowedBiomes = new AllowedBiomes(false);
    }

    public KciDrop(KciDrop toCopy, boolean mutable) {
        super(mutable);
        this.outputTable = toCopy.getOutputTable();
        this.cancelNormalDrops = toCopy.shouldCancelNormalDrops();
        this.requiredHeldItems = toCopy.getRequiredHeldItems();
        this.allowedBiomes = toCopy.getAllowedBiomes();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        String itemName = input.readString();
        int minDropAmount = input.readInt();
        int maxDropAmount = input.readInt();
        int rawDropChance = input.readInt() * Chance.ONE_PERCENT;
        this.cancelNormalDrops = input.readBoolean();
        this.requiredHeldItems = new RequiredItems(false);
        this.allowedBiomes = new AllowedBiomes(false);

        int numAmounts = 1 + maxDropAmount - minDropAmount;
        int rawChancePerAmount = rawDropChance / numAmounts;

        // Resolve rounding errors as best as we can
        int rawLastChance = rawChancePerAmount + rawDropChance - numAmounts * rawChancePerAmount;

        OutputTable mutableOutputTable = new OutputTable(true);
        Collection<OutputTable.Entry> tableEntries = new ArrayList<>(numAmounts);
        for (byte amount = (byte) minDropAmount; amount < maxDropAmount; amount++) {
            CustomItemResult mutableResult = new CustomItemResult(true);
            mutableResult.setItem(itemSet.items.getReference(itemName));
            mutableResult.setAmount(amount);

            OutputTable.Entry mutableEntry = new OutputTable.Entry(true);
            mutableEntry.setChance(new Chance(rawChancePerAmount));
            mutableEntry.setResult(mutableResult);

            tableEntries.add(mutableEntry);
        }

        {
            CustomItemResult mutableResult = new CustomItemResult(true);
            mutableResult.setItem(itemSet.items.getReference(itemName));
            mutableResult.setAmount((byte) maxDropAmount);

            OutputTable.Entry mutableEntry = new OutputTable.Entry(true);
            mutableEntry.setChance(new Chance(rawLastChance));
            mutableEntry.setResult(mutableResult);

            tableEntries.add(mutableEntry);
        }

        mutableOutputTable.setEntries(tableEntries);
        this.outputTable = mutableOutputTable.copy(false);
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.outputTable = OutputTable.load1(input, itemSet);
        this.cancelNormalDrops = input.readBoolean();

        int numRequiredItems = input.readInt();
        this.requiredHeldItems = new RequiredItems(true);
        Collection<ItemReference> customItems = new ArrayList<>(numRequiredItems);
        for (int counter = 0; counter < numRequiredItems; counter++) {
            customItems.add(itemSet.items.getReference(input.readString()));
        }
        this.requiredHeldItems.setCustomItems(customItems);
        this.requiredHeldItems = this.requiredHeldItems.copy(false);
        this.allowedBiomes = new AllowedBiomes(false);
    }

    private void load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 3) throw new UnknownEncodingException("Drop", encoding);

        this.outputTable = OutputTable.load(input, itemSet);
        this.cancelNormalDrops = input.readBoolean();

        if (encoding < 3) {
            int numRequiredItems = input.readInt();
            Collection<ItemReference> customItems = new ArrayList<>(numRequiredItems);
            for (int counter = 0; counter < numRequiredItems; counter++) {
                customItems.add(itemSet.items.getReference(input.readString()));
            }
            this.requiredHeldItems = new RequiredItems(true);
            this.requiredHeldItems.setCustomItems(customItems);
            this.requiredHeldItems = requiredHeldItems.copy(false);
        } else this.requiredHeldItems = RequiredItems.load(input, itemSet, false);
        if (encoding >= 2) {
            this.allowedBiomes = AllowedBiomes.load(input);
        } else {
            this.allowedBiomes = new AllowedBiomes(false);
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 3);

        outputTable.save(output);
        output.addBoolean(cancelNormalDrops);
        requiredHeldItems.save(output);
        allowedBiomes.save(output);
    }

    @Override
    public KciDrop copy(boolean mutable) {
        return new KciDrop(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == KciDrop.class) {
            KciDrop otherDrop = (KciDrop) other;
            return this.outputTable.equals(otherDrop.outputTable) && this.cancelNormalDrops == otherDrop.cancelNormalDrops
                    && this.requiredHeldItems.equals(otherDrop.requiredHeldItems) && this.allowedBiomes.equals(otherDrop.allowedBiomes);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Drop(" + cancelNormalDrops + ", " + requiredHeldItems + ", " + outputTable + ")";
    }

    public OutputTable getOutputTable() {
        return outputTable;
    }

    public boolean shouldCancelNormalDrops() {
        return cancelNormalDrops;
    }

    public RequiredItems getRequiredHeldItems() {
        return requiredHeldItems;
    }

    public AllowedBiomes getAllowedBiomes() {
        return allowedBiomes;
    }

    public void setOutputTable(OutputTable newOutputTable) {
        assertMutable();
        Checks.notNull(newOutputTable);
        this.outputTable = newOutputTable.copy(false);
    }

    public void setCancelNormalDrops(boolean cancelNormalDrops) {
        assertMutable();
        this.cancelNormalDrops = cancelNormalDrops;
    }

    public void setRequiredHeldItems(RequiredItems newRequiredHeldItems) {
        assertMutable();
        this.requiredHeldItems = newRequiredHeldItems.copy(false);
    }

    public void setAllowedBiomes(AllowedBiomes allowedBiomes) {
        assertMutable();
        Checks.notNull(allowedBiomes);
        this.allowedBiomes = allowedBiomes.copy(false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (outputTable == null) throw new ProgrammingValidationException("No output table");
        Validation.scope("Output table", () -> outputTable.validate(itemSet));
        if (requiredHeldItems == null) throw new ProgrammingValidationException("No required held items");
        Validation.scope("Required held items", requiredHeldItems::validateComplete, itemSet);
        if (allowedBiomes == null) throw new ProgrammingValidationException("No allowed biomes");
        Validation.scope("Allowed biomes", allowedBiomes::validate);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        outputTable.validateExportVersion(version);
        allowedBiomes.validateExportVersion(version);
        requiredHeldItems.validateExportVersion(version);
    }
}
