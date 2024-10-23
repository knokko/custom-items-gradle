package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.events.CustomContainerActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

class KciContainerActionEvent extends KciContainerEvent implements Listener {

    // <--[event]
    // @Events
    // kci container action (<'action'>)
    //
    // @Cancellable false
    //
    // @Triggers when a player clicks on a custom container action slot
    //
    // @Plugin CustomItems
    //
    // -->

    KciContainerActionEvent() {
        registerCouldMatcher("kci container action (<'action'>)");
    }

    private CustomContainerActionEvent event;

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event != null ? new PlayerTag(event.getPlayer()) : null, null);
    }

    @Override
    ContainerInstance getInstance() {
        return event.container;
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("cursor_item")) return new ItemTag(event.clickEvent.getCursor());
		if (name.equals("click_type")) return new ElementTag(event.clickEvent.getClick().name());
		if (name.equals("is_shift_click")) return new ElementTag(event.clickEvent.isShiftClick());
		return super.getContext(name);
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determinationObject) {
        if (determinationObject instanceof ElementTag) {
            String rawDetermination = determinationObject.toString();
            int indexColon = rawDetermination.indexOf(':');
            if (indexColon != -1) {
                String key = rawDetermination.substring(0, indexColon);
                ElementTag value = new ElementTag(rawDetermination.substring(indexColon + 1));

                if (key.equals("switch_container")) {
                    Optional<KciContainer> newContainer = event.itemSet.get().containers.get(value.asString());
                    if (newContainer.isPresent()) {
                        CustomItemsPlugin.getInstance().getData().containerManager.attemptToSwitchToLinkedContainer(
                                event.getPlayer(), newContainer.get()
                        );
                    } else {
                        Debug.echoError("There is no custom container with name '" + value.asString() + "'");
                    }
                    return true;
                }
            }
        }

        return super.applyDetermination(path, determinationObject);
    }

    @EventHandler
    public void onContainerAction(CustomContainerActionEvent event) {
        this.event = event;
        fire(event);
    }

    @Override
    public boolean matches(ScriptPath path) {
        String desiredActionID = path.eventArgLowerAt(3);
        return desiredActionID.equals(event.actionID);
    }
}
