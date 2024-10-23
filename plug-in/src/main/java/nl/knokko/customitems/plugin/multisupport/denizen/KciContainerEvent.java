package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.InventoryTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizencore.utilities.text.StringHolder;
import nl.knokko.customitems.plugin.container.ContainerInstance;

import java.util.*;

abstract class KciContainerEvent extends BukkitScriptEvent {

	abstract ContainerInstance getInstance();

	@Override
	public ObjectTag getContext(String name) {
		if (name.equals("container_name")) return new ElementTag(getInstance().getType().getName());
		if (name.equals("container_inventory")) return new InventoryTag(
				getInstance().getInventory(), null,
				new ElementTag("custom container: " + getInstance().getType().getName())
		);
		if (name.equals("inputs")) {
			Map<StringHolder, ObjectTag> inputs = new HashMap<>();
			getInstance().getCurrentIngredients().forEach((inputSlotName, inputStack) -> {
				inputs.put(new StringHolder(inputSlotName), new ItemTag(inputStack));
			});
			return new MapTag(inputs);
		}
		if (name.startsWith("inputs(") && name.endsWith(")")) {
			String inputSlotName = name.substring("inputs(".length(), name.length() - 1);
			if (!getInstance().hasInput(inputSlotName)) {
				Debug.echoError("No input slot has name '" + inputSlotName + "'");
				return null;
			}
			return new ItemTag(getInstance().getInput(inputSlotName));
		}
		if (name.equals("outputs")) {
			Map<StringHolder, ObjectTag> outputs = new HashMap<>();
			getInstance().getCurrentResults().forEach((outputSlotName, outputStack) -> {
				outputs.put(new StringHolder(outputSlotName), new ItemTag(outputStack));
			});
			return new MapTag(outputs);
		}
		if (name.startsWith("outputs(") && name.endsWith(")")) {
			String outputSlotName = name.substring("outputs(".length(), name.length() - 1);
			if (!getInstance().hasOutput(outputSlotName)) {
				Debug.echoError("No output slot has name '" + outputSlotName + "'");
				return null;
			}
			return new ItemTag(getInstance().getOutput(outputSlotName));
		}
		if (name.equals("fuel")) {
			Map<StringHolder, ObjectTag> fuel = new HashMap<>();
			getInstance().getCurrentFuel().forEach((fuelSlotName, fuelStack) -> {
				fuel.put(new StringHolder(fuelSlotName), new ItemTag(fuelStack));
			});
			return new MapTag(fuel);
		}
		if (name.startsWith("fuel(") && name.endsWith(")")) {
			String fuelSlotName = name.substring("fuel(".length(), name.length() - 1);
			if (!getInstance().hasFuel(fuelSlotName)) {
				Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
				return null;
			}
			return new ItemTag(getInstance().getFuel(fuelSlotName));
		}
		if (name.equals("storage")) {
			List<ObjectTag> storedItems = new ArrayList<>(getInstance().getNumStorageSlots());
			for (int index = 0; index < getInstance().getNumStorageSlots(); index++) {
				storedItems.add(new ItemTag(getInstance().getStorageItem(index)));
			}
			return new ListTag(storedItems);
		}
		if (name.startsWith("storage(") && name.endsWith(")")) {
			try {
				int index = Integer.parseInt(name.substring("storage(".length(), name.length() - 1));
				if (index < 0) Debug.echoError("index must not be negative");
				else if (index >= getInstance().getNumStorageSlots()) Debug.echoError("index must be smaller than " + getInstance().getNumStorageSlots());
				else return new ItemTag(getInstance().getStorageItem(index));
			} catch (NumberFormatException invalidIndex) {
				Debug.echoError("Invalid index: it must be an integer");
			}
			return null;
		}
		if (name.equals("fuel_burn_times")) {
			Map<StringHolder, ObjectTag> fuelBurnTimes = new HashMap<>();
			getInstance().getCurrentFuel().forEach((fuelSlotName, _fuelStack) -> {
				fuelBurnTimes.put(new StringHolder(fuelSlotName), new ElementTag(getInstance().getRemainingFuelBurnTime(fuelSlotName)));
			});
			return new MapTag(fuelBurnTimes);
		}
		if (name.startsWith("fuel_burn_time(") && name.endsWith(")")) {
			String fuelSlotName = name.substring("fuel_burn_time(".length(), name.length() - 1);
			if (!getInstance().hasFuel(fuelSlotName)) {
				Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
				return null;
			}
			return new ElementTag(getInstance().getRemainingFuelBurnTime(fuelSlotName));
		}
		if (name.equals("crafting_progress")) return new ElementTag(getInstance().getCurrentCraftingProgress());
		if (name.equals("stored_experience")) return new ElementTag(getInstance().getStoredExperience());
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
					if (!getInstance().hasInput(inputSlotName)) {
						Debug.echoError("No input slot has name '" + inputSlotName + "'");
						return true;
					}

					ItemTag newItem = value.asType(ItemTag.class, new BukkitTagContext(path.container));
					if (newItem != null) getInstance().setInput(inputSlotName, newItem.getItemStack());
					return true;
				}

				if (key.startsWith("outputs.")) {
					String outputSlotName = key.substring("outputs.".length());
					if (!getInstance().hasOutput(outputSlotName)) {
						Debug.echoError("No output slot has name '" + outputSlotName + "'");
						return true;
					}

					ItemTag newItem = value.asType(ItemTag.class, new BukkitTagContext(path.container));
					if (newItem != null) getInstance().setOutput(outputSlotName, newItem.getItemStack());
					return true;
				}

				if (key.startsWith("fuel.")) {
					String fuelSlotName = key.substring("fuel.".length());
					if (!getInstance().hasFuel(fuelSlotName)) {
						Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
						return true;
					}

					ItemTag newFuel = value.asType(ItemTag.class, new BukkitTagContext(path.container));
					if (newFuel != null) getInstance().setFuel(fuelSlotName, newFuel.getItemStack());
					return true;
				}

				if (key.startsWith("storage.")) {
					try {
						int index = Integer.parseInt(key.substring("storage.".length()));

						if (index < 0) Debug.echoError("index can't be negative");
						else if (index >= getInstance().getNumStorageSlots()) Debug.echoError("index must be smaller than " + getInstance().getNumStorageSlots());
						else {
							ItemTag newItem = value.asType(ItemTag.class, new BukkitTagContext(path.container));
							if (newItem != null) getInstance().setStorageItem(index, newItem.getItemStack());
						}

					} catch (NumberFormatException invalidIndex) {
						Debug.echoError("Invalid index: the index must be an integer");
					}
					return true;
				}

				if (key.startsWith("fuel_burn_time.")) {
					String fuelSlotName = key.substring("fuel_burn_time.".length());
					if (!getInstance().hasFuel(fuelSlotName)) {
						Debug.echoError("No fuel slot has name '" + fuelSlotName + "'");
						return true;
					}

					int newBurnTime = value.asInt();
					getInstance().setRemainingFuelBurnTime(fuelSlotName, newBurnTime);
					return true;
				}

				if (key.equals("stored_experience")) {
					getInstance().setStoredExperience(value.asInt());
					return true;
				}
			}
		}

		return super.applyDetermination(path, determinationObject);
	}
}
