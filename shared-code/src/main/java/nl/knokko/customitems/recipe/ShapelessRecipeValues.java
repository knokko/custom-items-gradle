package nl.knokko.customitems.recipe;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.ingredient.SIngredient;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class ShapelessRecipeValues extends CraftingRecipeValues {

    private Collection<SIngredient> ingredients;

    public ShapelessRecipeValues(boolean mutable) {
        super(mutable);

        this.ingredients = new ArrayList<>();
    }

    public ShapelessRecipeValues(ShapelessRecipeValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.ingredients = toCopy.getIngredients();
    }

    @Override
    public ShapelessRecipeValues copy(boolean mutable) {
        return new ShapelessRecipeValues(this, mutable);
    }

    public Collection<SIngredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(Collection<SIngredient> newIngredients) {
        assertMutable();
        Checks.nonNull(newIngredients);
        this.ingredients = Mutability.createDeepCopy(newIngredients, false);
    }

    @Override
    public void validate(SItemSet itemSet, RecipeReference selfReference) throws ValidationException, ProgrammingValidationException {
        super.validate(itemSet, selfReference);

        if (ingredients == null) throw new ProgrammingValidationException("No ingredients");
        int ingredientIndex = 0;
        for (SIngredient ingredient : ingredients) {
            ingredientIndex++;

            if (ingredient == null) throw new ProgrammingValidationException("Missing ingredient " + ingredientIndex);
            Validation.scope("Ingredient " + ingredientIndex, () -> ingredient.validateComplete(itemSet));
        }

        // TODO Check for conflicts
    }
}
