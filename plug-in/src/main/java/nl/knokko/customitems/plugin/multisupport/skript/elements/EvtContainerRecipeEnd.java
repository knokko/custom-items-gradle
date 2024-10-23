package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.events.CustomContainerRecipeEndEvent;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unused")
public class EvtContainerRecipeEnd extends SkriptEvent {

	static {
		EventValues.registerEventValue(
				CustomContainerRecipeEndEvent.class, ContainerInstance.class, new Getter<ContainerInstance, CustomContainerRecipeEndEvent>() {
					@Override
					public ContainerInstance get(CustomContainerRecipeEndEvent event) {
						return event.container;
					}
				}, 0
		);
		EventValues.registerEventValue(
				CustomContainerRecipeEndEvent.class, ContainerRecipe.class, new Getter<ContainerRecipe, CustomContainerRecipeEndEvent>() {
					@Override
					public ContainerRecipe get(CustomContainerRecipeEndEvent event) {
						return event.recipe;
					}
				}, 0
		);
		EventValues.registerEventValue(
				CustomContainerRecipeEndEvent.class, Inventory.class, new Getter<Inventory, CustomContainerRecipeEndEvent>() {
					@Override
					public Inventory get(CustomContainerRecipeEndEvent event) {
						return event.container.getInventory();
					}
				}, 0
		);
		Skript.registerEvent(
				"Kci Container Recipe Ends", EvtContainerRecipeEnd.class, CustomContainerRecipeEndEvent.class,
				"kci container recipe ends"
		);
	}

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	public boolean check(Event event) {
		return true;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "KciContainerRecipeEnd";
	}
}
