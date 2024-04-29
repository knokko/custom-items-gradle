package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.KciCookingRecipe;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class CookingRecipeManager extends ModelManager<KciCookingRecipe, CookingRecipeReference> {

    CookingRecipeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciCookingRecipe recipe, BitOutput output, ItemSet.Side targetSide) {
        recipe.save(output);
    }

    @Override
    CookingRecipeReference createReference(Model<KciCookingRecipe> element) {
        return new CookingRecipeReference(element);
    }

    @Override
    protected KciCookingRecipe loadElement(BitInput input) throws UnknownEncodingException {
        return KciCookingRecipe.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(KciCookingRecipe recipe, int mcVersion) throws ValidationException, ProgrammingValidationException {
        recipe.validateExportVersion(mcVersion);
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        for (Model<KciCookingRecipe> model : elements) {
            KciCookingRecipe recipe = model.getValues();
            Validation.scope(
                    "Furnace recipe for " + recipe.getResult(),
                    () -> recipe.validate(itemSet, new CookingRecipeReference(model))
            );
        }
    }

    @Override
    protected void validate(KciCookingRecipe recipe) throws ValidationException, ProgrammingValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void validateCreation(KciCookingRecipe recipe) throws ValidationException, ProgrammingValidationException {
        recipe.validate(itemSet, null);
    }

    @Override
    protected void validateChange(CookingRecipeReference reference, KciCookingRecipe newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference);
    }
}
