package nl.knokko.customrecipes.ingredient;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

public class CustomIngredient {

    public final Material material;
    public final Predicate<ItemStack> shouldAccept;
    public final int amount;
    public final Function<ItemStack, ItemStack> remainingItem;

    public CustomIngredient(
            Material material, Predicate<ItemStack> shouldAccept,
            int amount, Function<ItemStack, ItemStack> remainingItem
    ) {
        if (!material.isItem()) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid ingredient " + material + ": it must be an item");
            material = Material.BEDROCK;
        }
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

    @Override
    public String toString() {
        return "CustomIngredient(" + material + "," + (shouldAccept != null) + "," + amount + "," + remainingItem + ")";
    }

    public boolean accepts(ItemStack input) {
        if (input == null) return false;
        if (material != input.getType()) return false;
        if (amount > input.getAmount()) return false;
        if (remainingItem != null && amount != input.getAmount()) return false;
        return shouldAccept.test(input);
    }
}
