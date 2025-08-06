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
public class EffContainerStorage extends Effect {

    static {
        Skript.registerEffect(
                EffContainerStorage.class, "set storage %integer% of %kcicontainer% to %itemstack%"
        );
    }

    private Expression<Integer> storageSlotIndex;
    private Expression<ContainerInstance> container;
    private Expression<ItemStack> newStack;

    @Override
    protected void execute(Event event) {
        ContainerInstance container = this.container.getSingle(event);
        int storageSlotIndex = this.storageSlotIndex.getSingle(event);
        if (storageSlotIndex < 0 || storageSlotIndex >= container.getNumStorageSlots()) {
            Skript.error("The storage slot index must be at least 0 and smaller than " + container.getNumStorageSlots());
            return;
        }
        container.setStorageItem(storageSlotIndex, newStack.getSingle(event));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set storage slot of kci container";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.storageSlotIndex = (Expression<Integer>) expressions[0];
        this.container = (Expression<ContainerInstance>) expressions[1];
        this.newStack = (Expression<ItemStack>) expressions[2];
        return true;
    }
}
