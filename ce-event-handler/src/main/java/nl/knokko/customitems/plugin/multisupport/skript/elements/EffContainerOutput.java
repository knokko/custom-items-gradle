package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class EffContainerOutput extends Effect {

    static {
        Skript.registerEffect(
                EffContainerOutput.class, "set output %string% of %kcicontainer% to %itemstack%"
        );
    }

    private Expression<String> outputSlotName;
    private Expression<ContainerInstance> container;
    private Expression<ItemStack> newStack;

    @Override
    protected void execute(Event event) {
        ContainerInstance container = this.container.getSingle(event);
        String outputSlotName = this.outputSlotName.getSingle(event);
        if (!container.hasOutput(outputSlotName)) {
            Skript.error("There is no output slot with name '" + outputSlotName + "'");
            return;
        }
        container.setOutput(outputSlotName, newStack.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set output of kci container";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.outputSlotName = (Expression<String>) expressions[0];
        this.container = (Expression<ContainerInstance>) expressions[1];
        this.newStack = (Expression<ItemStack>) expressions[2];
        return true;
    }
}
