package nl.knokko.customrecipes.production;

import org.bukkit.inventory.ItemStack;

public abstract class Production {

    public final ItemStack result;
    public final int maximumCount;
    public final boolean needsManualWork;

    public Production(ItemStack result, int maximumCount, boolean needsManualWork) {
        this.result = result;
        this.maximumCount = maximumCount;
        this.needsManualWork = needsManualWork;
    }

    @Override
    public String toString() {
        return "Production(" + result.getType() + " x " + result.getAmount() + " * " + maximumCount + ")";
    }
}
