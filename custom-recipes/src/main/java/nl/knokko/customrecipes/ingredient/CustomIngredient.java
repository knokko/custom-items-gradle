package nl.knokko.customrecipes.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class CustomIngredient {

    public final Material material;
    public final Predicate<ItemStack> shouldAccept;
    public final int amount;
    public final ItemStack remainingItem;

    public CustomIngredient(
            Material material, Predicate<ItemStack> shouldAccept,
            int amount, ItemStack remainingItem
    ) {
        this.material = material;
        this.shouldAccept = shouldAccept;
        this.amount = amount;
        this.remainingItem = remainingItem;
    }

    public CustomIngredient(Material material, Predicate<ItemStack> shouldAccept) {
        this(material, shouldAccept, 1, null);
    }

    public CustomIngredient(Material material) {
        this(material, ingredient -> true);
    }
}
