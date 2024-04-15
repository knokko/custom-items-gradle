package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.worldgen.TreeGenerator;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;

public class TreeGeneratorManager extends ModelManager<TreeGenerator, TreeGeneratorValues, TreeGeneratorReference> {

    protected TreeGeneratorManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(TreeGenerator treeGenerator, BitOutput output, ItemSet.Side targetSide) {
        treeGenerator.getValues().save(output);
    }

    @Override
    protected TreeGeneratorReference createReference(TreeGenerator element) {
        return new TreeGeneratorReference(element);
    }

    @Override
    protected TreeGenerator loadElement(BitInput input) throws UnknownEncodingException {
        return new TreeGenerator(TreeGeneratorValues.load(input, itemSet));
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
    protected TreeGenerator checkAndCreateElement(TreeGeneratorValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
        return new TreeGenerator(values);
    }

    @Override
    protected void validateChange(TreeGeneratorReference reference, TreeGeneratorValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
