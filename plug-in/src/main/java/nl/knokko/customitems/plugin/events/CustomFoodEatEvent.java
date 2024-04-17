package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.KciFood;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class CustomFoodEatEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public final ItemStack foodStack;
    public final KciFood customFood;
    public final ItemSetWrapper itemSet;

    private boolean isCancelled = false;

    public CustomFoodEatEvent(
            Player who, ItemStack foodStack,
            KciFood customFood, ItemSetWrapper itemSet
    ) {
        super(who);
        this.foodStack = foodStack;
        this.customFood = customFood;
        this.itemSet = itemSet;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
