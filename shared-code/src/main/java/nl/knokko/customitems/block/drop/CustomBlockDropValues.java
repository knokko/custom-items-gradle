package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

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
        } else if (encoding == 3) {
            result.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomBlockDrop", encoding);
        }

        return result;
    }

    private RequiredItemValues requiredItems;
    private SilkTouchRequirement silkTouch;
    private int minFortuneLevel;
    private Integer maxFortuneLevel;
    private OutputTableValues itemsToDrop;

    public CustomBlockDropValues(boolean mutable) {
        super(mutable);

        this.requiredItems = new RequiredItemValues(false);
        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.itemsToDrop = new OutputTableValues(false);
    }

    public CustomBlockDropValues(CustomBlockDropValues toCopy, boolean mutable) {
        super(mutable);

        this.requiredItems = toCopy.getRequiredItems();
        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.minFortuneLevel = toCopy.getMinFortuneLevel();
        this.maxFortuneLevel = toCopy.getMaxFortuneLevel();
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
                    otherDrop.minFortuneLevel == this.minFortuneLevel &&
                    Objects.equals(otherDrop.maxFortuneLevel, this.maxFortuneLevel) &&
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
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.itemsToDrop = OutputTableValues.load1(input, itemSet);
    }

    private void load2(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        this.requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.itemsToDrop = OutputTableValues.load(input, itemSet);
    }

    private void loadNew(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomBlockDrop", encoding);

        this.requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.minFortuneLevel = input.readInt();
        if (input.readBoolean()) this.maxFortuneLevel = input.readInt();
        else this.maxFortuneLevel = null;
        this.itemsToDrop = OutputTableValues.load(input, itemSet);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 3);
        saveNew(output);
    }

    private void saveNew(BitOutput output) {
        output.addByte((byte) 1);

        requiredItems.save(output);
        output.addString(silkTouch.name());
        output.addInt(minFortuneLevel);
        output.addBoolean(maxFortuneLevel != null);
        if (maxFortuneLevel != null) output.addInt(maxFortuneLevel);
        itemsToDrop.save(output);
    }

    public RequiredItemValues getRequiredItems() {
        return requiredItems;
    }

    public SilkTouchRequirement getSilkTouchRequirement() {
        return silkTouch;
    }

    public int getMinFortuneLevel() {
        return minFortuneLevel;
    }

    public Integer getMaxFortuneLevel() {
        return maxFortuneLevel;
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

    public void setMinFortuneLevel(int minFortuneLevel) {
        assertMutable();
        this.minFortuneLevel = minFortuneLevel;
    }

    public void setMaxFortuneLevel(Integer maxFortuneLevel) {
        assertMutable();
        this.maxFortuneLevel = maxFortuneLevel;
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

        if (minFortuneLevel < 0) throw new ValidationException("Minimum fortune level can't be negative");
        if (maxFortuneLevel != null && maxFortuneLevel < minFortuneLevel) {
            throw new ValidationException("Maximum fortune level can't be smaller than minimum fortune level");
        }

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
