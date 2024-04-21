package nl.knokko.customrecipes.crafting;

import org.bukkit.inventory.ItemStack;

class ShapedProduction extends Production {

    final ShapedPlacement placement;
    final CustomShapedRecipe recipe;

    ShapedProduction(
            ItemStack result, int maximumCustomCount, int maximumNaturalCount,
            boolean hasSpecialIngredients, ShapedPlacement placement, CustomShapedRecipe recipe
    ) {
        super(result, maximumCustomCount, maximumNaturalCount, hasSpecialIngredients);
        this.placement = placement;
        this.recipe = recipe;
    }
}
