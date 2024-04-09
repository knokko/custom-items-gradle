package nl.knokko.customrecipes.collector;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ResultCollectorEvent {

    public final ItemStack result;
    public final int maximumProductionCount;
    public final Inventory destination;
    public final ItemStack oldCursor;
    public final Consumer<ItemStack> changeCursor;
    public final InventoryAction action;

    public int actualProductionCount = -1;

    public ResultCollectorEvent(
            ItemStack result, int maximumProductionCount, Inventory destination,
            ItemStack oldCursor, Consumer<ItemStack> changeCursor, InventoryAction action
    ) {
        this.result = result;
        this.maximumProductionCount = maximumProductionCount;
        this.destination = destination;
        this.oldCursor = oldCursor;
        this.changeCursor = changeCursor;
        this.action = action;
    }
}
