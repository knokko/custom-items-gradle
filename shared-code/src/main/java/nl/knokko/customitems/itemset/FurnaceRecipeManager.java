package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.recipe.KciFurnaceRecipe;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class FurnaceRecipeManager extends ModelManager<KciFurnaceRecipe, FurnaceRecipeReference> {

    FurnaceRecipeManager(ItemSet itemSet) {
        super(itemSet);
    }

    @Override
    protected void saveElement(KciFurnaceRecipe recipe, BitOutput output, ItemSet.Side targetSide) {
        recipe.save(output);
    }

    @Override
    FurnaceRecipeReference createReference(Model<KciFurnaceRecipe> element) {
        return new FurnaceRecipeReference(element);
    }

    @Override
    protected KciFurnaceRecipe loadElement(BitInput input) throws UnknownEncodingException {
        return KciFurnaceRecipe.load(input, itemSet);
    }

    @Override
    protected void validateExportVersion(KciFurnaceRecipe recipe, int mcVersion) throws ValidationException, ProgrammingValidationException {
        recipe.validateExportVersion(mcVersion);
    }

    @Override
    protected void validate(KciFurnaceRecipe recipe) throws ValidationException, ProgrammingValidationException {
        recipe.validate(itemSet);
    }

    @Override
    protected void validateCreation(KciFurnaceRecipe recipe) throws ValidationException, ProgrammingValidationException {
        recipe.validate(itemSet);
    }

    @Override
    protected void validateChange(FurnaceRecipeReference reference, KciFurnaceRecipe newValues) throws ValidationException, ProgrammingValidationException {
        newValues.validate(itemSet);
    }
}
