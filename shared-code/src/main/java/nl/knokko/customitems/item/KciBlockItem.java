package nl.knokko.customitems.item;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class KciBlockItem extends KciItem {

    public static KciBlockItem load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        KciBlockItem result = new KciBlockItem(false);

        if (encoding == ItemEncoding.ENCODING_BLOCK_ITEM_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_BLOCK_ITEM_12) {
            result.loadBlockItemPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomBlockItem", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private BlockReference block;
    private byte maxStacksize;

    public KciBlockItem(boolean mutable) {
        super(mutable, KciItemType.OTHER);

        this.otherMaterial = VMaterial.STONE;
        this.maxStacksize = 64;
        this.block = null;
    }

    public KciBlockItem(KciBlockItem source, boolean mutable) {
        super(source, mutable);

        this.maxStacksize = source.getMaxStacksize();
        this.block = source.getBlockReference();
    }

    @Override
    public DefaultModelType getDefaultModelType() {
        return null;
    }

    protected void loadBlockItemPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadSharedPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("BlockItemNew", encoding);

        this.maxStacksize = input.readByte();
        this.block = itemSet.blocks.getReference(input.readInt());
    }

    protected void saveBlockItemPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveSharedPropertiesNew(output, targetSide);

        output.addByte((byte) 1);

        output.addByte(this.maxStacksize);
        output.addInt(this.block.get().getInternalID());
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBase10(input, itemSet);
        loadBlockOnlyProperties10(input, itemSet);
    }

    private void loadBlockOnlyProperties10(BitInput input, ItemSet itemSet) {
        this.block = itemSet.blocks.getReference(input.readInt());
        this.maxStacksize = (byte) input.readInt();
    }

    private void initDefaults10() {
        initBaseDefaults10();
        initBlockItemOnlyDefaults10();
    }

    private void initBlockItemOnlyDefaults10() {
        // Nothing to be done until the next encoding is out
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_BLOCK_ITEM_12);
        saveBlockItemPropertiesNew(output, side);
    }

    @Override
    public byte getMaxStacksize() {
        return maxStacksize;
    }

    protected boolean areBlockItemPropertiesEqual(KciBlockItem other) {
        return areBaseItemPropertiesEqual(other) && this.block.equals(other.block) && this.maxStacksize == other.maxStacksize;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == KciBlockItem.class && areBlockItemPropertiesEqual((KciBlockItem) other);
    }

    @Override
    public KciBlockItem copy(boolean mutable) {
        return new KciBlockItem(this, mutable);
    }

    public BlockReference getBlockReference() {
        return block;
    }

    public KciBlock getBlock() {
        return block.get();
    }

    public void setBlock(BlockReference newBlock) {
        assertMutable();
        Checks.notNull(newBlock);
        this.block = newBlock;
        this.texture = newBlock.get().getModel().getPrimaryTexture();
    }

    public void setMaxStacksize(byte newStacksize) {
        assertMutable();
        this.maxStacksize = newStacksize;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (block == null) throw new ValidationException("You need to choose a block");
        if (maxStacksize < 1) throw new ValidationException("The maximum stacksize must be positive");
        if (maxStacksize > 64) throw new ValidationException("The maximum stacksize can be at most 64");
        if (model != null) throw new ValidationException("Custom block items can't have custom models");
        super.validateIndependent();
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!itemSet.blocks.isValid(block)) throw new ProgrammingValidationException("Block is no longer valid");
    }
}
