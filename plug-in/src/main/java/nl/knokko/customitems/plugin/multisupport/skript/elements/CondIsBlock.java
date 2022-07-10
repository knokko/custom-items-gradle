package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

import java.util.Objects;

public class CondIsBlock extends Condition {

    static {
        Skript.registerCondition(
                CondIsBlock.class,
                "%block% is [a] kci %string%",
                "%block% (isn't|is not) [a] kci %string%"
        );
    }

    private Expression<Block> block;
    private Expression<String> customBlockName;

    @Override
    public boolean check(Event event) {
        Block candidateBlock = block.getSingle(event);
        if (candidateBlock == null) return isNegated();
        String desiredCustomBlockName = customBlockName.getSingle(event);

        String actualCustomBlockName = CustomItemsApi.getBlockName(candidateBlock);
        return isNegated() != Objects.equals(desiredCustomBlockName, actualCustomBlockName);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Block is KCI block";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.block = (Expression<Block>) exprs[0];
        this.customBlockName = (Expression<String>) exprs[1];
        this.setNegated(matchedPattern == 1);
        return true;
    }
}
