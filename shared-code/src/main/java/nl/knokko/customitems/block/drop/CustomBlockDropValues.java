package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

/**
 * Represents a (potential) drop of a custom block.
 */
public class CustomBlockDropValues extends ModelValues {

    public static CustomBlockDropValues load(
            BitInput input, ItemSet itemSet, boolean mutable
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        CustomBlockDropValues result = new CustomBlockDropValues(mutable);
        if (encoding == 1) {
            result.load1(input, itemSet);
        } else if (encoding == 2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomBlockDrop", encoding);
        }

        return result;
    }

    private RequiredItemValues requiredItems;
    private SilkTouchRequirement silkTouch;
    private OutputTableValues itemsToDrop;

    public CustomBlockDropValues(boolean mutable) {
        super(mutable);

        this.requiredItems = new RequiredItemValues(false);
        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.itemsToDrop = new OutputTableValues(false);
    }

    public CustomBlockDropValues(CustomBlockDropValues toCopy, boolean mutable) {
        super(mutable);

        this.requiredItems = toCopy.getRequiredItems();
        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.itemsToDrop = toCopy.getItemsToDrop();
    }
    
    public CustomBlockDropValues copy(boolean mutable) {
        return new CustomBlockDropValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomBlockDropValues) {

            CustomBlockDropValues otherDrop = (CustomBlockDropValues) other;
            return otherDrop.requiredItems.equals(this.requiredItems) &&
                    otherDrop.silkTouch == this.silkTouch &&
                    otherDrop.itemsToDrop.equals(this.itemsToDrop);
        } else {
            return false;
        }
    }

    private void load1(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        this.requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.itemsToDrop = OutputTableValues.load1(input, itemSet);
    }

    private void load2(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        this.requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.itemsToDrop = OutputTableValues.load(input, itemSet);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 2);
        save2(output);
    }

    private void save2(BitOutput output) {
        requiredItems.save(output);
        output.addString(silkTouch.name());
        itemsToDrop.save(output);
    }

    public RequiredItemValues getRequiredItems() {
        return requiredItems;
    }

    public SilkTouchRequirement getSilkTouchRequirement() {
        return silkTouch;
    }

    public OutputTableValues getItemsToDrop() {
        return itemsToDrop.copy(false);
    }

    public void setRequiredItems(RequiredItemValues newRequiredItems) {
        assertMutable();
        this.requiredItems = new RequiredItemValues(newRequiredItems, false);
    }

    public void setSilkTouchRequirement(SilkTouchRequirement newRequirement) {
        assertMutable();
        this.silkTouch = newRequirement;
    }

    public void setItemsToDrop(OutputTableValues newItemsToDrop) {
        assertMutable();
        this.itemsToDrop = newItemsToDrop.copy(false);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (requiredItems == null)
            throw new ProgrammingValidationException("requiredItems is null");
        requiredItems.validateIndependent();

        if (silkTouch == null)
            throw new ProgrammingValidationException("silkTouch is null");

        if (itemsToDrop == null)
            throw new ProgrammingValidationException("itemsToDrop is null");
    }

    public void validateComplete(
            ItemSet itemSet
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        requiredItems.validateComplete(itemSet);
        itemsToDrop.validate(itemSet);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        itemsToDrop.validateExportVersion(version);
    }
}
