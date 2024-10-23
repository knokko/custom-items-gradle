package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class CustomContainerRecipeEndEvent extends CustomContainerRecipeEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean cancelled;

	public CustomContainerRecipeEndEvent(ContainerInstance container, ContainerRecipe recipe, ItemSetWrapper itemSet) {
		super(container, recipe, itemSet);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
}
