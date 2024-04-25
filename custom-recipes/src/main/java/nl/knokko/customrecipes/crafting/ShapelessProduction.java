package nl.knokko.customrecipes.crafting;

import org.bukkit.inventory.ItemStack;

class ShapelessProduction extends Production {

    final ShapelessPlacement placement;
    final CustomShapelessRecipe recipe;

    ShapelessProduction(
            ItemStack result, int maximumCustomCount, int maximumNaturalCount,
            boolean hasSpecialIngredients, ShapelessPlacement placement, CustomShapelessRecipe recipe
    ) {
        super(result, maximumCustomCount, maximumNaturalCount, hasSpecialIngredients);
        this.placement = placement;
        this.recipe = recipe;
    }
}
