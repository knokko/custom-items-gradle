package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.KciFurnaceFuel;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class FurnaceFuelManager extends ModelManager<KciFurnaceFuel, FurnaceFuelReference> {

    FurnaceFuelManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciFurnaceFuel fuel, BitOutput output, ItemSet.Side targetSide) {
        fuel.save(output);
    }

    @Override
    FurnaceFuelReference createReference(Model<KciFurnaceFuel> element) {
        return new FurnaceFuelReference(element);
    }

    @Override
    protected KciFurnaceFuel loadElement(BitInput input) throws UnknownEncodingException {
        return KciFurnaceFuel.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(KciFurnaceFuel fuel, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Furnace fuel", fuel::validateExportVersion, mcVersion);
    }

    @Override
    protected void validate(KciFurnaceFuel fuel) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Furnace fuel", fuel::validate, itemSet);
    }

    @Override
    protected void validateCreation(KciFurnaceFuel values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(FurnaceFuelReference reference, KciFurnaceFuel newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
