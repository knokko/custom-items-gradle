package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class CustomBlockDrop {

    private RequiredItems requiredItems;
    private SilkTouchRequirement silkTouch;
    private OutputTable itemsToDrop;

    private final boolean mutable;

    public CustomBlockDrop(boolean mutable) {
        this.mutable = mutable;

        this.requiredItems = new RequiredItems(false);
        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.itemsToDrop = new OutputTable();
    }

    public CustomBlockDrop(CustomBlockDrop toCopy, boolean mutable) {
        this.mutable = mutable;

        this.requiredItems = toCopy.getRequiredItems();
        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.itemsToDrop = toCopy.getItemsToDrop();
    }

    public RequiredItems getRequiredItems() {
        return requiredItems;
    }

    public SilkTouchRequirement getSilkTouchRequirement() {
        return silkTouch;
    }

    public OutputTable getItemsToDrop() {
        return itemsToDrop.copy();
    }

    private void assertMutable() {
        if (!mutable) throw new UnsupportedOperationException("This CustomBlockDrop is immutable");
    }

    public void setRequiredItems(RequiredItems newRequiredItems) {
        assertMutable();
        this.requiredItems = new RequiredItems(newRequiredItems, false);
    }

    public void setSilkTouchRequirement(SilkTouchRequirement newRequirement) {
        assertMutable();
        this.silkTouch = newRequirement;
    }

    public void setItemsToDrop(OutputTable newItemsToDrop) {
        this.itemsToDrop = newItemsToDrop.copy();
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (requiredItems == null)
            throw new ProgrammingValidationException("requiredItems is null");
        requiredItems.validateIndependent();

        if (silkTouch == null)
            throw new ProgrammingValidationException("silkTouch is null");

        if (itemsToDrop == null)
            throw new ProgrammingValidationException("itemsToDrop is null");
        String itemsToDropError = itemsToDrop.validate();
        if (itemsToDropError != null)
            throw new ValidationException(itemsToDropError);
    }

    public void validateComplete(
            Iterable<? extends CustomItem> customItems
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        requiredItems.validateComplete(customItems);
    }
}
