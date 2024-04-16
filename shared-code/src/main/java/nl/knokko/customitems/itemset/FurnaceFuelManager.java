package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.FurnaceFuelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class FurnaceFuelManager extends ModelManager<FurnaceFuelValues, FurnaceFuelReference> {

    FurnaceFuelManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(FurnaceFuelValues fuel, BitOutput output, ItemSet.Side targetSide) {
        fuel.save(output);
    }

    @Override
    FurnaceFuelReference createReference(Model<FurnaceFuelValues> element) {
        return new FurnaceFuelReference(element);
    }

    @Override
    protected FurnaceFuelValues loadElement(BitInput input) throws UnknownEncodingException {
        return FurnaceFuelValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(FurnaceFuelValues fuel, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Furnace fuel", fuel::validateExportVersion, mcVersion);
    }

    @Override
    protected void validate(FurnaceFuelValues fuel) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Furnace fuel", fuel::validate, itemSet);
    }

    @Override
    protected void validateCreation(FurnaceFuelValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet);
    }

    @Override
    protected void validateChange(FurnaceFuelReference reference, FurnaceFuelValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
