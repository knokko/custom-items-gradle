package nl.knokko.customrecipes.ingredient;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class CustomIngredient {

    public final Material material;
    public final Predicate<ItemStack> shouldAccept;
    public final int amount;

    public CustomIngredient(Material material, Predicate<ItemStack> shouldAccept, int amount) {
        this.material = material;
        this.shouldAccept = shouldAccept;
        this.amount = 1;
    }

    public CustomIngredient(Material material) {
        this(material, ingredient -> true, 1);
    }
}
