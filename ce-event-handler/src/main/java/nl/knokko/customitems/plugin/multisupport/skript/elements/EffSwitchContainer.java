package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Optional;

@SuppressWarnings("unused")
public class EffSwitchContainer extends Effect {

    static {
        Skript.registerEffect(EffSwitchContainer.class, "switch container of %player% to %string%");
    }

    private Expression<Player> player;
    private Expression<String> newContainerName;


    @Override
    protected void execute(Event event) {
        Optional<KciContainer> newContainer = CustomItemsPlugin.getInstance().getSet().get().containers.get(
                newContainerName.getSingle(event)
        );
        if (newContainer.isPresent()) {
            CustomItemsPlugin.getInstance().getData().containerManager.attemptToSwitchToLinkedContainer(
                    player.getSingle(event), newContainer.get()
            );
        } else {
            Skript.error("There is no custom container with name '" + newContainerName.getSingle(event) + "'");
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "Switch container of " + player.getSingle(event) + " to " + newContainerName.getSingle(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.player = (Expression<Player>) expressions[0];
        this.newContainerName = (Expression<String>) expressions[1];
        return true;
    }
}
