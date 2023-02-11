package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class ExprContainerCraftingProgress extends SimpleExpression<Integer> {

    static {
        Skript.registerExpression(
                ExprContainerCraftingProgress.class, Integer.class, ExpressionType.PROPERTY,
                "crafting progress of %kcicontainer%"
        );
    }

    private Expression<ContainerInstance> container;

    @Override
    protected Integer[] get(Event event) {
        ContainerInstance container = this.container.getSingle(event);
        if (container == null) return new Integer[] { null };
        return new Integer[] { container.getCurrentCraftingProgress() };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "get crafting progress of kci container";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.container = (Expression<ContainerInstance>) expressions[0];
        return true;
    }
}
