package nl.knokko.customrecipes.production;

import nl.knokko.customrecipes.shaped.ShapedPlacement;
import org.bukkit.inventory.ItemStack;

public class ShapedProduction extends Production {

    public final ShapedPlacement placement;

    public ShapedProduction(
            ItemStack result, int maximumCustomCount, int maximumNaturalCount,
            boolean hasSpecialIngredients, ShapedPlacement placement
    ) {
        super(result, maximumCustomCount, maximumNaturalCount, hasSpecialIngredients);
        this.placement = placement;
    }
}
