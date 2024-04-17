package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.recipe.ingredient.KciIngredient;

public class IngredientEntry {

    public final KciIngredient ingredient;
    public final int ingredientIndex;
    public final int itemIndex;

    public IngredientEntry(KciIngredient ingredient, int ingredientIndex, int itemIndex) {
        this.ingredient = ingredient;
        this.ingredientIndex = ingredientIndex;
        this.itemIndex = itemIndex;
    }
}
