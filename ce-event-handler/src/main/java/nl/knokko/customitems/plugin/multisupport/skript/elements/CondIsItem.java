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
                "%itemstack% is [a] kci (%-string%|item)",
                "%itemstack% (is not|isn't) [a] kci (%-string%|item)"
        );
    }

    private Expression<ItemStack> itemStack;
    private Expression<String> customItemName;

    @Override
    public boolean check(Event event) {
        ItemStack candidateStack = itemStack.getSingle(event);
        if (candidateStack == null) return isNegated();

        String actualName = CustomItemsApi.getItemName(candidateStack);
        boolean isEqual;
        if (customItemName != null) {
            String desiredName = customItemName.getSingle(event);
            isEqual = Objects.equals(desiredName, actualName);
        } else {
            isEqual = actualName != null;
        }

        return isNegated() != isEqual;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Item stack is kci custom item";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.itemStack = (Expression<ItemStack>) expressions[0];
        this.customItemName = expressions.length > 1 ? (Expression<String>) expressions[1] : null;
        this.setNegated(matchedPattern == 1);
        return true;
    }
}
