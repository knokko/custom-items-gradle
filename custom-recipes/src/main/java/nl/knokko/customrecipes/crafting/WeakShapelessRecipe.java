package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

class WeakShapelessRecipe {

    final Map<Material, Integer> ingredients = new EnumMap<>(Material.class);

    WeakShapelessRecipe(CustomIngredient[] rawIngredients) {
        if (rawIngredients.length > 9) throw new IllegalArgumentException("Too many ingredients: " + rawIngredients.length);

        for (CustomIngredient ingredient : rawIngredients) {
            if (ingredients.containsKey(ingredient.material)) {
                ingredients.put(ingredient.material, ingredients.get(ingredient.material) + 1);
            } else ingredients.put(ingredient.material, 1);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof WeakShapelessRecipe && this.ingredients.equals(((WeakShapelessRecipe) other).ingredients);
    }

    @Override
    public int hashCode() {
        return this.ingredients.hashCode();
    }
}
