package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class EffPlaceBlock extends Effect {

    static {
        Skript.registerEffect(
                EffPlaceBlock.class,
                "place kci %string% at %block%"
        );
    }

    private Expression<Block> destination;
    private Expression<String> customBlockName;

    @Override
    protected void execute(Event event) {
        Block destination = this.destination.getSingle(event);
        if (destination != null) {
            CustomItemsApi.placeBlock(destination, customBlockName.getSingle(event));
        }
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Place KCI block at a given destination";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.customBlockName = (Expression<String>) expressions[0];
        this.destination = (Expression<Block>) expressions[1];
        return true;
    }
}
