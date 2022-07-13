package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class EffDestroyContainer extends Effect {

    static {
        Skript.registerEffect(
                EffDestroyContainer.class,
                "destroy kci %string% at %string%",
                "destroy kci %string% at %string% and drop its items at %location%"
        );
    }

    private Expression<String> containerName;
    private Expression<String> stringHost;
    private Expression<Location> dropLocation;

    @Override
    protected void execute(Event event) {
        int numDestroyedContainers = CustomItemsApi.destroyCustomContainersAtStringHost(
                containerName.getSingle(event), stringHost.getSingle(event), dropLocation != null ? dropLocation.getSingle(event) : null
        );
        if (numDestroyedContainers == -1) {
            Bukkit.getLogger().warning("Tried to destroy instances of non-existing container '" + containerName.getSingle(event) + "'");
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Destroy container instances at a string host";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.containerName = (Expression<String>) expressions[0];
        this.stringHost = (Expression<String>) expressions[1];
        if (matchedPattern == 1) {
            this.dropLocation = (Expression<Location>) expressions[2];
        }
        return true;
    }
}
