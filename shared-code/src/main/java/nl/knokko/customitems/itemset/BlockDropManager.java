package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class BlockDropManager extends ModelManager<BlockDrop, BlockDropReference> {

    protected BlockDropManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(BlockDrop element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    BlockDropReference createReference(Model<BlockDrop> element) {
        return new BlockDropReference(element);
    }

    @Override
    protected BlockDrop loadElement(BitInput input) throws UnknownEncodingException {
        return BlockDrop.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(BlockDrop blockDrop, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Block drop for " + blockDrop.getBlockType(),
                () -> blockDrop.validateExportVersion(mcVersion)
        );
    }

    @Override
    protected void validate(BlockDrop blockDrop) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Block drop for " + blockDrop.getBlockType(),
                () -> blockDrop.validate(itemSet)
        );
    }

    @Override
    protected void validateCreation(BlockDrop values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(BlockDropReference reference, BlockDrop newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
