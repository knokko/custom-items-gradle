package nl.knokko.customitems.recipe;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ShapedRecipeValues extends CraftingRecipeValues {

    static ShapedRecipeValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        ShapedRecipeValues result = new ShapedRecipeValues(false);

        if (encoding == RecipeEncoding.SHAPED_RECIPE) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("ShapedCraftingRecipe", encoding);
        }

        return result;
    }

    public static ShapedRecipeValues createQuick(IngredientValues[] ingredients, ResultValues recipeResult) {
        ShapedRecipeValues result = new ShapedRecipeValues(true);
        for (int index = 0; index < 9; index++) {
            result.setIngredientAt(index % 3, index / 3, ingredients[index]);
        }
        result.setResult(recipeResult);
        return result;
    }

    private final IngredientValues[] ingredients;

    public ShapedRecipeValues(boolean mutable) {
        super(mutable);

        this.ingredients = new IngredientValues[9];
        Arrays.fill(this.ingredients, new NoIngredientValues());
    }

    public ShapedRecipeValues(ShapedRecipeValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.ingredients = new IngredientValues[9];
        for (int index = 0; index < 9; index++) {
            this.ingredients[index] = toCopy.ingredients[index].copy(false);
        }
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.result = ResultValues.load(input, itemSet);
        for (int index = 0; index < 9; index++) {
            this.ingredients[index] = IngredientValues.load(input, itemSet);
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.SHAPED_RECIPE);
        result.save(output);
        for (IngredientValues ingredient : ingredients) {
            ingredient.save(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ShapedRecipeValues) {
            ShapedRecipeValues otherRecipe = (ShapedRecipeValues) other;
            return result.equals(otherRecipe.result) && Arrays.equals(ingredients, otherRecipe.ingredients);
        } else {
            return false;
        }
    }

    @Override
    public ShapedRecipeValues copy(boolean mutable) {
        return new ShapedRecipeValues(this, mutable);
    }

    private void checkBounds(int x, int y) {
        if (x < 0 || x >= 3) throw new IllegalArgumentException("x (" + x + ") must be 0, 1, or 2");
        if (y < 0 || y >= 3) throw new IllegalArgumentException("y (" + y + ") must be 0, 1, or 2");
    }

    public IngredientValues getIngredientAt(int x, int y) {
        checkBounds(x, y);
        return ingredients[x + 3 * y];
    }

    public void setIngredientAt(int x, int y, IngredientValues newIngredient) {
        assertMutable();
        checkBounds(x, y);
        this.ingredients[x + 3 * y] = newIngredient.copy(false);
    }

    @Override
    public void validate(SItemSet itemSet, CraftingRecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, selfReference);

        if (ingredients == null) throw new ProgrammingValidationException("No ingredients");
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                IngredientValues ingredient = getIngredientAt(x, y);
                if (ingredient == null) throw new ProgrammingValidationException("Missing ingredient at (" + x + ", " + y + ")");
                Validation.scope("Ingredient at (" + x + ", " + y + ")", () -> ingredient.validateComplete(itemSet));
            }
        }

        boolean isEmpty = true;
        for (IngredientValues ingredient : ingredients) {
            if (!(ingredient instanceof NoIngredientValues)) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            throw new ValidationException("You need at least 1 ingredient");
        }

        for (CraftingRecipeReference otherReference : itemSet.getCraftingRecipeReferences().collect(Collectors.toList())) {
            if (selfReference == null || !selfReference.equals(otherReference)) {
                CraftingRecipeValues otherRecipe = otherReference.get();
                if (otherRecipe instanceof ShapedRecipeValues) {

                    IngredientValues[] otherIngredients = ((ShapedRecipeValues) otherRecipe).ingredients;
                    boolean conflicts = true;
                    for (int index = 0; index < 9; index++) {
                        if (!this.ingredients[index].conflictsWith(otherIngredients[index])) {
                            conflicts = false;
                            break;
                        }
                    }

                    if (conflicts) {
                        throw new ValidationException("Conflict with recipe for " + otherRecipe.getResult());
                    }
                }
            }
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);
        for (IngredientValues ingredient : ingredients) {
            Validation.scope("Ingredients", () -> ingredient.validateExportVersion(version));
        }
    }
}
