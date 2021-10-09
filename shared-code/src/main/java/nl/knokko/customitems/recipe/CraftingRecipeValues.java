package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.result.SResult;
import nl.knokko.customitems.recipe.result.SSimpleVanillaResult;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public abstract class CraftingRecipeValues extends ModelValues {

    protected SResult result;

    CraftingRecipeValues(boolean mutable) {
        super(mutable);

        SSimpleVanillaResult mutableResult = new SSimpleVanillaResult(true);
        mutableResult.setAmount((byte) 1);
        mutableResult.setMaterial(CIMaterial.IRON_INGOT);
        this.result = mutableResult.copy(false);
    }

    CraftingRecipeValues(CraftingRecipeValues toCopy, boolean mutable) {
        super(mutable);

        this.result = toCopy.getResult();
    }

    @Override
    public abstract CraftingRecipeValues copy(boolean mutable);

    public SResult getResult() {
        return result;
    }

    public void setResult(SResult newResult) {
        assertMutable();
        Checks.notNull(newResult);
        this.result = newResult.copy(false);
    }

    public void validate(SItemSet itemSet, RecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        if (result == null) throw new ProgrammingValidationException("No result");
        Validation.scope("Result", () -> result.validateComplete(itemSet));
    }
}
