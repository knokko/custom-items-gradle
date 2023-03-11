package nl.knokko.customitems.recipe;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Arrays;
import java.util.Objects;

public class ShapedRecipeValues extends CraftingRecipeValues {

    static ShapedRecipeValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        ShapedRecipeValues result = new ShapedRecipeValues(false);

        if (encoding == RecipeEncoding.SHAPED_RECIPE) {
            result.load1(input, itemSet);
        } else if (encoding == RecipeEncoding.SHAPED_RECIPE_2) {
            result.load2(input, itemSet);
        } else if (encoding == RecipeEncoding.SHAPED_RECIPE_NEW) {
            result.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("ShapedCraftingRecipe", encoding);
        }

        return result;
    }

    public static ShapedRecipeValues createQuick(
            IngredientValues[] ingredients, ResultValues recipeResult, boolean ignoreDisplacement
    ) {
        ShapedRecipeValues result = new ShapedRecipeValues(true);
        result.setIgnoreDisplacement(ignoreDisplacement);
        for (int index = 0; index < 9; index++) {
            result.setIngredientAt(index % 3, index / 3, ingredients[index]);
        }
        result.setResult(recipeResult);
        return result;
    }

    private final IngredientValues[] ingredients;
    private boolean ignoreDisplacement;

    public ShapedRecipeValues(boolean mutable) {
        super(mutable);

        this.ingredients = new IngredientValues[9];
        Arrays.fill(this.ingredients, new NoIngredientValues());
        this.ignoreDisplacement = true;
    }

    public ShapedRecipeValues(ShapedRecipeValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.ingredients = new IngredientValues[9];
        for (int index = 0; index < 9; index++) {
            this.ingredients[index] = toCopy.ingredients[index].copy(false);
        }
        this.ignoreDisplacement = toCopy.shouldIgnoreDisplacement();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.result = ResultValues.load(input, itemSet);
        for (int index = 0; index < 9; index++) {
            this.ingredients[index] = IngredientValues.load(input, itemSet);
        }
        this.ignoreDisplacement = false;
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.load1(input, itemSet);
        this.requiredPermission = input.readString();
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ShapedRecipe", encoding);

        result = ResultValues.load(input, itemSet);
        for (int index = 0; index < 9; index++) {
            this.ingredients[index] = IngredientValues.load(input, itemSet);
        }
        ignoreDisplacement = input.readBoolean();
        requiredPermission = input.readString();
    }

    public int getEffectiveMinX() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (!(this.getIngredientAt(x, y) instanceof NoIngredientValues)) return x;
            }
        }
        return 2;
    }

    private int getEffectiveMaxX() {
        for (int x = 2; x >= 0; x--) {
            for (int y = 0; y < 3; y++) {
                if (!(this.getIngredientAt(x, y) instanceof NoIngredientValues)) return x;
            }
        }
        return 0;
    }

    public int getEffectiveWidth() {
        return 1 + getEffectiveMaxX() - getEffectiveMinX();
    }

    public int getEffectiveMinY() {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (!(this.getIngredientAt(x, y) instanceof NoIngredientValues)) return y;
            }
        }
        return 2;
    }

    private int getEffectiveMaxY() {
        for (int y = 2; y >= 0; y--) {
            for (int x = 0; x < 3; x++) {
                if (!(this.getIngredientAt(x, y) instanceof NoIngredientValues)) return y;
            }
        }
        return 0;
    }

    public int getEffectiveHeight() {
        return 1 + getEffectiveMaxY() - getEffectiveMinY();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.SHAPED_RECIPE_NEW);
        output.addByte((byte) 1);
        result.save(output);
        for (IngredientValues ingredient : ingredients) {
            ingredient.save(output);
        }
        output.addBoolean(ignoreDisplacement);
        output.addString(requiredPermission);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ShapedRecipeValues) {
            ShapedRecipeValues otherRecipe = (ShapedRecipeValues) other;
            return result.equals(otherRecipe.result) && Arrays.equals(ingredients, otherRecipe.ingredients) &&
                    this.ignoreDisplacement == otherRecipe.ignoreDisplacement &&
                    Objects.equals(this.requiredPermission, otherRecipe.requiredPermission);
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

    public boolean shouldIgnoreDisplacement() {
        return ignoreDisplacement;
    }

    public void setIngredientAt(int x, int y, IngredientValues newIngredient) {
        assertMutable();
        checkBounds(x, y);
        this.ingredients[x + 3 * y] = newIngredient.copy(false);
    }

    public void setIgnoreDisplacement(boolean ignoreDisplacement) {
        assertMutable();
        this.ignoreDisplacement = ignoreDisplacement;
    }

    @Override
    public void validate(ItemSet itemSet, CraftingRecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, selfReference);

        if (result instanceof UpgradeResultValues) {
            int ingredientIndex = ((UpgradeResultValues) result).getIngredientIndex();
            if (ingredientIndex < 0) throw new ProgrammingValidationException("Upgrade ingredient index can't be negative");
            if (ingredientIndex >= 9) throw new ProgrammingValidationException("Upgrade ingredient index must be smaller than 9");
            if (ingredients[ingredientIndex] instanceof NoIngredientValues) {
                throw new ValidationException("Ingredient to be upgraded can't be empty");
            }
        }

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

        for (CraftingRecipeReference otherReference : itemSet.getCraftingRecipes().references()) {
            if (selfReference == null || !selfReference.equals(otherReference)) {
                CraftingRecipeValues otherRecipe = otherReference.get();
                if (otherRecipe instanceof ShapedRecipeValues) {
                    if (this.conflictsWith((ShapedRecipeValues) otherRecipe)) {
                        throw new ValidationException("Conflict with recipe for " + otherRecipe.getResult());
                    }
                }
            }
        }
    }

    public boolean conflictsWith(ShapedRecipeValues otherRecipe) {
        int ownWidth = this.getEffectiveWidth();
        int ownHeight = this.getEffectiveHeight();
        int otherWidth = otherRecipe.getEffectiveWidth();
        int otherHeight = otherRecipe.getEffectiveHeight();
        if (ownWidth != otherWidth || ownHeight != otherHeight) return false;

        int ownMinX = this.getEffectiveMinX();
        int ownMinY = this.getEffectiveMinY();
        int otherMinX = otherRecipe.getEffectiveMinX();
        int otherMinY = otherRecipe.getEffectiveMinY();

        if (!this.ignoreDisplacement && !otherRecipe.ignoreDisplacement && (ownMinX != otherMinX || ownMinY != otherMinY)) return false;

        for (int x = 0; x < ownWidth; x++) {
            for (int y = 0; y < ownHeight; y++) {
                IngredientValues ownIngredient = this.getIngredientAt(x + ownMinX, y + ownMinY);
                IngredientValues otherIngredient = otherRecipe.getIngredientAt(x + otherMinX, y + otherMinY);
                if (!ownIngredient.conflictsWith(otherIngredient)) return false;
            }
        }

        return true;
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);
        for (IngredientValues ingredient : ingredients) {
            Validation.scope("Ingredients", () -> ingredient.validateExportVersion(version));
        }
    }
}
