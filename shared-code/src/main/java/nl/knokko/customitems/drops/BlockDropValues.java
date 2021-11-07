package nl.knokko.customitems.drops;

import nl.knokko.customitems.encoding.DropEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class BlockDropValues extends ModelValues {

    public static BlockDropValues load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        BlockDropValues result = new BlockDropValues(false);

        if (encoding == DropEncoding.Block.ENCODING1) {
            result.load1(input, itemSet);
        } else if (encoding == DropEncoding.Block.ENCODING2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("BlockDrop", encoding);
        }

        return result;
    }

    private BlockType blockType;
    private boolean allowSilkTouch;
    private DropValues drop;

    public BlockDropValues(boolean mutable) {
        super(mutable);
        this.blockType = BlockType.STONE;
        this.allowSilkTouch = false;
        this.drop = new DropValues(false);
    }

    public BlockDropValues(BlockDropValues toCopy, boolean mutable) {
        super(mutable);

        this.blockType = toCopy.getBlockType();
        this.allowSilkTouch = toCopy.shouldAllowSilkTouch();
        this.drop = toCopy.getDrop();
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.blockType = BlockType.getByOrdinal(input.readInt());
        this.allowSilkTouch = false;
        this.drop = DropValues.load1(input, itemSet, false);
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.blockType = BlockType.getByOrdinal(input.readInt());
        this.allowSilkTouch = false;
        this.drop = DropValues.load2(input, itemSet, false);
    }

    public void save(BitOutput output) {
        output.addByte(DropEncoding.Block.ENCODING2);

        output.addInt(blockType.ordinal());
        output.addBoolean(allowSilkTouch);
        drop.save2(output);
    }

    @Override
    public BlockDropValues copy(boolean mutable) {
        return new BlockDropValues(this, mutable);
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public boolean shouldAllowSilkTouch() {
        return allowSilkTouch;
    }

    public DropValues getDrop() {
        return drop;
    }

    public void setBlockType(BlockType newBlockType) {
        assertMutable();
        Checks.notNull(newBlockType);
        this.blockType = newBlockType;
    }

    public void setAllowSilkTouch(boolean allowSilkTouch) {
        assertMutable();
        this.allowSilkTouch = allowSilkTouch;
    }

    public void setDrop(DropValues newDrop) {
        assertMutable();
        Checks.notNull(newDrop);
        this.drop = newDrop.copy(false);
    }

    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (blockType == null) throw new ProgrammingValidationException("No block type");
        if (drop == null) throw new ProgrammingValidationException("No drop");
        Validation.scope("Drop", () -> drop.validate(itemSet));
    }
}