package nl.knokko.customrecipes.ingredient;

import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class IngredientBlocker {

    public final Predicate<String> isForbiddenNamespace;
    public final Predicate<ItemStack> isIngredient;

    public IngredientBlocker(Predicate<String> isForbiddenNamespace, Predicate<ItemStack> isIngredient) {
        this.isForbiddenNamespace = isForbiddenNamespace;
        this.isIngredient = isIngredient;
    }

    public IngredientBlocker(Predicate<ItemStack> isIngredient) {
        this.isForbiddenNamespace = namespace -> true;
        this.isIngredient = isIngredient;
    }
}
