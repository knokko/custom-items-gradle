package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public final class ProducedBlock {

    public static ProducedBlock load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ProducedBlock", encoding);

        if (input.readBoolean()) return new ProducedBlock(itemSet.blocks.getReference(input.readInt()));
        else return new ProducedBlock(CIMaterial.valueOf(input.readString()));
    }

    private final BlockReference customBlock;
    private final CIMaterial vanillaBlock;

    public ProducedBlock(BlockReference customBlock) {
        this.customBlock = Objects.requireNonNull(customBlock);
        this.vanillaBlock = null;
    }

    public ProducedBlock(CIMaterial vanillaBlock) {
        this.customBlock = null;
        this.vanillaBlock = Objects.requireNonNull(vanillaBlock);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addBoolean(isCustom());
        if (isCustom()) {
            output.addInt(customBlock.get().getInternalID());
        } else {
            output.addString(vanillaBlock.name());
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ProducedBlock) {
            ProducedBlock otherBlock = (ProducedBlock) other;
            return Objects.equals(this.customBlock, otherBlock.customBlock) && Objects.equals(this.vanillaBlock, otherBlock.vanillaBlock);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (isCustom()) {
            return customBlock.get().getName();
        } else {
            return vanillaBlock.toString();
        }
    }

    public boolean isCustom() {
        return customBlock != null;
    }

    public BlockReference getCustomBlock() {
        if (!isCustom()) throw new UnsupportedOperationException("This is a vanilla block");
        return customBlock;
    }

    public CIMaterial getVanillaBlock() {
        if (isCustom()) throw new UnsupportedOperationException("This is a custom block");
        return vanillaBlock;
    }

    public void validate(ItemSet itemSet) throws ProgrammingValidationException {
        if ((customBlock == null) == (vanillaBlock == null)) {
            throw new ProgrammingValidationException("Exactly 1 of customBlock and vanillaBlock must be null");
        }
        if (customBlock != null && !itemSet.blocks.isValid(customBlock)) {
            throw new ProgrammingValidationException("Block is no longer valid");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (vanillaBlock != null) {
            if (version < vanillaBlock.firstVersion) {
                throw new ValidationException(vanillaBlock + " doesn't exist yet in MC " + MCVersions.createString(version));
            } else if (version > vanillaBlock.lastVersion) {
                throw new ValidationException(vanillaBlock + " no longer exists in MC " + MCVersions.createString(version));
            }
        }
    }
}
