package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.drops.AllowedBiomesValues;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
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

    private SilkTouchRequirement silkTouch;
    private int minFortuneLevel;
    private Integer maxFortuneLevel;
    private DropValues drop;

    public CustomBlockDropValues(boolean mutable) {
        super(mutable);

        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.drop = new DropValues(false);
    }

    public CustomBlockDropValues(CustomBlockDropValues toCopy, boolean mutable) {
        super(mutable);

        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.minFortuneLevel = toCopy.getMinFortuneLevel();
        this.maxFortuneLevel = toCopy.getMaxFortuneLevel();
        this.drop = toCopy.getDrop();
    }
    
    public CustomBlockDropValues copy(boolean mutable) {
        return new CustomBlockDropValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomBlockDropValues) {

            CustomBlockDropValues otherDrop = (CustomBlockDropValues) other;
            return otherDrop.silkTouch == this.silkTouch &&
                    otherDrop.minFortuneLevel == this.minFortuneLevel &&
                    Objects.equals(otherDrop.maxFortuneLevel, this.maxFortuneLevel) &&
                    otherDrop.drop.equals(this.drop);
        } else {
            return false;
        }
    }

    private void load1(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        RequiredItemValues requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        OutputTableValues itemsToDrop = OutputTableValues.load1(input, itemSet);
        this.drop = DropValues.createQuick(itemsToDrop, false, requiredItems, new AllowedBiomesValues(false));
    }

    private void load2(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        RequiredItemValues requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        OutputTableValues itemsToDrop = OutputTableValues.load(input, itemSet);
        this.drop = DropValues.createQuick(itemsToDrop, false, requiredItems, new AllowedBiomesValues(false));
    }

    private void loadNew(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomBlockDrop", encoding);

        RequiredItemValues requiredItems = null;
        if (encoding == 1) requiredItems = RequiredItemValues.load(input, itemSet, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.minFortuneLevel = input.readInt();
        if (input.readBoolean()) this.maxFortuneLevel = input.readInt();
        else this.maxFortuneLevel = null;

        if (encoding == 1) {
            OutputTableValues itemsToDrop = OutputTableValues.load(input, itemSet);
            this.drop = DropValues.createQuick(itemsToDrop, false, requiredItems, new AllowedBiomesValues(false));
        } else {
            this.drop = DropValues.load(input, itemSet, false);
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 3);
        saveNew(output);
    }

    private void saveNew(BitOutput output) {
        output.addByte((byte) 2);

        output.addString(silkTouch.name());
        output.addInt(minFortuneLevel);
        output.addBoolean(maxFortuneLevel != null);
        if (maxFortuneLevel != null) output.addInt(maxFortuneLevel);
        drop.save(output);
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

    public DropValues getDrop() {
        return drop;
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

    public void setDrop(DropValues drop) {
        assertMutable();
        this.drop = drop.copy(false);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (silkTouch == null)
            throw new ProgrammingValidationException("silkTouch is null");

        if (minFortuneLevel < 0) throw new ValidationException("Minimum fortune level can't be negative");
        if (maxFortuneLevel != null && maxFortuneLevel < minFortuneLevel) {
            throw new ValidationException("Maximum fortune level can't be smaller than minimum fortune level");
        }

        if (drop == null) throw new ProgrammingValidationException("itemsToDrop is null");
    }

    public void validateComplete(
            ItemSet itemSet
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        Validation.scope("Drop", drop::validate, itemSet);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        drop.validateExportVersion(version);
    }
}
