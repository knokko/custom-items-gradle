package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.InventoryTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.events.CustomContainerActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

class KciContainerActionEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // kci container action (<'action'>)
    //
    // @Cancellable false
    //
    // @Triggers when a player clicks on a custom container action slot
    //
    // @Plugin CustomItems
    //
    // -->

    KciContainerActionEvent() {
        registerCouldMatcher("kci container action (<'action'>)");
    }

    private CustomContainerActionEvent event;

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event != null ? new PlayerTag(event.getPlayer()) : null, null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("container_name")) return new ElementTag(event.container.getType().getName());
        if (name.equals("container_inventory")) return new InventoryTag(
                event.container.getInventory(), null,
                new ElementTag("custom container: " + event.container.getType().getName())
        );
        if (name.equals("inputs")) {
            Map<StringHolder, ObjectTag> inputs = new HashMap<>();
            event.container.getCurrentIngredients().forEach((inputSlotName, inputStack) -> {
                inputs.put(new StringHolder(inputSlotName), new ItemTag(inputStack));
            });
            return new MapTag(inputs);
        }
        if (name.startsWith("inputs(") && name.endsWith(")")) {
            String inputSlotName = name.substring("inputs(".length(), name.length() - 1);
            if (!event.container.hasInput(inputSlotName)) {
                Debug.echoError("No input slot has name '" + inputSlotName + "'");
                return null;
            }
            return new ItemTag(event.container.getInput(inputSlotName));
        }
        if (name.equals("outputs")) {
            Map<StringHolder, ObjectTag> outputs = new HashMap<>();
            event.container.getCurrentResults().forEach((outputSlotName, outputStack) -> {
                outputs.put(new StringHolder(outputSlotName), new ItemTag(outputStack));
            });
            return new MapTag(outputs);
        }
        if (name.startsWith("outputs(") && name.endsWith(")")) {
            String outputSlotName = name.substring("outputs(".length(), name.length() - 1);
            if (!event.container.hasOutput(outputSlotName)) {
                Debug.echoError("No output slot has name '" + outputSlotName + "'");
                return null;
            }
            return new ItemTag(event.container.getOutput(outputSlotName));
        }
        if (name.equals("fuel")) {
            Map<StringHolder, ObjectTag> fuel = new HashMap<>();
            event.container.getCurrentFuel().forEach((fuelSlotName, fuelStack) -> {
                fuel.put(new StringHolder(fuelSlotName), new ItemTag(fuelStack));
            });
            return new MapTag(fuel);
        }
        if (name.startsWith("fuel(") && name.endsWith(")")) {
            String fuelSlotName = name.substring("fuel(".length(), name.length() - 1);
            if (!event.container.hasFuel(fuelSlotName)) {
                Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
                return null;
            }
            return new ItemTag(event.container.getFuel(fuelSlotName));
        }
        if (name.equals("storage")) {
            List<ObjectTag> storedItems = new ArrayList<>(event.container.getNumStorageSlots());
            for (int index = 0; index < event.container.getNumStorageSlots(); index++) {
                storedItems.add(new ItemTag(event.container.getStorageItem(index)));
            }
            return new ListTag(storedItems);
        }
        if (name.startsWith("storage(") && name.endsWith(")")) {
            try {
                int index = Integer.parseInt(name.substring("storage(".length(), name.length() - 1));
                if (index < 0) Debug.echoError("index must not be negative");
                else if (index >= event.container.getNumStorageSlots()) Debug.echoError("index must be smaller than " + event.container.getNumStorageSlots());
                else return new ItemTag(event.container.getStorageItem(index));
            } catch (NumberFormatException invalidIndex) {
                Debug.echoError("Invalid index: it must be an integer");
            }
            return null;
        }
        if (name.equals("fuel_burn_times")) {
            Map<StringHolder, ObjectTag> fuelBurnTimes = new HashMap<>();
            event.container.getCurrentFuel().forEach((fuelSlotName, _fuelStack) -> {
                fuelBurnTimes.put(new StringHolder(fuelSlotName), new ElementTag(event.container.getRemainingFuelBurnTime(fuelSlotName)));
            });
            return new MapTag(fuelBurnTimes);
        }
        if (name.startsWith("fuel_burn_time(") && name.endsWith(")")) {
            String fuelSlotName = name.substring("fuel_burn_time(".length(), name.length() - 1);
            if (!event.container.hasFuel(fuelSlotName)) {
                Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
                return null;
            }
            return new ElementTag(event.container.getRemainingFuelBurnTime(fuelSlotName));
        }
        if (name.equals("crafting_progress")) return new ElementTag(event.container.getCurrentCraftingProgress());
        if (name.equals("stored_experience")) return new ElementTag(event.container.getStoredExperience());
        if (name.equals("cursor_item")) return new ItemTag(event.clickEvent.getCursor());
        if (name.equals("click_type")) return new ElementTag(event.clickEvent.getClick().name());
        if (name.equals("is_shift_click")) return new ElementTag(event.clickEvent.isShiftClick());
        return super.getContext(name);
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determinationObject) {
        if (determinationObject instanceof ElementTag) {
            String rawDetermination = determinationObject.toString();
            int indexColon = rawDetermination.indexOf(':');
            if (indexColon != -1) {
                String key = rawDetermination.substring(0, indexColon);
                ElementTag value = new ElementTag(rawDetermination.substring(indexColon + 1));

                if (key.startsWith("inputs.")) {
                    String inputSlotName = key.substring("inputs.".length());
                    if (!event.container.hasInput(inputSlotName)) {
                        Debug.echoError("No input slot has name '" + inputSlotName + "'");
                        return true;
                    }

                    ItemTag newItem = value.asType(ItemTag.class, new BukkitTagContext(path.container));
                    if (newItem != null) event.container.setInput(inputSlotName, newItem.getItemStack());
                    return true;
                }

                if (key.startsWith("outputs.")) {
                    String outputSlotName = key.substring("outputs.".length());
                    if (!event.container.hasOutput(outputSlotName)) {
                        Debug.echoError("No output slot has name '" + outputSlotName + "'");
                        return true;
                    }

                    ItemTag newItem = value.asType(ItemTag.class, new BukkitTagContext(path.container));
                    if (newItem != null) event.container.setOutput(outputSlotName, newItem.getItemStack());
                    return true;
                }

                if (key.startsWith("fuel.")) {
                    String fuelSlotName = key.substring("fuel.".length());
                    if (!event.container.hasFuel(fuelSlotName)) {
                        Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
                        return true;
                    }

                    ItemTag newFuel = value.asType(ItemTag.class, new BukkitTagContext(path.container));
                    if (newFuel != null) event.container.setFuel(fuelSlotName, newFuel.getItemStack());
                    return true;
                }

                if (key.startsWith("storage.")) {
                    try {
                        int index = Integer.parseInt(key.substring("storage.".length()));

                        if (index < 0) Debug.echoError("index can't be negative");
                        else if (index >= event.container.getNumStorageSlots()) Debug.echoError("index must be smaller than " + event.container.getNumStorageSlots());
                        else {
                            ItemTag newItem = value.asType(ItemTag.class, new BukkitTagContext(path.container));
                            if (newItem != null) event.container.setStorageItem(index, newItem.getItemStack());
                        }

                    } catch (NumberFormatException invalidIndex) {
                        Debug.echoError("Invalid index: the index must be an integer");
                    }
                    return true;
                }

                if (key.startsWith("fuel_burn_time.")) {
                    String fuelSlotName = key.substring("fuel_burn_time.".length());
                    if (!event.container.hasFuel(fuelSlotName)) {
                        Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
                        return true;
                    }

                    int newBurnTime = value.asInt();
                    event.container.setRemainingFuelBurnTime(fuelSlotName, newBurnTime);
                    return true;
                }

                if (key.equals("stored_experience")) {
                    event.container.setStoredExperience(value.asInt());
                    return true;
                }

                if (key.equals("switch_container")) {
                    Optional<KciContainer> newContainer = event.itemSet.get().containers.get(value.asString());
                    if (newContainer.isPresent()) {
                        CustomItemsPlugin.getInstance().getData().containerManager.attemptToSwitchToLinkedContainer(
                                event.getPlayer(), newContainer.get()
                        );
                    } else {
                        Debug.echoError("There is no custom container with name '" + value.asString() + "'");
                    }
                    return true;
                }
            }
        }

        return super.applyDetermination(path, determinationObject);
    }

    @EventHandler
    public void onContainerAction(CustomContainerActionEvent event) {
        this.event = event;
        fire(event);
    }

    @Override
    public boolean matches(ScriptPath path) {
        String desiredActionID = path.eventArgLowerAt(3);
        return desiredActionID.equals(event.actionID);
    }
}
