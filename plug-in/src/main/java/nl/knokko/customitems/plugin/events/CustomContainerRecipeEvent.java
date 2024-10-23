package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.event.Event;

public abstract class CustomContainerRecipeEvent extends Event {

	public final ContainerInstance container;
	public final ContainerRecipe recipe;
	public final ItemSetWrapper itemSet;

	public CustomContainerRecipeEvent(
			ContainerInstance container, ContainerRecipe recipe, ItemSetWrapper itemSet
	) {
		super();
		this.container = container;
		this.recipe = recipe;
		this.itemSet = itemSet;
	}
}
