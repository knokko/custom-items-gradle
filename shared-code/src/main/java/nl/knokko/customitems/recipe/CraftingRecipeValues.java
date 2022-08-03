package nl.knokko.customitems.recipe;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public abstract class CraftingRecipeValues extends ModelValues {

    public static CraftingRecipeValues load(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == RecipeEncoding.SHAPED_RECIPE || encoding == RecipeEncoding.SHAPED_RECIPE_2) {
            return ShapedRecipeValues.load(input, encoding, itemSet);
        } else if (encoding == RecipeEncoding.SHAPELESS_RECIPE || encoding == RecipeEncoding.SHAPELESS_RECIPE_2) {
            return ShapelessRecipeValues.load(input, encoding, itemSet);
        } else {
            throw new UnknownEncodingException("CraftingRecipe", encoding);
        }
    }

    protected ResultValues result;
    protected String requiredPermission;

    CraftingRecipeValues(boolean mutable) {
        super(mutable);

        SimpleVanillaResultValues mutableResult = new SimpleVanillaResultValues(true);
        mutableResult.setAmount((byte) 1);
        mutableResult.setMaterial(CIMaterial.IRON_INGOT);
        this.result = mutableResult.copy(false);
        this.requiredPermission = null;
    }

    CraftingRecipeValues(CraftingRecipeValues toCopy, boolean mutable) {
        super(mutable);

        this.result = toCopy.getResult();
        this.requiredPermission = toCopy.getRequiredPermission();
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract CraftingRecipeValues copy(boolean mutable);

    public ResultValues getResult() {
        return result;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setResult(ResultValues newResult) {
        assertMutable();
        Checks.notNull(newResult);
        this.result = newResult.copy(false);
    }

    public void setRequiredPermission(String requiredPermission) {
        assertMutable();
        this.requiredPermission = "".equals(requiredPermission) ? null : requiredPermission;
    }

    public void validate(ItemSet itemSet, CraftingRecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        if (result == null) throw new ProgrammingValidationException("No result");
        Validation.scope("Result", () -> result.validateComplete(itemSet));
        if ("".equals(requiredPermission)) throw new ProgrammingValidationException("Required permission can't be empty");
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Result", () -> result.validateExportVersion(version));
    }
}
