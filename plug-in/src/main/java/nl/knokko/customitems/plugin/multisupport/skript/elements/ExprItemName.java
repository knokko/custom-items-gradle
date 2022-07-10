package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class ExprItemName extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
                ExprItemName.class, String.class, ExpressionType.PROPERTY,
                "kci name of %itemstack%"
        );
    }

    private Expression<ItemStack> itemStack;

    @Override
    protected String[] get(Event event) {
        ItemStack itemStack = this.itemStack.getSingle(event);
        if (itemStack == null) return new String[] { null };
        return new String[] { CustomItemsApi.getItemName(itemStack) };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Get KCI name of an item stack";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.itemStack = (Expression<ItemStack>) expressions[0];
        return true;
    }
}
