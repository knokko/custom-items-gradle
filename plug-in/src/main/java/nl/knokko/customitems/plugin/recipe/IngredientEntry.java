package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class IngredientEntry {

    public final Ingredient ingredient;
    public final int itemIndex;

    public IngredientEntry(Ingredient ingredient, int itemIndex) {
        this.ingredient = ingredient;
        this.itemIndex = itemIndex;
    }
}
