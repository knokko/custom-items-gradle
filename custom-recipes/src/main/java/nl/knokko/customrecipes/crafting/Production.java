package nl.knokko.customrecipes.crafting;

import org.bukkit.inventory.ItemStack;

abstract class Production {

    public final ItemStack result;
    final int maximumCustomCount;
    final int maximumNaturalCount;
    final boolean hasSpecialIngredients;

    Production(ItemStack result, int maximumCustomCount, int maximumNaturalCount, boolean hasSpecialIngredients) {
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
