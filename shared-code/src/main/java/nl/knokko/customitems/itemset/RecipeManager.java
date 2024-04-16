package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class RecipeManager extends ModelManager<CraftingRecipeValues, CraftingRecipeReference> {

    protected RecipeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(CraftingRecipeValues element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    CraftingRecipeReference createReference(Model<CraftingRecipeValues> element) {
        return new CraftingRecipeReference(element);
    }

    @Override
    protected CraftingRecipeValues loadElement(BitInput input) throws UnknownEncodingException {
        return CraftingRecipeValues.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(CraftingRecipeValues recipe, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Recipe for " + recipe.getResult(),
                () -> recipe.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        for (Model<CraftingRecipeValues> model : elements) {
            CraftingRecipeValues recipe = model.getValues();
            Validation.scope(
                    "Recipe for " + recipe.getResult(),
                    () -> recipe.validate(itemSet, new CraftingRecipeReference(model))
            );
        }
    }

    @Override
    protected void validateCreation(CraftingRecipeValues values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(CraftingRecipeValues recipe) throws ValidationException, ProgrammingValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void validateChange(CraftingRecipeReference reference, CraftingRecipeValues newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference);
    }
}
