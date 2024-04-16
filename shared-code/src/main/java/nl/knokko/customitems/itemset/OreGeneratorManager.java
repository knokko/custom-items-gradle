package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;

public class OreGeneratorManager extends ModelManager<OreVeinGeneratorValues, OreVeinGeneratorReference> {

    protected OreGeneratorManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(OreVeinGeneratorValues oreGenerator, BitOutput output, ItemSet.Side targetSide) {
        oreGenerator.save(output);
    }

    @Override
    OreVeinGeneratorReference createReference(Model<OreVeinGeneratorValues> element) {
        return new OreVeinGeneratorReference(element);
    }

    @Override
    protected OreVeinGeneratorValues loadElement(BitInput input) throws UnknownEncodingException {
        return OreVeinGeneratorValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(OreVeinGeneratorValues oreVein, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Ore vein generator " + oreVein, oreVein::validateExportVersion, mcVersion);
    }

    @Override
    protected void validate(OreVeinGeneratorValues oreVeinGenerator) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Ore vein generator " + oreVeinGenerator,
                () -> oreVeinGenerator.validate(itemSet)
        );
    }

    @Override
    protected void validateCreation(OreVeinGeneratorValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(OreVeinGeneratorReference reference, OreVeinGeneratorValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
