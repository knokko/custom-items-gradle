package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class EffOpenContainer extends Effect {

    static {
        Skript.registerEffect(
                EffOpenContainer.class,
                "%player% opens kci %string% at %string%"
        );
    }

    private Expression<Player> player;
    private Expression<String> containerName;
    private Expression<String> hostString;

    @Override
    protected void execute(Event event) {
        if (!CustomItemsApi.openContainerAtStringHost(player.getSingle(event), containerName.getSingle(event), hostString.getSingle(event))) {
            Skript.error("Attempted to open non-existing container '" + containerName.getSingle(event) + "' via Skript");
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Let player open KCI container at string host";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.player = (Expression<Player>) expressions[0];
        this.containerName = (Expression<String>) expressions[1];
        this.hostString = (Expression<String>) expressions[2];
        return true;
    }
}
