package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class ExprItemCount extends SimpleExpression<Integer> {

    static {
        Skript.registerExpression(
                ExprItemCount.class, Integer.class, ExpressionType.PROPERTY,
                "count of kci %string% in %inventory%"
        );
    }

    private Expression<String> itemName;
    private Expression<Inventory> inventory;

    @Override
    protected Integer[] get(Event event) {
        String itemName = this.itemName.getSingle(event);
        ItemStack[] contents = inventory.getSingle(event).getContents();
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
        int amount = 0;

        for (ItemStack content : contents) {
            CustomItemValues customItem = itemSet.getItem(content);
            if (customItem != null && customItem.getName().equals(itemName)) {
                amount += content.getAmount();
            }
        }
        return new Integer[] { amount };
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
        return "count of kci item_name in inventory";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.itemName = (Expression<String>) expressions[0];
        this.inventory = (Expression<Inventory>) expressions[1];
        return true;
    }
}
