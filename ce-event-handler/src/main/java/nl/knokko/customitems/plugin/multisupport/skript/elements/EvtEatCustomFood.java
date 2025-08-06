package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import nl.knokko.customitems.plugin.events.CustomFoodEatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class EvtEatCustomFood extends SkriptEvent {

    static {
        EventValues.registerEventValue(
                CustomFoodEatEvent.class, Player.class, new Getter<Player, CustomFoodEatEvent>() {
                    @Override
                    public Player get(CustomFoodEatEvent event) {
                        return event.getPlayer();
                    }
                }, 0
        );
        EventValues.registerEventValue(
                CustomFoodEatEvent.class, ItemStack.class, new Getter<ItemStack, CustomFoodEatEvent>() {
                    @Override
                    public ItemStack get(CustomFoodEatEvent event) {
                        return event.foodStack;
                    }
                }, 0
        );
        Skript.registerEvent(
                "Kci Eat Custom Food", EvtEatCustomFood.class, CustomFoodEatEvent.class,
                "kci player eats"
        );
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event e) {
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Kci Eat Custom Food";
    }
}
