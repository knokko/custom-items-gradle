package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.events.CustomContainerActionEvent;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class EvtContainerAction extends SkriptEvent {

    static {
        EventValues.registerEventValue(
                CustomContainerActionEvent.class, ContainerInstance.class, new Getter<ContainerInstance, CustomContainerActionEvent>() {
                    @Override
                    public ContainerInstance get(CustomContainerActionEvent event) {
                        return event.container;
                    }
                }, 0
        );
        EventValues.registerEventValue(
                CustomContainerActionEvent.class, Inventory.class, new Getter<Inventory, CustomContainerActionEvent>() {
                    @Override
                    public Inventory get(CustomContainerActionEvent event) {
                        return event.container.getInventory();
                    }
                }, 0
        );
        EventValues.registerEventValue(
                CustomContainerActionEvent.class, ItemStack.class, new Getter<ItemStack, CustomContainerActionEvent>() {
                    @Override
                    public ItemStack get(CustomContainerActionEvent event) {
                        return event.clickEvent.getCursor();
                    }
                }, 0
        );
        EventValues.registerEventValue(
                CustomContainerActionEvent.class, ClickType.class, new Getter<ClickType, CustomContainerActionEvent>() {
                    @Override
                    public ClickType get(CustomContainerActionEvent event) {
                        return event.clickEvent.getClick();
                    }
                }, 0
        );
        Skript.registerEvent(
                "Kci Container Action", EvtContainerAction.class, CustomContainerActionEvent.class,
                "kci container action %string%"
        );
    }

    private Literal<String> actionID;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        this.actionID = (Literal<String>) args[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (actionID != null) {
            String actualActionID = ((CustomContainerActionEvent) event).actionID;
            return actionID.check(event, desiredActionID -> desiredActionID.equals(actualActionID));
        }
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "KciContainerAction " + actionID.toString(e, debug);
    }
}
