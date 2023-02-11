package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class EffContainerStoredExperience extends Effect {

    static {
        Skript.registerEffect(
                EffContainerStoredExperience.class,
                "set stored experience of %kcicontainer% to %integer%"
        );
    }

    private Expression<ContainerInstance> container;
    private Expression<Integer> newAmount;

    @Override
    protected void execute(Event event) {
        container.getSingle(event).setStoredExperience(newAmount.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set stored experience of kci container " + container.getSingle(event).getType().getName()
                + " to " + newAmount.getSingle(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.container = (Expression<ContainerInstance>) expressions[0];
        this.newAmount = (Expression<Integer>) expressions[1];
        return true;
    }
}
