package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.FurnaceRecipeValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class FurnaceRecipeManager extends ModelManager<FurnaceRecipeValues, FurnaceRecipeReference> {

    FurnaceRecipeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(FurnaceRecipeValues recipe, BitOutput output, ItemSet.Side targetSide) {
        recipe.save(output);
    }

    @Override
    FurnaceRecipeReference createReference(Model<FurnaceRecipeValues> element) {
        return new FurnaceRecipeReference(element);
    }

    @Override
    protected FurnaceRecipeValues loadElement(BitInput input) throws UnknownEncodingException {
        return FurnaceRecipeValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(FurnaceRecipeValues recipe, int mcVersion) throws ValidationException, ProgrammingValidationException {
        recipe.validateExportVersion(mcVersion);
    }

    @Override
    protected void validate(FurnaceRecipeValues recipe) throws ValidationException, ProgrammingValidationException {
        recipe.validate(itemSet);
    }

    @Override
    protected void validateCreation(FurnaceRecipeValues recipe) throws ValidationException, ProgrammingValidationException {
        recipe.validate(itemSet);
    }

    @Override
    protected void validateChange(FurnaceRecipeReference reference, FurnaceRecipeValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
