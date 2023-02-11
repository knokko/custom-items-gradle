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
public class EffContainerInput extends Effect {

    static {
        Skript.registerEffect(
                EffContainerInput.class, "set input %string% of %kcicontainer% to %itemstack%"
        );
    }

    private Expression<String> inputSlotName;
    private Expression<ContainerInstance> container;
    private Expression<ItemStack> newStack;

    @Override
    protected void execute(Event event) {
        ContainerInstance container = this.container.getSingle(event);
        String inputSlotName = this.inputSlotName.getSingle(event);
        if (!container.hasInput(inputSlotName)) {
            Skript.error("There is no input slot with name '" + inputSlotName + "'");
            return;
        }
        container.setInput(inputSlotName, newStack.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set input of kci container";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.inputSlotName = (Expression<String>) expressions[0];
        this.container = (Expression<ContainerInstance>) expressions[1];
        this.newStack = (Expression<ItemStack>) expressions[2];
        return true;
    }
}
