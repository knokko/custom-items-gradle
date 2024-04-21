package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomShapedRecipe {

    public final ItemStack result;
    public final String[] shape;
    public final Map<Character, CustomIngredient> ingredientMap = new HashMap<>();

    public CustomShapedRecipe(ItemStack result, String... shape) {
        this.result = result;
        this.shape = shape;
    }
}
