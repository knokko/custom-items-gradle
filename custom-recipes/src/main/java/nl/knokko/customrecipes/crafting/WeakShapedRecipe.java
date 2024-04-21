package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Map;

class WeakShapedRecipe {

    final Material[][] shape;

    WeakShapedRecipe(String[] shape, Map<Character, CustomIngredient> ingredientMap) {
        this.shape = new Material[shape[0].length()][shape.length];
        for (int x = 0; x < shape[0].length(); x++) {
            for (int y = 0; y < shape.length; y++) {
                char key = shape[y].charAt(x);
                CustomIngredient customIngredient = ingredientMap.get(key);
                if (customIngredient == null) this.shape[x][y] = Material.AIR;
                else this.shape[x][y] = customIngredient.material;
            }
        }
    }

    @Override
    public String toString() {
        return "WeakShapedRecipe(" + Arrays.deepToString(shape) + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof WeakShapedRecipe) {
            WeakShapedRecipe weak = (WeakShapedRecipe) other;
            return Arrays.deepEquals(this.shape, weak.shape);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(shape);
    }
}
