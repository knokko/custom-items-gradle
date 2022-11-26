package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.command.CommandCustomItemsGive;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

@SuppressWarnings("unused")
public class EffGiveItemToInventory extends Effect {

    static {
        Skript.registerEffect(
                EffGiveItemToInventory.class,
                "give [a] kci %string% to %inventory%",
                "give %integer% kci %string% to %inventory%"
        );
    }

    private Expression<String> customItemName;
    private Expression<Integer> amount;
    private Expression<Inventory> receiver;

    @Override
    protected void execute(Event event) {
        int amount = this.amount != null ? Objects.requireNonNull(this.amount.getSingle(event)) : 1;
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
        CommandCustomItemsGive.giveCustomItemToInventory(
                itemSet, receiver.getSingle(event), itemSet.getItem(customItemName.getSingle(event)), amount
        );
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Give KCI item to an inventory";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0) {
            customItemName = (Expression<String>) expressions[0];
            receiver = (Expression<Inventory>) expressions[1];
        } else {
            amount = (Expression<Integer>) expressions[0];
            customItemName = (Expression<String>) expressions[1];
            receiver = (Expression<Inventory>) expressions[2];
        }
        return true;
    }
}
