package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.block.BlockConstants;
import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class BlockManager extends ModelManager<KciBlock, BlockReference> {

    BlockManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciBlock block, BitOutput output, ItemSet.Side targetSide) {
        output.addInt(block.getInternalID());
        block.save(output, targetSide);
    }

    @Override
    BlockReference createReference(Model<KciBlock> block) {
        return new BlockReference(block);
    }

    @Override
    protected KciBlock loadElement(BitInput input) throws UnknownEncodingException {
        int id = input.readInt();
        return KciBlock.load(input, itemSet, id);
    }

    @Override
    protected void validateExportVersion(KciBlock block, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Block " + block.getName(),
                () -> block.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        super.validate();
        validateUniqueIDs("block id", elements, block -> block.getValues().getInternalID());
        validateUniqueIDs("block name", elements, block -> block.getValues().getName());
    }

    @Override
    protected void validateCreation(KciBlock values) throws ValidationException, ProgrammingValidationException {
        values.setInternalId(this.findFreeBlockId());
        values.validateComplete(itemSet, null);
    }

    @Override
    protected void validate(KciBlock block) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Block " + block.getName(),
                () -> block.validateComplete(itemSet, block.getInternalID())
        );
    }

    @Override
    protected void validateChange(
            BlockReference reference, KciBlock newValues
    ) throws ValidationException, ProgrammingValidationException {
        newValues.validateComplete(itemSet, reference.getModel().getValues().getInternalID());
    }

    private int findFreeBlockId() throws ValidationException {
        for (int candidateId = BlockConstants.MIN_BLOCK_ID; candidateId <= BlockConstants.MAX_BLOCK_ID; candidateId++) {
            if (!this.get(candidateId).isPresent()) return candidateId;
        }
        throw new ValidationException("Maximum number of custom blocks has been reached");
    }

    public BlockReference getReference(int blockID) throws NoSuchElementException {
        if (itemSet.finishedLoading) {
            return new BlockReference(CollectionHelper.find(elements, block -> block.getValues().getInternalID(), blockID).get());
        } else {
            return new BlockReference(blockID, itemSet);
        }
    }

    public Optional<KciBlock> get(int internalId) {
        return CollectionHelper.find(elements, block -> block.getValues().getInternalID(), internalId).map(Model::getValues);
    }

    public Optional<KciBlock> get(String name) {
        return CollectionHelper.find(elements, block -> block.getValues().getName(), name).map(Model::getValues);
    }

    @Override
    public void combine(
            ModelManager<KciBlock, ?> primary,
            ModelManager<KciBlock, ?> secondary
    ) throws ValidationException {
        elements.addAll(primary.elements);
        if (!secondary.elements.isEmpty()) throw new ValidationException("The secondary item set can't have blocks");
    }
}
