package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.Location;
import org.bukkit.event.Event;

import java.util.Objects;

@SuppressWarnings("unused")
public class EffDropItem extends Effect {

    static {
        Skript.registerEffect(
                EffDropItem.class,
                "drop [a] kci %string% at %location%",
                "drop %integer% kci %string% at %location%"
        );
    }

    private Expression<Integer> amount;
    private Expression<String> itemName;
    private Expression<Location> location;

    @Override
    protected void execute(Event event) {
        int amount = this.amount != null ? Objects.requireNonNull(this.amount.getSingle(event)) : 1;
        Location location = this.location.getSingle(event);
        location.getWorld().dropItemNaturally(location, CustomItemsApi.createItemStack(itemName.getSingle(event), amount));
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Drop a kci item at a given location";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0) {
            amount = null;
            itemName = (Expression<String>) expressions[0];
            location = (Expression<Location>) expressions[1];
        } else {
            amount = (Expression<Integer>) expressions[0];
            itemName = (Expression<String>) expressions[1];
            location = (Expression<Location>) expressions[2];
        }
        return true;
    }
}
