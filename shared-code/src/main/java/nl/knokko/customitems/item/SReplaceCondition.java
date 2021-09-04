package nl.knokko.customitems.item;

import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SReplaceCondition extends ModelValues {

    private final int MAX_DEFAULT_SPACE = 37 * 64; // 37 slots of 64 items at most

    public static SReplaceCondition load1(BitInput input, SItemSet itemSet, boolean mutable) {
        SReplaceCondition result = new SReplaceCondition(mutable);
        result.load1(input, itemSet);
        return result;
    }

    private ReplacementCondition condition;
    private ItemReference item;
    private ReplacementOperation operation;
    private int value;
    private ItemReference replaceItem;

    public SReplaceCondition(boolean mutable) {
        super(mutable);
    }

    public SReplaceCondition(SReplaceCondition toCopy, boolean mutable) {
        super(mutable);

        this.condition = toCopy.getCondition();
        this.item = toCopy.getItemReference();
        this.operation = toCopy.getOperation();
        this.value = toCopy.getValue();
        this.replaceItem = toCopy.getReplaceItemReference();
    }

    private void load1(BitInput input, SItemSet itemSet) {
        this.condition = ReplacementCondition.valueOf(input.readJavaString());
        this.item = itemSet.getItemReference(input.readJavaString());
        this.operation = ReplacementOperation.valueOf(input.readJavaString());
        this.value = input.readInt();
        this.replaceItem = itemSet.getItemReference(input.readJavaString());
    }

    @Override
    public ModelValues copy(boolean mutable) {
        return new SReplaceCondition(this, mutable);
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

    public CustomItemValues getItem() {
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

    public CustomItemValues getReplaceItem() {
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
        if (condition == ReplacementCondition.HASITEM) {
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
