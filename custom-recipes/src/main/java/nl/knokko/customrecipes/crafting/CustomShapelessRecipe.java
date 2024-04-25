package nl.knokko.customrecipes.crafting;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import java.util.function.Function;
import java.util.function.Predicate;

public class CustomShapelessRecipe {

    public final Function<ItemStack[], ItemStack> result;
    public final Predicate<HumanEntity> canCraft;
    public final CustomIngredient[] ingredients;

    public CustomShapelessRecipe(
            Function<ItemStack[], ItemStack> result,
            Predicate<HumanEntity> canCraft,
            CustomIngredient... ingredients
    ) {
        this.result = result;
        this.canCraft = canCraft;
        this.ingredients = ingredients;
    }

    public CustomShapelessRecipe(ItemStack result, CustomIngredient... ingredients) {
        this(sortedIngredients -> result, crafter -> true, ingredients);
    }
}
