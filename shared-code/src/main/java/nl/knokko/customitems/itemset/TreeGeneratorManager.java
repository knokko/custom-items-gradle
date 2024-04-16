package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;

public class TreeGeneratorManager extends ModelManager<TreeGeneratorValues, TreeGeneratorReference> {

    protected TreeGeneratorManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(TreeGeneratorValues treeGenerator, BitOutput output, ItemSet.Side targetSide) {
        treeGenerator.save(output);
    }

    @Override
    TreeGeneratorReference createReference(Model<TreeGeneratorValues> element) {
        return new TreeGeneratorReference(element);
    }

    @Override
    protected TreeGeneratorValues loadElement(BitInput input) throws UnknownEncodingException {
        return TreeGeneratorValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(
            TreeGeneratorValues treeGenerator, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Tree generator " + treeGenerator, treeGenerator::validateExportVersion, mcVersion);
    }

    @Override
    protected void validate(TreeGeneratorValues treeGenerator) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Tree generator " + treeGenerator, () -> treeGenerator.validate(itemSet));
    }

    @Override
    protected void validateCreation(TreeGeneratorValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(TreeGeneratorReference reference, TreeGeneratorValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
