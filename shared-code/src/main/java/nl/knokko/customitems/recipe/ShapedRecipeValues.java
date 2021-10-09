package nl.knokko.customitems.recipe;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.SIngredient;
import nl.knokko.customitems.recipe.ingredient.SNoIngredient;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Arrays;

public class ShapedRecipeValues extends CraftingRecipeValues {

    private SIngredient[] ingredients;

    public ShapedRecipeValues(boolean mutable) {
        super(mutable);

        this.ingredients = new SIngredient[9];
        Arrays.fill(this.ingredients, new SNoIngredient());
    }

    public ShapedRecipeValues(ShapedRecipeValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.ingredients = new SIngredient[9];
        for (int index = 0; index < 9; index++) {
            this.ingredients[index] = toCopy.ingredients[index].copy(false);
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

    public SIngredient getIngredientAt(int x, int y) {
        checkBounds(x, y);
        return ingredients[x + 3 * y];
    }

    public void setIngredientAt(int x, int y, SIngredient newIngredient) {
        assertMutable();
        checkBounds(x, y);
        this.ingredients[x + 3 * y] = newIngredient.copy(false);
    }

    @Override
    public void validate(SItemSet itemSet, RecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, selfReference);

        if (ingredients == null) throw new ProgrammingValidationException("No ingredients");
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                SIngredient ingredient = getIngredientAt(x, y);
                if (ingredient == null) throw new ProgrammingValidationException("Missing ingredient at (" + x + ", " + y + ")");
                Validation.scope("Ingredient at (" + x + ", " + y + ")", () -> ingredient.validateComplete(itemSet));
            }
        }

        // TODO Check for conflicts
    }
}
