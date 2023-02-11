package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;

public class CustomContainerActionEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public final String actionID;
    public final ContainerInstance container;
    public final InventoryClickEvent clickEvent;
    public final ItemSetWrapper itemSet;

    public CustomContainerActionEvent(
            String actionID, ContainerInstance container, InventoryClickEvent clickEvent, ItemSetWrapper itemSet
    ) {
        super((Player) clickEvent.getWhoClicked());
        this.actionID = actionID;
        this.container = container;
        this.clickEvent = clickEvent;
        this.itemSet = itemSet;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
