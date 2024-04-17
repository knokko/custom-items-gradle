package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.encoding.RecipeEncoding.*;

public abstract class KciCraftingRecipe extends ModelValues {

    public static KciCraftingRecipe load(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == SHAPED_RECIPE || encoding == SHAPED_RECIPE_2 || encoding == SHAPED_RECIPE_NEW) {
            return KciShapedRecipe.load(input, encoding, itemSet);
        } else if (encoding == SHAPELESS_RECIPE || encoding == SHAPELESS_RECIPE_2) {
            return KciShapelessRecipe.load(input, encoding, itemSet);
        } else {
            throw new UnknownEncodingException("CraftingRecipe", encoding);
        }
    }

    protected KciResult result;
    protected String requiredPermission;

    KciCraftingRecipe(boolean mutable) {
        super(mutable);

        SimpleVanillaResult mutableResult = new SimpleVanillaResult(true);
        mutableResult.setAmount((byte) 1);
        mutableResult.setMaterial(VMaterial.IRON_INGOT);
        this.result = mutableResult.copy(false);
        this.requiredPermission = null;
    }

    KciCraftingRecipe(KciCraftingRecipe toCopy, boolean mutable) {
        super(mutable);

        this.result = toCopy.getResult();
        this.requiredPermission = toCopy.getRequiredPermission();
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract KciCraftingRecipe copy(boolean mutable);

    public KciResult getResult() {
        return result;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setResult(KciResult newResult) {
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
