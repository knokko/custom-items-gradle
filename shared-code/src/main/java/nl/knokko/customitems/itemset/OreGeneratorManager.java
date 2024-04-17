package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.worldgen.OreGenerator;

public class OreGeneratorManager extends ModelManager<OreGenerator, OreGeneratorReference> {

    protected OreGeneratorManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(OreGenerator oreGenerator, BitOutput output, ItemSet.Side targetSide) {
        oreGenerator.save(output);
    }

    @Override
    OreGeneratorReference createReference(Model<OreGenerator> element) {
        return new OreGeneratorReference(element);
    }

    @Override
    protected OreGenerator loadElement(BitInput input) throws UnknownEncodingException {
        return OreGenerator.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(OreGenerator oreVein, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Ore vein generator " + oreVein, oreVein::validateExportVersion, mcVersion);
    }

    @Override
    protected void validate(OreGenerator oreVeinGenerator) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Ore vein generator " + oreVeinGenerator,
                () -> oreVeinGenerator.validate(itemSet)
        );
    }

    @Override
    protected void validateCreation(OreGenerator values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(OreGeneratorReference reference, OreGenerator newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
