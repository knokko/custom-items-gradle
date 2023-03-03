package nl.knokko.customitems.drops;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.block.drop.SilkTouchRequirement;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

/**
 * Represents a (potential) custom drop of a vanilla block.
 */
public class BlockDropValues extends ModelValues {

    public static BlockDropValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        BlockDropValues result = new BlockDropValues(false);

        if (encoding == 0) {
            result.load1(input, itemSet);
        } else if (encoding == 1) {
            result.load2(input, itemSet);
        } else if (encoding == 2) {
            result.load3(input, itemSet);
        } else if (encoding == 3) {
            result.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("BlockDrop", encoding);
        }

        return result;
    }

    public static BlockDropValues createQuick(
            BlockType blockType, SilkTouchRequirement silkTouch, DropValues drop,
            int minFortuneLevel, Integer maxFortuneLevel
    ) {
        BlockDropValues result = new BlockDropValues(true);
        result.setBlockType(blockType);
        result.setSilkTouchRequirement(silkTouch);
        result.setMinFortuneLevel(minFortuneLevel);
        result.setMaxFortuneLevel(maxFortuneLevel);
        result.setDrop(drop);
        return result;
    }

    private BlockType blockType;
    private SilkTouchRequirement silkTouch;
    private int minFortuneLevel;
    private Integer maxFortuneLevel;
    private DropValues drop;

    public BlockDropValues(boolean mutable) {
        super(mutable);
        this.blockType = BlockType.STONE;
        this.silkTouch = SilkTouchRequirement.FORBIDDEN;
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.drop = new DropValues(false);
    }

    public BlockDropValues(BlockDropValues toCopy, boolean mutable) {
        super(mutable);

        this.blockType = toCopy.getBlockType();
        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.minFortuneLevel = toCopy.getMinFortuneLevel();
        this.maxFortuneLevel = toCopy.getMaxFortuneLevel();
        this.drop = toCopy.getDrop();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.blockType = BlockType.getByOrdinal(input.readInt());
        this.silkTouch = SilkTouchRequirement.FORBIDDEN;
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.drop = DropValues.load1(input, itemSet, false);
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.blockType = BlockType.getByOrdinal(input.readInt());
        this.silkTouch = input.readBoolean() ? SilkTouchRequirement.OPTIONAL : SilkTouchRequirement.FORBIDDEN;
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.drop = DropValues.load2(input, itemSet, false);
    }

    private void load3(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.blockType = BlockType.getByOrdinal(input.readInt());
        this.silkTouch = input.readBoolean() ? SilkTouchRequirement.OPTIONAL : SilkTouchRequirement.FORBIDDEN;
        this.minFortuneLevel = 0;
        this.maxFortuneLevel = null;
        this.drop = DropValues.load(input, itemSet, false);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 3);
        output.addByte((byte) 1);

        output.addString(blockType.name());
        output.addString(silkTouch.name());
        output.addInt(minFortuneLevel);
        output.addBoolean(maxFortuneLevel != null);
        if (maxFortuneLevel != null) output.addInt(maxFortuneLevel);
        drop.save(output);
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("BlockDrop", encoding);

        this.blockType = BlockType.valueOf(input.readString());
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.minFortuneLevel = input.readInt();
        if (input.readBoolean()) this.maxFortuneLevel = input.readInt();
        else this.maxFortuneLevel = null;
        this.drop = DropValues.load(input, itemSet, false);
    }

    @Override
    public BlockDropValues copy(boolean mutable) {
        return new BlockDropValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == BlockDropValues.class) {
            BlockDropValues otherDrop = (BlockDropValues) other;
            return this.blockType == otherDrop.blockType && this.silkTouch == otherDrop.silkTouch
                    && this.minFortuneLevel == otherDrop.minFortuneLevel
                    && Objects.equals(this.maxFortuneLevel, otherDrop.maxFortuneLevel)
                    && this.drop.equals(otherDrop.drop);
        } else {
            return false;
        }
    }

    public BlockType getBlockType() {
        return blockType;
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

    public void setBlockType(BlockType newBlockType) {
        assertMutable();
        Checks.notNull(newBlockType);
        this.blockType = newBlockType;
    }

    public void setSilkTouchRequirement(SilkTouchRequirement silkTouch) {
        assertMutable();
        this.silkTouch = Objects.requireNonNull(silkTouch);
    }

    public void setMinFortuneLevel(int minFortuneLevel) {
        assertMutable();
        this.minFortuneLevel = minFortuneLevel;
    }

    public void setMaxFortuneLevel(Integer maxFortuneLevel) {
        assertMutable();
        this.maxFortuneLevel = maxFortuneLevel;
    }

    public void setDrop(DropValues newDrop) {
        assertMutable();
        Checks.notNull(newDrop);
        this.drop = newDrop.copy(false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (blockType == null) throw new ProgrammingValidationException("No block type");
        if (silkTouch == null) throw new ProgrammingValidationException("No silk touch requirement");
        if (minFortuneLevel < 0) throw new ValidationException("Minimum fortune level can't be negative");
        if (maxFortuneLevel != null && maxFortuneLevel < minFortuneLevel) {
            throw new ValidationException("Maximum fortune level can't be smaller than minimum fortune level");
        }
        if (drop == null) throw new ProgrammingValidationException("No drop");
        Validation.scope("Drop", () -> drop.validate(itemSet));
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < blockType.firstVersion) {
            throw new ValidationException(blockType + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > blockType.lastVersion) {
            throw new ValidationException(blockType + " was renamed after mc " + MCVersions.createString(version));
        }
        drop.validateExportVersion(version);
    }
}
