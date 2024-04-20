package nl.knokko.customrecipes.furnace;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class CustomFurnaceRecipe {

    public final Function<ItemStack, ItemStack> result;
    public final CustomIngredient input;
    public final float experience;
    public final int cookingTime;

    public CustomFurnaceRecipe(Function<ItemStack, ItemStack> result, CustomIngredient input, float experience, int cookingTime) {
        this.result = result;
        this.input = input;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }
}
