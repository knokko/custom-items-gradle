package nl.knokko.customitems.plugin.multisupport.denizen;

import nl.knokko.customitems.plugin.events.CustomContainerRecipeStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KciContainerRecipeStartEvent extends KciContainerRecipeEvent implements Listener {
	// <--[event]
    // @Events
    // kci container recipe starts
    //
    // @Cancellable true
    //
    // @Triggers when a kci container recipe is about to start
    //
    // @Plugin CustomItems
    //
    // -->
    KciContainerRecipeStartEvent() {
        registerCouldMatcher("kci container recipe starts");
    }

    @EventHandler
    public void onContainerRecipeStart(CustomContainerRecipeStartEvent event) {
        this.event = event;
        fire(event);
    }
}
