package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class ExprContainerInput extends SimpleExpression<ItemStack> {

    static {
        Skript.registerExpression(
                ExprContainerInput.class, ItemStack.class, ExpressionType.PROPERTY,
                "input %string% of %kcicontainer%"
        );
    }

    private Expression<ContainerInstance> container;
    private Expression<String> inputSlotName;

    @Override
    protected ItemStack[] get(Event event) {
        ContainerInstance container = this.container.getSingle(event);
        String inputSlotName = this.inputSlotName.getSingle(event);
        if (container == null || inputSlotName == null) return new ItemStack[] { null };
        if (!container.hasInput(inputSlotName)) {
            Skript.error("There is no input slot with name '" + inputSlotName + "'");
            return new ItemStack[1];
        }
        return new ItemStack[] { container.getInput(inputSlotName) };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "get input of kci container";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.inputSlotName = (Expression<String>) expressions[0];
        this.container = (Expression<ContainerInstance>) expressions[1];
        return true;
    }
}
