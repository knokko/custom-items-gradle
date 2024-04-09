package nl.knokko.customrecipes.shaped;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WeakShapedRecipe {

    public final String[] shape;
    public final Map<Character, Material> materialMap = new HashMap<>();

    public WeakShapedRecipe(String[] shape, Map<Character, CustomIngredient> ingredientMap) {
        this.shape = shape;
        ingredientMap.forEach((key, ingredient) -> materialMap.put(key, ingredient.material));
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof WeakShapedRecipe) {
            WeakShapedRecipe weak = (WeakShapedRecipe) other;
            return Arrays.equals(this.shape, weak.shape) && this.materialMap.equals(weak.materialMap);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(shape) + 13 * materialMap.hashCode();
    }
}
