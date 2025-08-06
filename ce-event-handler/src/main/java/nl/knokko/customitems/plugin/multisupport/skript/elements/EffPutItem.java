package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.event.Event;

import java.util.Objects;

@SuppressWarnings("unused")
public class EffPutItem extends Effect {

    static {
        Skript.registerEffect(
                EffPutItem.class,
                "put [a] kci %string% in %slot%",
                "put %integer% kci %string% in %slot%"
        );
    }

    private Expression<String> customItemName;
    private Expression<Integer> amount;
    private Expression<Slot> destinationSlot;

    @Override
    protected void execute(Event event) {
        int amount = this.amount != null ? Objects.requireNonNull(this.amount.getSingle(event)) : 1;
        destinationSlot.getSingle(event).setItem(CustomItemsApi.createItemStack(customItemName.getSingle(event), amount));
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Put a KCI item in a slot";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0) {
            this.customItemName = (Expression<String>) expressions[0];
            this.amount = null;
            this.destinationSlot = (Expression<Slot>) expressions[1];
        } else {
            this.amount = (Expression<Integer>) expressions[0];
            this.customItemName = (Expression<String>) expressions[1];
            this.destinationSlot = (Expression<Slot>) expressions[2];
        }
        return true;
    }
}
