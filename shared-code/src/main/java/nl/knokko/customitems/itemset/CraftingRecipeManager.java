package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.KciCraftingRecipe;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class CraftingRecipeManager extends ModelManager<KciCraftingRecipe, CraftingRecipeReference> {

    protected CraftingRecipeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciCraftingRecipe element, BitOutput output, ItemSet.Side targetSide) {
        element.save(output);
    }

    @Override
    CraftingRecipeReference createReference(Model<KciCraftingRecipe> element) {
        return new CraftingRecipeReference(element);
    }

    @Override
    protected KciCraftingRecipe loadElement(BitInput input) throws UnknownEncodingException {
        return KciCraftingRecipe.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(KciCraftingRecipe recipe, int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope(
                "Recipe for " + recipe.getResult(),
                () -> recipe.validateExportVersion(mcVersion)
        );
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        for (Model<KciCraftingRecipe> model : elements) {
            KciCraftingRecipe recipe = model.getValues();
            Validation.scope(
                    "Recipe for " + recipe.getResult(),
                    () -> recipe.validate(itemSet, new CraftingRecipeReference(model))
            );
        }
    }

    @Override
    protected void validateCreation(KciCraftingRecipe values) throws ValidationException, ProgrammingValidationException {
        values.validate(itemSet, null);
    }

    @Override
    protected void validate(KciCraftingRecipe recipe) throws ValidationException, ProgrammingValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void validateChange(CraftingRecipeReference reference, KciCraftingRecipe newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet, reference);
    }
}
