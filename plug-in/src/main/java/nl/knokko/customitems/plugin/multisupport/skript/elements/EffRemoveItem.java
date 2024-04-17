package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class EffRemoveItem extends Effect {

    static {
        Skript.registerEffect(EffRemoveItem.class, "kci remove %integer% %string% from %inventory%");
    }

    private Expression<Integer> amount;
    private Expression<String> itemName;
    private Expression<Inventory> inventory;

    @Override
    protected void execute(Event event) {
        int itemIndex = 0;
        int remainingAmount = amount.getSingle(event);
        ItemStack[] contents = inventory.getSingle(event).getContents();
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
        String itemName = this.itemName.getSingle(event);

        while (remainingAmount > 0 && itemIndex < contents.length) {
            ItemStack item = contents[itemIndex];
            KciItem customItem = itemSet.getItem(item);
            if (customItem != null && customItem.getName().equals(itemName)) {
                if (remainingAmount >= item.getAmount()) {
                    remainingAmount -= item.getAmount();
                    contents[itemIndex] = null;
                } else {
                    item.setAmount(item.getAmount() - remainingAmount);
                    remainingAmount = 0;
                }
            }
            itemIndex++;
        }

        inventory.getSingle(event).setContents(contents);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "remove amount item_name from inventory";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.amount = (Expression<Integer>) expressions[0];
        this.itemName = (Expression<String>) expressions[1];
        this.inventory = (Expression<Inventory>) expressions[2];
        return true;
    }
}
