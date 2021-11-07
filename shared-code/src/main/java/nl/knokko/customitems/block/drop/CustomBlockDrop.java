package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomBlockDrop extends ModelValues {

    private static final byte ENCODING_1 = 1;

    public static CustomBlockDrop load(
            BitInput input, SItemSet itemSet, boolean mutable
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        CustomBlockDrop result = new CustomBlockDrop(mutable);
        if (encoding == ENCODING_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomBlockDrop", encoding);
        }

        return result;
    }

    private RequiredItems requiredItems;
    private SilkTouchRequirement silkTouch;
    private OutputTableValues itemsToDrop;

    public CustomBlockDrop(boolean mutable) {
        super(mutable);

        this.requiredItems = new RequiredItems(false);
        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.itemsToDrop = new OutputTableValues(false);
    }

    public CustomBlockDrop(CustomBlockDrop toCopy, boolean mutable) {
        super(mutable);

        this.requiredItems = toCopy.getRequiredItems();
        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.itemsToDrop = toCopy.getItemsToDrop();
    }
    
    public CustomBlockDrop copy(boolean mutable) {
        return new CustomBlockDrop(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomBlockDrop) {

            CustomBlockDrop otherDrop = (CustomBlockDrop) other;
            return otherDrop.requiredItems.equals(this.requiredItems) &&
                    otherDrop.silkTouch == this.silkTouch &&
                    otherDrop.itemsToDrop.equals(this.itemsToDrop);
        } else {
            return false;
        }
    }

    private void load1(
            BitInput input, SItemSet itemSet
    ) throws UnknownEncodingException {
        this.requiredItems = RequiredItems.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.itemsToDrop = OutputTableValues.load1(input, itemSet);
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_1);
        save1(output);
    }

    private void save1(BitOutput output) {
        requiredItems.save(output);
        output.addString(silkTouch.name());
        itemsToDrop.save1(output);
    }

    public RequiredItems getRequiredItems() {
        return requiredItems;
    }

    public SilkTouchRequirement getSilkTouchRequirement() {
        return silkTouch;
    }

    public OutputTableValues getItemsToDrop() {
        return itemsToDrop.copy(false);
    }

    public void setRequiredItems(RequiredItems newRequiredItems) {
        assertMutable();
        this.requiredItems = new RequiredItems(newRequiredItems, false);
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
            SItemSet itemSet
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        requiredItems.validateComplete(itemSet);
        itemsToDrop.validate(itemSet);
    }
}
