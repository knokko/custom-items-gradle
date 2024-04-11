package nl.knokko.customrecipes.production;

import nl.knokko.customrecipes.shaped.CustomShapedRecipe;
import nl.knokko.customrecipes.shaped.ShapedPlacement;
import org.bukkit.inventory.ItemStack;

public class ShapedProduction extends Production {

    public final ShapedPlacement placement;
    public final CustomShapedRecipe recipe;

    public ShapedProduction(
            ItemStack result, int maximumCustomCount, int maximumNaturalCount,
            boolean hasSpecialIngredients, ShapedPlacement placement, CustomShapedRecipe recipe
    ) {
        super(result, maximumCustomCount, maximumNaturalCount, hasSpecialIngredients);
        this.placement = placement;
        this.recipe = recipe;
    }
}
