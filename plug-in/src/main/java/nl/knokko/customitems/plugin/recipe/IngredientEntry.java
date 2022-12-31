package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.recipe.ingredient.IngredientValues;

public class IngredientEntry {

    public final IngredientValues ingredient;
    public final int ingredientIndex;
    public final int itemIndex;

    public IngredientEntry(IngredientValues ingredient, int ingredientIndex, int itemIndex) {
        this.ingredient = ingredient;
        this.ingredientIndex = ingredientIndex;
        this.itemIndex = itemIndex;
    }
}
