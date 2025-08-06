package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import nl.knokko.customitems.plugin.events.CustomFoodEatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class KciFoodEatEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // kci player eats
    //
    // @Cancellable true
    //
    // @Triggers when a player eats custom food
    //
    // @Plugin CustomItems
    //
    // -->

    KciFoodEatEvent() {
        registerCouldMatcher("kci player eats");
    }

    private CustomFoodEatEvent event;

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event != null ? new PlayerTag(event.getPlayer()) : null, null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("food_stack")) return new ItemTag(event.foodStack);
        return super.getContext(name);
    }

    @EventHandler
    public void onCustomFoodEat(CustomFoodEatEvent event) {
        this.event = event;
        fire(event);
    }
}
