package nl.knokko.customrecipes.smithing;

import nl.knokko.customrecipes.ingredient.CustomIngredient;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public class CustomSmithingRecipe {

    public final Function<ItemStack[], ItemStack> result;
    public final CustomIngredient[] ingredients;
    public final Predicate<HumanEntity> canCraft;

    public CustomSmithingRecipe(
            Function<ItemStack[], ItemStack> result,
            Predicate<HumanEntity> canCraft,
            CustomIngredient... ingredients
    ) {
        if (ingredients.length != 3) throw new IllegalArgumentException("Smithing recipes need exactly 3 ingredients");
        this.result = result;
        this.ingredients = ingredients;
        this.canCraft = canCraft;
    }

    public CustomSmithingRecipe(Function<ItemStack[], ItemStack> result, CustomIngredient... ingredients) {
        this(result, smith -> true, ingredients);
    }
}
