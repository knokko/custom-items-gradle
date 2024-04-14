package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class BlockDropManager extends ModelManager<BlockDrop, BlockDropValues, BlockDropReference> {

    protected BlockDropManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(BlockDrop element, BitOutput output, ItemSet.Side targetSide) {
        element.getValues().save(output);
    }

    @Override
    protected BlockDropReference createReference(BlockDrop element) {
        return new BlockDropReference(element);
    }

    @Override
    protected BlockDrop loadElement(BitInput input) throws UnknownEncodingException {
        return new BlockDrop(BlockDropValues.load(input, itemSet));
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
    protected BlockDrop checkAndCreateElement(BlockDropValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
        return new BlockDrop(values);
    }

    @Override
    protected void validateChange(BlockDropReference reference, BlockDropValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }

    public void combine(BlockDropManager primary, BlockDropManager secondary) {
        elements.addAll(primary.elements);
        elements.addAll(secondary.elements);
    }
}
