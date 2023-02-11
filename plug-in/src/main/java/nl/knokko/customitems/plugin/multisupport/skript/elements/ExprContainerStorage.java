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
public class ExprContainerStorage extends SimpleExpression<ItemStack> {

    static {
        Skript.registerExpression(
                ExprContainerStorage.class, ItemStack.class, ExpressionType.PROPERTY,
                "storage %integer% of %kcicontainer%"
        );
    }

    private Expression<ContainerInstance> container;
    private Expression<Integer> storageIndex;

    @Override
    protected ItemStack[] get(Event event) {
        ContainerInstance container = this.container.getSingle(event);
        Integer storageIndex = this.storageIndex.getSingle(event);
        if (container == null || storageIndex == null) return new ItemStack[] { null };
        if (storageIndex < 0 || storageIndex >= container.getNumStorageSlots()) {
            Skript.error("Storage index must be at least 0 and smaller than " + container.getNumStorageSlots());
            return new ItemStack[1];
        }
        return new ItemStack[] { container.getStorageItem(storageIndex) };
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
        return "get storage slot of kci container";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.storageIndex = (Expression<Integer>) expressions[0];
        this.container = (Expression<ContainerInstance>) expressions[1];
        return true;
    }
}
