package nl.knokko.customitems.item;

import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class ReplacementConditionEntry extends ModelValues {

    private final int MAX_DEFAULT_SPACE = 37 * 64; // 37 slots of 64 items at most

    public static ReplacementConditionEntry load1(BitInput input, ItemSet itemSet, boolean mutable) {
        ReplacementConditionEntry result = new ReplacementConditionEntry(mutable);
        result.load1(input, itemSet);
        return result;
    }

    public static ReplacementConditionEntry createQuick(
            ReplacementCondition condition, ItemReference item, ReplacementOperation operation, int value, ItemReference replaceItem
    ) {
        ReplacementConditionEntry result = new ReplacementConditionEntry(true);
        result.setCondition(condition);
        if (item != null) result.setItem(item);
        result.setOperation(operation);
        result.setValue(value);
        if (replaceItem != null) result.setReplaceItem(replaceItem);
        return result;
    }

    private ReplacementCondition condition;
    private ItemReference item;
    private ReplacementOperation operation;
    private int value;
    private ItemReference replaceItem;

    public ReplacementConditionEntry(boolean mutable) {
        super(mutable);
    }

    public ReplacementConditionEntry(ReplacementConditionEntry toCopy, boolean mutable) {
        super(mutable);

        this.condition = toCopy.getCondition();
        this.item = toCopy.getItemReference();
        this.operation = toCopy.getOperation();
        this.value = toCopy.getValue();
        this.replaceItem = toCopy.getReplaceItemReference();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        this.condition = ReplacementConditionEntry.ReplacementCondition.valueOf(input.readJavaString());
        this.item = itemSet.items.getReference(input.readJavaString());
        this.operation = ReplacementOperation.valueOf(input.readJavaString());
        this.value = input.readInt();
        this.replaceItem = itemSet.items.getReference(input.readJavaString());
    }

    @Override
    public String toString() {
        return "ReplaceCondition(" + condition + ", " + item.get().getName() + ", " + operation + ", " + value + ", "
                + replaceItem.get().getName() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ReplacementConditionEntry) {
            ReplacementConditionEntry rc = (ReplacementConditionEntry) other;
            return this.condition == rc.condition && this.item.equals(rc.item) && this.operation == rc.operation
                    && this.value == rc.value && this.replaceItem.equals(rc.replaceItem);
        } else {
            return false;
        }
    }

    @Override
    public ModelValues copy(boolean mutable) {
        return new ReplacementConditionEntry(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addJavaString(condition.name());
        output.addJavaString(item.get().getName());
        output.addJavaString(operation.name());
        output.addInt(value);
        output.addJavaString(replaceItem.get().getName());
    }

    public ReplacementCondition getCondition() {
        return condition;
    }

    public KciItem getItem() {
        return item.get();
    }

    public ItemReference getItemReference() {
        return item;
    }

    public ReplacementOperation getOperation() {
        return operation;
    }

    public int getValue() {
        return value;
    }

    public KciItem getReplaceItem() {
        return replaceItem.get();
    }

    public ItemReference getReplaceItemReference() {
        return replaceItem;
    }

    public void setCondition(ReplacementCondition newCondition) {
        assertMutable();
        Checks.notNull(newCondition);
        this.condition = newCondition;
    }

    public void setItem(ItemReference newItem) {
        assertMutable();
        Checks.notNull(newItem);
        this.item = newItem;
    }

    public void setOperation(ReplacementOperation newOperation) {
        assertMutable();
        Checks.notNull(newOperation);
        this.operation = newOperation;
    }

    public void setValue(int newValue) {
        assertMutable();
        this.value = newValue;
    }

    public void setReplaceItem(ItemReference newReplaceItem) {
        assertMutable();
        Checks.notNull(newReplaceItem);
        this.replaceItem = newReplaceItem;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (condition == null) throw new ProgrammingValidationException("No condition");
        if (item == null) throw new ProgrammingValidationException("No item");
        if (operation == null) throw new ProgrammingValidationException("No operation");
        if (replaceItem == null) throw new ProgrammingValidationException("No replace item");
        if (condition == ReplacementConditionEntry.ReplacementCondition.HASITEM) {
            if (operation == ReplacementOperation.ATMOST && value >= MAX_DEFAULT_SPACE) {
                throw new ValidationException("ATMOST " + value + " is always true");
            }
            if (operation == ReplacementOperation.ATLEAST && value > MAX_DEFAULT_SPACE) {
                throw new ValidationException("ATLEAST " + value + " is always false");
            }
            if (operation == ReplacementOperation.EXACTLY && (value < 0 || value > MAX_DEFAULT_SPACE)) {
                throw new ValidationException("EXACTLY " + value + " is always false");
            }
        }
    }

    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
        if (!itemSet.items.isValid(item)) {
            throw new ValidationException("The item is not/no longer valid");
        }
        if (!itemSet.items.isValid(replaceItem)) {
            throw new ValidationException("The replace item is not/no longer valid");
        }
    }

    public enum ReplacementCondition {
        HASITEM,
        MISSINGITEM,
        ISBROKEN
    }

    public enum ReplacementOperation {
        ATMOST,
        ATLEAST,
        EXACTLY,
        NONE
    }

    public enum ConditionOperation {
        AND,
        OR,
        NONE
    }
}
