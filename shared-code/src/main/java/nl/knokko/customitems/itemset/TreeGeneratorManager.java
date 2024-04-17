package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.worldgen.TreeGenerator;

public class TreeGeneratorManager extends ModelManager<TreeGenerator, TreeGeneratorReference> {

    protected TreeGeneratorManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(TreeGenerator treeGenerator, BitOutput output, ItemSet.Side targetSide) {
        treeGenerator.save(output);
    }

    @Override
    TreeGeneratorReference createReference(Model<TreeGenerator> element) {
        return new TreeGeneratorReference(element);
    }

    @Override
    protected TreeGenerator loadElement(BitInput input) throws UnknownEncodingException {
        return TreeGenerator.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(
            TreeGenerator treeGenerator, int mcVersion
    ) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Tree generator " + treeGenerator, treeGenerator::validateExportVersion, mcVersion);
    }

    @Override
    protected void validate(TreeGenerator treeGenerator) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Tree generator " + treeGenerator, () -> treeGenerator.validate(itemSet));
    }

    @Override
    protected void validateCreation(TreeGenerator values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(TreeGeneratorReference reference, TreeGenerator newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
