package nl.knokko.customitems.plugin.multisupport.denizen;

import nl.knokko.customitems.plugin.events.CustomContainerRecipeEndEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KciContainerRecipeEndEvent extends KciContainerRecipeEvent implements Listener {
	// <--[event]
    // @Events
    // kci container recipe ends
    //
    // @Cancellable true
    //
    // @Triggers when a kci container recipe is about to finish
    //
    // @Plugin CustomItems
    //
    // -->
    KciContainerRecipeEndEvent() {
        registerCouldMatcher("kci container recipe ends");
    }

    @EventHandler
    public void onContainerRecipeStart(CustomContainerRecipeEndEvent event) {
        this.event = event;
        fire(event);
    }
}
