package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@SuppressWarnings("unused")
public class CondIsItem extends Condition {

    static {
        Skript.registerCondition(
                CondIsItem.class,
                "%itemstack% is [a] kci %string%",
                "%itemstack% (is not|isn't) [a] kci %string%"
        );
    }

    private Expression<ItemStack> itemStack;
    private Expression<String> customItemName;

    @Override
    public boolean check(Event event) {
        ItemStack candidateStack = itemStack.getSingle(event);
        if (candidateStack == null) return isNegated();
        String desiredName = customItemName.getSingle(event);

        String actualName = CustomItemsApi.getItemName(candidateStack);
        return isNegated() != Objects.equals(desiredName, actualName);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Item stack is kci custom item";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.itemStack = (Expression<ItemStack>) expressions[0];
        this.customItemName = (Expression<String>) expressions[1];
        this.setNegated(matchedPattern == 1);
        return true;
    }
}
