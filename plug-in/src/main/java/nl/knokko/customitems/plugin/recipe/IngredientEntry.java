package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.recipe.ingredient.IngredientValues;

public class IngredientEntry {

    public final IngredientValues ingredient;
    public final int itemIndex;

    public IngredientEntry(IngredientValues ingredient, int itemIndex) {
        this.ingredient = ingredient;
        this.itemIndex = itemIndex;
    }
}
