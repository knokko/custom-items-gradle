package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ReplaceBlocks extends ModelValues {

    public static ReplaceBlocks load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ReplaceBlocks", encoding);

        ReplaceBlocks result = new ReplaceBlocks(false);

        int numCustomBlocks = input.readInt();
        result.customBlocks = new HashSet<>(numCustomBlocks);
        for (int counter = 0; counter < numCustomBlocks; counter++) {
            result.customBlocks.add(itemSet.blocks.getReference(input.readInt()));
        }

        int numVanillaBlocks = input.readInt();
        result.vanillaBlocks = EnumSet.noneOf(VMaterial.class);
        for (int counter = 0; counter < numVanillaBlocks; counter++) {
            result.vanillaBlocks.add(VMaterial.valueOf(input.readString()));
        }

        return result;
    }

    private Set<BlockReference> customBlocks;
    private Set<VMaterial> vanillaBlocks;

    public ReplaceBlocks(boolean mutable) {
        super(mutable);
        this.customBlocks = new HashSet<>();
        this.vanillaBlocks = EnumSet.noneOf(VMaterial.class);
    }

    public ReplaceBlocks(ReplaceBlocks toCopy, boolean mutable) {
        super(mutable);
        this.customBlocks = toCopy.getCustomBlocks();
        this.vanillaBlocks = toCopy.getVanillaBlocks();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(customBlocks.size());
        for (BlockReference block : customBlocks) {
            output.addInt(block.get().getInternalID());
        }

        output.addInt(vanillaBlocks.size());
        for (VMaterial block : vanillaBlocks) {
            output.addString(block.name());
        }
    }

    @Override
    public ReplaceBlocks copy(boolean mutable) {
        return new ReplaceBlocks(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ReplaceBlocks) {
            ReplaceBlocks otherBlocks = (ReplaceBlocks) other;
            return this.customBlocks.equals(otherBlocks.customBlocks) && this.vanillaBlocks.equals(otherBlocks.vanillaBlocks);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ReplaceBlocks(" + customBlocks + "," + vanillaBlocks + ")";
    }

    public Set<BlockReference> getCustomBlocks() {
        return new HashSet<>(customBlocks);
    }

    public Set<VMaterial> getVanillaBlocks() {
        return EnumSet.copyOf(vanillaBlocks);
    }

    public boolean contains(BlockReference block) {
        return customBlocks.contains(block);
    }

    public boolean contains(KciBlock block, ItemSet itemSet) {
        return contains(itemSet.blocks.getReference(block.getInternalID()));
    }

    public boolean contains(VMaterial block) {
        return vanillaBlocks.contains(block);
    }

    public void setCustomBlocks(Set<BlockReference> customBlocks) {
        assertMutable();
        this.customBlocks = new HashSet<>(customBlocks);
    }

    public void setVanillaBlocks(Set<VMaterial> vanillaBlocks) {
        assertMutable();
        this.vanillaBlocks = EnumSet.copyOf(vanillaBlocks);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (customBlocks == null) throw new ProgrammingValidationException("No custom blocks");
        for (BlockReference block : customBlocks) {
            if (block == null) throw new ProgrammingValidationException("Missing a custom block");
            if (!itemSet.blocks.isValid(block)) throw new ProgrammingValidationException("Block is no longer valid");
        }

        if (vanillaBlocks == null) throw new ProgrammingValidationException("No vanilla blocks");
        for (VMaterial block : vanillaBlocks) {
            if (block == null) throw new ProgrammingValidationException("Missing a vanilla block");
        }

        if (customBlocks.isEmpty() && vanillaBlocks.isEmpty()) {
            throw new ValidationException("You must allow at least 1 block to be replaced");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        for (VMaterial block : vanillaBlocks) {
            if (version < block.firstVersion) {
                throw new ValidationException(block + " doesn't exist yet in MC " + MCVersions.createString(version));
            }
            if (version > block.lastVersion) {
                throw new ValidationException(block + " no longer exists in MC " + MCVersions.createString(version));
            }
        }
    }
}
