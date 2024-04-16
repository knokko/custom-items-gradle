package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class BlockDropManager extends ModelManager<BlockDropValues, BlockDropReference> {

    protected BlockDropManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(BlockDropValues element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    BlockDropReference createReference(Model<BlockDropValues> element) {
        return new BlockDropReference(element);
    }

    @Override
    protected BlockDropValues loadElement(BitInput input) throws UnknownEncodingException {
        return BlockDropValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(BlockDropValues blockDrop, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Block drop for " + blockDrop.getBlockType(),
                () -> blockDrop.validateExportVersion(mcVersion)
        );
    }

    @Override
    protected void validate(BlockDropValues blockDrop) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Block drop for " + blockDrop.getBlockType(),
                () -> blockDrop.validate(itemSet)
        );
    }

    @Override
    protected void validateCreation(BlockDropValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(BlockDropReference reference, BlockDropValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
