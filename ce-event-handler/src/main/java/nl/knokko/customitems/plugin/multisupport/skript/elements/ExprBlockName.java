package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class ExprBlockName extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
                ExprBlockName.class, String.class, ExpressionType.PROPERTY,
                "kci name of %block%"
        );
    }

    private Expression<Block> block;

    @Override
    protected String[] get(Event event) {
        Block block = this.block.getSingle(event);
        if (block == null) return new String[] { null };
        return new String[] {CustomItemsApi.getBlockName(block) };
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
        return "Get name of KCI block";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.block = (Expression<Block>) expressions[0];
        return true;
    }
}
