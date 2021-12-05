package nl.knokko.customitems.recipe;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class CraftingRecipeValues extends ModelValues {

    public static CraftingRecipeValues load(
            BitInput input, SItemSet itemSet
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == RecipeEncoding.SHAPED_RECIPE) {
            return ShapedRecipeValues.load(input, encoding, itemSet);
        } else if (encoding == RecipeEncoding.SHAPELESS_RECIPE) {
            return ShapelessRecipeValues.load(input, encoding, itemSet);
        } else {
            throw new UnknownEncodingException("CraftingRecipe", encoding);
        }
    }

    protected ResultValues result;

    CraftingRecipeValues(boolean mutable) {
        super(mutable);

        SimpleVanillaResultValues mutableResult = new SimpleVanillaResultValues(true);
        mutableResult.setAmount((byte) 1);
        mutableResult.setMaterial(CIMaterial.IRON_INGOT);
        this.result = mutableResult.copy(false);
    }

    CraftingRecipeValues(CraftingRecipeValues toCopy, boolean mutable) {
        super(mutable);

        this.result = toCopy.getResult();
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract CraftingRecipeValues copy(boolean mutable);

    public ResultValues getResult() {
        return result;
    }

    public void setResult(ResultValues newResult) {
        assertMutable();
        Checks.notNull(newResult);
        this.result = newResult.copy(false);
    }

    public void validate(SItemSet itemSet, CraftingRecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        if (result == null) throw new ProgrammingValidationException("No result");
        Validation.scope("Result", () -> result.validateComplete(itemSet));
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Result", () -> result.validateExportVersion(version));
    }
}
