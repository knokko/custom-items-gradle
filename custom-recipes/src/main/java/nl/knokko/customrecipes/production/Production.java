package nl.knokko.customrecipes.production;

import org.bukkit.inventory.ItemStack;

public abstract class Production {

    public final ItemStack result;
    public final int maximumCustomCount;
    public final int maximumNaturalCount;
    public final boolean hasSpecialIngredients;

    public Production(ItemStack result, int maximumCustomCount, int maximumNaturalCount, boolean hasSpecialIngredients) {
        this.result = result;
        this.maximumCustomCount = maximumCustomCount;
        this.maximumNaturalCount = maximumNaturalCount;
        this.hasSpecialIngredients = hasSpecialIngredients;
    }

    @Override
    public String toString() {
        return "Production(" + result.getType() + " x " + result.getAmount() + " * " + maximumCustomCount + " vs " + maximumNaturalCount + ")";
    }
}
