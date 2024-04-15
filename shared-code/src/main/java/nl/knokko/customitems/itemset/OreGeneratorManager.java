package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.worldgen.OreVeinGenerator;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;

public class OreGeneratorManager extends ModelManager<OreVeinGenerator, OreVeinGeneratorValues, OreVeinGeneratorReference> {

    protected OreGeneratorManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(OreVeinGenerator oreGenerator, BitOutput output, ItemSet.Side targetSide) {
        oreGenerator.getValues().save(output);
    }

    @Override
    protected OreVeinGeneratorReference createReference(OreVeinGenerator element) {
        return new OreVeinGeneratorReference(element);
    }

    @Override
    protected OreVeinGenerator loadElement(BitInput input) throws UnknownEncodingException {
        return new OreVeinGenerator(OreVeinGeneratorValues.load(input, itemSet));
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
    protected OreVeinGenerator checkAndCreateElement(OreVeinGeneratorValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
        return new OreVeinGenerator(values);
    }

    @Override
    protected void validateChange(OreVeinGeneratorReference reference, OreVeinGeneratorValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
