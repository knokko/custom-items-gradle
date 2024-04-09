package nl.knokko.customrecipes.production;

import nl.knokko.customrecipes.shaped.ShapedPlacement;
import org.bukkit.inventory.ItemStack;

public class ShapedProduction extends Production {

    public final ShapedPlacement placement;

    public ShapedProduction(ItemStack result, int maximumCount, boolean needsManualWork, ShapedPlacement placement) {
        super(result, maximumCount, needsManualWork);
        this.placement = placement;
    }
}
