package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.events.CustomContainerRecipeStartEvent;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("unused")
public class EvtContainerRecipeStart extends SkriptEvent {

	static {
		EventValues.registerEventValue(
				CustomContainerRecipeStartEvent.class, ContainerInstance.class, new Getter<ContainerInstance, CustomContainerRecipeStartEvent>() {
					@Override
					public ContainerInstance get(CustomContainerRecipeStartEvent event) {
						return event.container;
					}
				}, 0
		);
		EventValues.registerEventValue(
				CustomContainerRecipeStartEvent.class, ContainerRecipe.class, new Getter<ContainerRecipe, CustomContainerRecipeStartEvent>() {
					@Override
					public ContainerRecipe get(CustomContainerRecipeStartEvent event) {
						return event.recipe;
					}
				}, 0
		);
		EventValues.registerEventValue(
				CustomContainerRecipeStartEvent.class, Inventory.class, new Getter<Inventory, CustomContainerRecipeStartEvent>() {
					@Override
					public Inventory get(CustomContainerRecipeStartEvent event) {
						return event.container.getInventory();
					}
				}, 0
		);
		Skript.registerEvent(
				"Kci Container Recipe Start", EvtContainerRecipeStart.class, CustomContainerRecipeStartEvent.class,
				"kci container recipe starts"
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
        return "KciContainerRecipeStart";
    }
}
