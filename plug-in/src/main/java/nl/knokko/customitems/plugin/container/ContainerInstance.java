package nl.knokko.customitems.plugin.container;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.StorageCustomSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.DataVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.container.ContainerInfo.DecorationProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.FuelProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.IndicatorProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.InputProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.OutputProps;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

/**
 * An in-game instance of a custom container. While the CustomContainer class defines
 * which types of slots and recipes a custom container will have, a ContainerInstance
 * defines which items are currently present in those slots and optionally which
 * (smelting) recipes are currently in progress.
 */
public class ContainerInstance {
	
	public static final String[] PLACEHOLDER_KEY = {"KnokkosItemFlags", "IsSlotDisplay"};
	
	@SuppressWarnings("deprecation")
	public static ItemStack fromDisplay(SlotDisplay display) {
		ItemStack stack;
		boolean isCustom = display.getItem() instanceof CustomItemDisplayItem;
		if (isCustom) {
			CustomItem customItem = ((CustomItem)((CustomItemDisplayItem) display.getItem()).getItem());
			stack = customItem.create(display.getAmount());
		} else {
			CIMaterial material;
			if (display.getItem() instanceof DataVanillaDisplayItem) {
				material = ((DataVanillaDisplayItem) display.getItem()).getMaterial();
			} else if (display.getItem() instanceof SimpleVanillaDisplayItem) {
				material = ((SimpleVanillaDisplayItem) display.getItem()).getMaterial();
			} else {
				throw new Error("Unknown display type: " + display);
			}
			stack = ItemHelper.createStack(material.name(), display.getAmount());
			if (display.getItem() instanceof DataVanillaDisplayItem) {
				MaterialData data = stack.getData();
				data.setData(((DataVanillaDisplayItem) display.getItem()).getData());
				stack.setData(data);
				stack.setDurability(data.getData());
			}
		}
		
		ItemMeta meta = stack.getItemMeta();
		
		// If a custom item is used, only overwrite display name and lore if its
		// specifically specified
		if (!isCustom || !display.getDisplayName().isEmpty())
			meta.setDisplayName(display.getDisplayName());
		if (!isCustom || display.getLore().length > 0)
			meta.setLore(Lists.newArrayList(display.getLore()));
		
		// Store changes in item meta
		stack.setItemMeta(meta);
		
		GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(stack);
		nbt.set(PLACEHOLDER_KEY, 1);
		return nbt.backToBukkit();
	}
	
	private static Inventory createInventory(ContainerInfo typeInfo) {
		CustomContainer container = typeInfo.getContainer();
		Inventory inv = Bukkit.createInventory(null, 9 * container.getHeight(), 
				container.getSelectionIcon().getDisplayName());
		
		for (DecorationProps decoration : typeInfo.getDecorations()) {
			ItemStack stack = fromDisplay(decoration.getSlotDisplay());
			inv.setItem(decoration.getInventoryIndex(), stack);
		}
		for (IndicatorProps indicator : typeInfo.getCraftingIndicators()) {
			inv.setItem(indicator.getInventoryIndex(), fromDisplay(indicator.getPlaceholder()));
		}
		typeInfo.getFuelSlots().forEach(entry -> {
			FuelProps props = entry.getValue();
			for (IndicatorProps indicator : props.getIndicators()) {
				inv.setItem(indicator.getInventoryIndex(), fromDisplay(indicator.getPlaceholder()));
			}
			if (props.getPlaceholder() != null) {
				inv.setItem(props.getSlotIndex(), fromDisplay(props.getPlaceholder()));
			}
		});
		
		return inv;
	}

	private static ItemStack loadStack(BitInput input) {
		String itemStackString = input.readString();

		YamlConfiguration dummyConfig = new YamlConfiguration();
		try {
			dummyConfig.loadFromString(itemStackString);
		} catch (InvalidConfigurationException e) {
			throw new IllegalArgumentException("Bad item stack string: " + itemStackString);
		}
		return dummyConfig.getItemStack("theStack");
	}

	private static void saveStack(BitOutput output, ItemStack stack) {
		YamlConfiguration dummyConfiguration = new YamlConfiguration();
		dummyConfiguration.set("theStack", stack);
		output.addString(dummyConfiguration.saveToString());
	}
	
	public static void discard1(BitInput input) {
		
		// TODO Perhaps drop the items rather than discarding them
		// Discard the item stacks for all 3 slot types: input, output and fuel
		for (int slotTypeCounter = 0; slotTypeCounter < 3; slotTypeCounter++) {
			
			// Discard all their slots
			int numSlots = input.readInt();
			for (int slotCounter = 0; slotCounter < numSlots; slotCounter++) {
				// Discard 2 strings: the name of the slot and the string 
				// representation of the ItemStack it contained
				input.readString();
				input.readString();
			}
		}
		
		// Discard the remaining fuel burn times
		int numBurningFuelSlots = input.readInt();
		for (int counter = 0; counter < numBurningFuelSlots; counter++) {
			
			// Discard slot name (String), remaining burn time and max burn time (int)
			input.readString();
			input.readInt();
			input.readInt();
		}
		
		// Discard remaining crafting progress
		input.readInt();
		// Discard stored experience
		input.readInt();
	}

	public static void discard2(BitInput input) {
		discard1(input);

		int numStoredItems = input.readInt();
		for (int counter = 0; counter < numStoredItems; counter++) {
			input.readByte();
			input.readByte();
			input.readString();
		}
	}
	
	public static ContainerInstance load1(BitInput input, ContainerInfo typeInfo) {
		
		ContainerInstance instance = new ContainerInstance(typeInfo);
		Inventory inv = instance.inventory;
		
		class StringStack {
			
			final String slotName;
			final ItemStack stack;
			
			StringStack(String slotName, ItemStack stack) {
				this.slotName = slotName;
				this.stack = stack;
			}
			
			private boolean putSimple(Function<String, Integer> getSlot, Inventory dest) {
				Integer rightSlot = getSlot.apply(slotName);
				if (rightSlot != null) {
					dest.setItem(rightSlot, stack);
					return true;
				} else {
					return false;
				}
			}
			
			private void putInEmptySlot(
					Iterable<Integer> firstSlots, Iterable<Integer> secondSlots,
					Iterable<Integer> thirdSlots) {
				
				// Use the preferred/first slots whenever possible
				for (Integer slotIndex : firstSlots) {
					if (ItemUtils.isEmpty(inv.getItem(slotIndex))) {
						inv.setItem(slotIndex, stack);
						return;
					}
				}
				
				// Fall back to the second choice type of slots
				for (int fuelSlot : secondSlots) {
					if (ItemUtils.isEmpty(inv.getItem(fuelSlot))) {
						inv.setItem(fuelSlot, stack);
						return;
					}
				}
				
				// The third choice type slots are the last resort
				for (Integer slotIndex : thirdSlots) {
					if (ItemUtils.isEmpty(inv.getItem(slotIndex))) {
						inv.setItem(slotIndex, stack);
						return;
					}
				}
				
				// If this line is reached, this item will be discarded
				// I don't know a better way to resolve this
			}
		}
		
		class FuelEntry {
			
			final String slotName;
			final int remainingBurnTime;
			final int fullBurnTime;
			
			FuelEntry(String slotName, int remainingBurnTime, int fullBurnTime) {
				this.slotName = slotName;
				this.remainingBurnTime = remainingBurnTime;
				this.fullBurnTime = fullBurnTime;
			}
		}
		
		class SlotReader {
			
			Collection<StringStack> readSlots(BitInput input) {
				
				int numNonEmptySlots = input.readInt();
				Collection<StringStack> slotStacks = new ArrayList<>();
				for (int counter = 0; counter < numNonEmptySlots; counter++) {
					String slotName = input.readString();
					// TODO Test that this still works
					ItemStack slotStack = loadStack(input);
					
					slotStacks.add(new StringStack(slotName, slotStack));
				}
				
				return slotStacks;
			}
		}
		
		SlotReader sr = new SlotReader();
		Collection<StringStack> inputStacks = sr.readSlots(input);
		Collection<StringStack> outputStacks = sr.readSlots(input);
		Collection<StringStack> fuelStacks = sr.readSlots(input);
		
		int numBurningFuelSlots = input.readInt();
		Collection<FuelEntry> fuelBurnDurations = new ArrayList<>(numBurningFuelSlots);
		for (int counter = 0; counter < numBurningFuelSlots; counter++) {
			
			String fuelSlotName = input.readString();
			int remainingBurnTime = input.readInt();
			int fullBurnTime = input.readInt();
			fuelBurnDurations.add(new FuelEntry(fuelSlotName, remainingBurnTime, fullBurnTime));
		}
		
		int craftingProgress = input.readInt();
		
		int storedExperience = input.readInt();
		
		/* Now that we have gathered all information, it's time to distribute the
		 * loaded item stacks over the container slots. This process is complex
		 * because the container might not have the exact same configuration as the
		 * moment when it was saved: the server owner might have changed it
		 * (via the editor) in the meantime.
		 */
		
		// But first the simple cases
		inputStacks.removeIf(inputStack -> inputStack.putSimple(
				inputSlotName -> typeInfo.getInputSlot(inputSlotName).getSlotIndex(), 
		inv));
		outputStacks.removeIf(outputStack -> outputStack.putSimple(
				outputSlotName -> typeInfo.getOutputSlot(outputSlotName).getSlotIndex(), 
		inv));
		fuelStacks.removeIf(fuelStack -> fuelStack.putSimple(
				fuelSlotName -> typeInfo.getFuelSlot(fuelSlotName).getSlotIndex(), 
		inv));
		
		// Now the annoying cases where the slot is changed or renamed
		for (StringStack inputStack : inputStacks) {
			inputStack.putInEmptySlot(
					takeValues(typeInfo.getInputSlots(), InputProps::getSlotIndex),
					takeValues(typeInfo.getOutputSlots(), OutputProps::getSlotIndex),
					takeValues(typeInfo.getFuelSlots(), FuelProps::getSlotIndex)
			);
		}
		
		for (StringStack outputStack : outputStacks) {
			outputStack.putInEmptySlot(
					takeValues(typeInfo.getOutputSlots(), OutputProps::getSlotIndex),
					takeValues(typeInfo.getFuelSlots(), FuelProps::getSlotIndex),
					takeValues(typeInfo.getInputSlots(), InputProps::getSlotIndex)
			);
		}
		
		for (StringStack fuelStack : fuelStacks) {
			fuelStack.putInEmptySlot(
					takeValues(typeInfo.getFuelSlots(), FuelProps::getSlotIndex),
					takeValues(typeInfo.getOutputSlots(), OutputProps::getSlotIndex),
					takeValues(typeInfo.getInputSlots(), InputProps::getSlotIndex)
			);
		}
		
		/*
		 * If fuel slots are renamed, the fuel burn time is hard to load as well.
		 * The trick of finding another fuel slot could be used here, but this can
		 * cause the situation where the burn time of one fuel slot would be given
		 * to another fuel slot. If that other fuel slot is very hard to get burning,
		 * this could give a very powerful advantage to a player.
		 * 
		 * I think simply discarding the burn time of renamed fuel slots is the best
		 * option here.
		 */
		for (FuelEntry fuel : fuelBurnDurations) {
			FuelBurnEntry entry = instance.fuelSlots.get(fuel.slotName);
			if (entry != null) {
				entry.remainingBurnTime = fuel.remainingBurnTime;
				entry.maxBurnTime = fuel.fullBurnTime;
			} else {
				Bukkit.getLogger().warning("Discarding burn duration for fuel slot "+ fuel.slotName + " for container " + instance.typeInfo.getContainer().getName());
			}
		}
		
		// This one is simple
		instance.currentCraftingProgress = craftingProgress;
		instance.storedExperience = storedExperience;
		
		return instance;
	}

	public static ContainerInstance load2(BitInput input, ContainerInfo typeInfo) {
		ContainerInstance base = load1(input, typeInfo);

		Collection<ItemStack> orphanStacks = new ArrayList<>(0);
		int numStoredStacks = input.readInt();
		for (int counter = 0; counter < numStoredStacks; counter++) {

			int x = input.readByte();
			int y = input.readByte();
			int invIndex = x + 9 * y;
			ItemStack storedStack = loadStack(input);

			// This can happen if the admin decreased the container height
			if (y >= typeInfo.getContainer().getHeight()) {
				orphanStacks.add(storedStack);
				continue;
			}

			CustomSlot slot = typeInfo.getContainer().getSlot(x, y);
			if (slot instanceof StorageCustomSlot) {
			    base.inventory.setItem(invIndex, storedStack);
			} else {
				// This can happen if the admin (re)moved the storage slot
				orphanStacks.add(storedStack);
			}
		}

		// If there are 'orphan' stacks (due to changes in the container layout between world saves), we should
		// try to put them in free storage slots or in input/fuel slots. Preferably storage slots
		if (!orphanStacks.isEmpty()) {
			// TODO Test this mechanism
			List<Integer> freeSlots = new ArrayList<>(40);

			// Collect the indices of the free storage slots first
			for (int y = 0; y < typeInfo.getContainer().getHeight(); y++) {
				for (int x = 0; x < 9; x++) {
					int invIndex = x + 9 * y;
					CustomSlot slot = typeInfo.getContainer().getSlot(x, y);
					if (slot instanceof StorageCustomSlot) {
						ItemStack currentStack = base.inventory.getItem(invIndex);
						if (ItemUtils.isEmpty(currentStack)) {
							freeSlots.add(invIndex);
						}
					}
				}
			}

			// Collect the indices of the free input slots
            typeInfo.getInputSlots().forEach(entry -> {
            	int invIndex = entry.getValue().getSlotIndex();
            	if (ItemUtils.isEmpty(base.inventory.getItem(invIndex))) {
            		freeSlots.add(invIndex);
				}
			});

			// Collect the indices of the free output slots
			typeInfo.getOutputSlots().forEach(entry -> {
				int invIndex = entry.getValue().getSlotIndex();
				if (ItemUtils.isEmpty(base.inventory.getItem(invIndex))) {
					freeSlots.add(invIndex);
				}
			});

			// Collect the indices of the free fuel slots
			typeInfo.getFuelSlots().forEach(entry -> {
				int invIndex = entry.getValue().getSlotIndex();
				if (ItemUtils.isEmpty(base.inventory.getItem(invIndex))) {
					freeSlots.add(invIndex);
				}
			});

			int slotListIndex = 0;
			for (ItemStack orphan : orphanStacks) {
				if (slotListIndex >= freeSlots.size()) {
					// If this happens, the remaining orphan items will be discarded because there are no free
					// slots left to place the items.
					break;
				}
				base.inventory.setItem(freeSlots.get(slotListIndex), orphan);
				slotListIndex++;
			}
		}

		// TODO Set the remaining free storage slots to their placeholder
		return base;
	}
	
	private static <T, U> Iterable<Integer> takeValues(Iterable<Entry<T, U>> entries, Function<U, Integer> getIndex) {
		return StreamSupport.stream(entries.spliterator(), false).map(entry -> getIndex.apply(entry.getValue()))::iterator;
	}
	
	private static class SimpleEntry<K, V> implements Entry<K, V> {
		
		final K key;
		final V value;
		
		SimpleEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}
	}
	
	private static <T, U> Iterable<Entry<T, Integer>> mapEntries(Iterable<Entry<T, U>> entries, Function<U, Integer> getIndex) {
		return StreamSupport.stream(entries.spliterator(), false).map(entry -> {
			Entry<T, Integer> resultEntry = new SimpleEntry<T, Integer>(entry.getKey(), getIndex.apply(entry.getValue()));
			return resultEntry;
		}).collect(Collectors.toList());
	}

	private final ContainerInfo typeInfo;
	
	private final Inventory inventory;
	
	// Will stay empty if there are no fuel slots
	private final Map<String, FuelBurnEntry> fuelSlots;
	
	// Will be ignored if the crafting for this container type is instant
	private int currentCraftingProgress;
	
	private ContainerRecipe currentRecipe;
	
	private int storedExperience;
	
	public ContainerInstance(ContainerInfo typeInfo) {
		if (typeInfo == null) throw new NullPointerException("typeInfo");
		this.typeInfo = typeInfo;
		this.inventory = createInventory(typeInfo);
		
		this.fuelSlots = new HashMap<>();
		this.initFuelSlots();
		
		this.currentCraftingProgress = 0;
	}
	
	public int getStoredExperience() {
		return storedExperience;
	}
	
	public void clearStoredExperience() {
		storedExperience = 0;
	}
	
	private void initFuelSlots() {
		for (Entry<String, FuelProps> fuelSlot : typeInfo.getFuelSlots()) {
			fuelSlots.put(fuelSlot.getKey(), new FuelBurnEntry());
		}
	}
	
	public void save1(BitOutput output) {
		
		// Since the container layout of type could change, we need to be careful
		// We will store entries of (slotName, itemStack) to store all items
		BiConsumer<String, Integer> stackSaver = (slotName, invIndex) -> {
			output.addString(slotName);
			ItemStack itemStack = inventory.getItem(invIndex);
			
			// Serializing item stacks is a little effort
			// TODO Test that I didn't break this
			saveStack(output, itemStack);
		};
		
		Consumer<Iterable<Entry<String, Integer>>> slotsSaver = collection -> {
			
			int numNonEmptySlots = 0;
			for (Entry<String, Integer> entry : collection) {
				ItemStack stack = inventory.getItem(entry.getValue());
				if (!ItemUtils.isEmpty(stack)) {
					numNonEmptySlots++;
				}
			}
			
			output.addInt(numNonEmptySlots);
			for (Entry<String, Integer> entry : collection) {
				ItemStack stack = inventory.getItem(entry.getValue());
				if (!ItemUtils.isEmpty(stack)) {
					stackSaver.accept(entry.getKey(), entry.getValue());
				}
			}
		};
		
		slotsSaver.accept(mapEntries(
				typeInfo.getInputSlots(), InputProps::getSlotIndex
		));
		slotsSaver.accept(mapEntries(
				typeInfo.getOutputSlots(), OutputProps::getSlotIndex
		));
		slotsSaver.accept(mapEntries(
				typeInfo.getFuelSlots(), FuelProps::getSlotIndex
		));
		
		int numBurningFuelSlots = 0;
		for (FuelBurnEntry burn : fuelSlots.values()) {
			if (burn.remainingBurnTime != 0) {
				numBurningFuelSlots++;
			}
		}
		
		// The fuel slots have a bit more data to store
		output.addInt(numBurningFuelSlots);
		fuelSlots.forEach((slotName, fuel) -> {
			if (fuel.remainingBurnTime != 0) {
				output.addString(slotName);
				output.addInt(fuel.remainingBurnTime);
				output.addInt(fuel.maxBurnTime);
			}
		});
		
		// Finally, we need to store the progress within the current crafting recipe
		output.addInt(currentCraftingProgress);
		
		// And the experience that is currently stored
		output.addInt(storedExperience);
		
		// We don't need to store for which recipe that progress is, because it can
		// be derived from the contents of the inventory slots
	}

	public void save2(BitOutput output) {
		save1(output);

		// TODO Save the non-empty storage slots
		class StackEntry {

			final byte x;
			final byte y;
			final ItemStack stack;

			StackEntry(int x, int y, ItemStack stack) {
				this.x = (byte) x;
				this.y = (byte) y;
				this.stack = stack;
			}
		}

		Collection<StackEntry> stacksToSave = new ArrayList<>();
		for (int y = 0; y < typeInfo.getContainer().getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				CustomSlot slot = typeInfo.getContainer().getSlot(x, y);
				if (slot instanceof StorageCustomSlot) {
					int invIndex = x + 9 * y;
					if (!ItemUtils.isEmpty(inventory.getItem(invIndex))) {
						stacksToSave.add(new StackEntry(x, y, inventory.getItem(invIndex)));
					}
				}
			}
		}

		output.addInt(stacksToSave.size());
		stacksToSave.forEach(entry -> {
			output.addByte(entry.x);
			output.addByte(entry.y);
			saveStack(output, entry.stack);
		});
	}
	
	public CustomContainer getType() {
		return typeInfo.getContainer();
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public void dropAllItems(Location location) {
		dropAllItems(location, typeInfo.getInputSlots(), InputProps::getSlotIndex);
		dropAllItems(location, typeInfo.getOutputSlots(), OutputProps::getSlotIndex);
		dropAllItems(location, typeInfo.getFuelSlots(), FuelProps::getSlotIndex);
	}
	
	private <T> void dropAllItems(Location location, Iterable<Entry<String, T>> slots, Function<T, Integer> getIndex) {
		slots.forEach(entry -> {
			int index = getIndex.apply(entry.getValue());
			ItemStack stack = inventory.getItem(index);
			if (!ItemUtils.isEmpty(stack)) {
				location.getWorld().dropItem(location, stack);
				inventory.setItem(index, null);
			}
		});
	}
	
	/**
	 * @return true if at least 1 of the fuel slots is burning
	 */
	public boolean isAnySlotBurning() {
		for (FuelBurnEntry entry : fuelSlots.values()) {
			if (entry.remainingBurnTime != 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public ItemStack getInput(String inputSlotName) {
		ItemStack potentialInput = inventory.getItem(
				typeInfo.getInputSlot(inputSlotName).getSlotIndex()
		);
		if (!ItemUtils.isEmpty(potentialInput)) {
			return potentialInput;
		} else {
			return null;
		}
	}
	
	public ItemStack getOutput(String outputSlotName) {
		ItemStack potentialOutput = inventory.getItem(
				typeInfo.getOutputSlot(outputSlotName).getSlotIndex()
		);
		if (!ItemUtils.isEmpty(potentialOutput)) {
			return potentialOutput;
		} else {
			return null;
		}
	}
	
	public ItemStack getFuel(String fuelSlotName) {
		ItemStack potentialFuel = inventory.getItem(
				typeInfo.getFuelSlot(fuelSlotName).getSlotIndex()
		);
		if (!ItemUtils.isEmpty(potentialFuel)) {
			return potentialFuel;
		} else {
			return null;
		}
	}
	
	public void setInput(String inputSlotName, ItemStack newStack) {
		inventory.setItem(typeInfo.getInputSlot(inputSlotName).getSlotIndex(), newStack);
	}
	
	public void setOutput(String outputSlotName, ItemStack newStack) {
		inventory.setItem(typeInfo.getOutputSlot(outputSlotName).getSlotIndex(), newStack);
	}
	
	public void setFuel(String fuelSlotName, ItemStack newStack) {
		inventory.setItem(typeInfo.getFuelSlot(fuelSlotName).getSlotIndex(), newStack);
	}
	
	public int getCurrentCraftingProgress() {
		return currentCraftingProgress;
	}
	
	private void updatePlaceholders() {
		typeInfo.getFuelSlots().forEach(entry -> {
			FuelProps props = entry.getValue();
			if (props.getPlaceholder() != null) {
				ItemStack currentItem = inventory.getItem(props.getSlotIndex());
				
				// DONT use ItemUtils.isEmpty because that considers placeholders
				// as empty items
				if (
						currentItem == null 
						|| currentItem.getAmount() <= 0
						|| ItemHelper.getMaterialName(currentItem)
						.equals(CIMaterial.AIR.name())
				) {
					inventory.setItem(
							props.getSlotIndex(), 
							fromDisplay(props.getPlaceholder())
					);
				}
			}
		});
		
		typeInfo.getInputSlots().forEach(entry -> {
			InputProps props = entry.getValue();
			if (props.getPlaceholder() != null) {
				ItemStack currentItem = inventory.getItem(props.getSlotIndex());
				
				// DONT use ItemUtils.isEmpty because that considers placeholders
				// as empty items
				if (
						currentItem == null 
						|| currentItem.getAmount() <= 0
						|| ItemHelper.getMaterialName(currentItem)
						.equals(CIMaterial.AIR.name())
				) {
					inventory.setItem(
							props.getSlotIndex(), 
							fromDisplay(props.getPlaceholder())
					);
				}
			}
		});
		
		typeInfo.getOutputSlots().forEach(entry -> {
			OutputProps props = entry.getValue();
			if (props.getPlaceholder() != null) {
				ItemStack currentItem = inventory.getItem(props.getSlotIndex());
				
				// DONT use ItemUtils.isEmpty because that considers placeholders
				// as empty items
				if (
						currentItem == null 
						|| currentItem.getAmount() <= 0
						|| ItemHelper.getMaterialName(currentItem)
						.equals(CIMaterial.AIR.name())
				) {
					inventory.setItem(
							props.getSlotIndex(), 
							fromDisplay(props.getPlaceholder())
					);
				}
			}
		});
	}
	
	public void update() {
		ContainerRecipe oldRecipe = currentRecipe;
		currentRecipe = null;
		
		updatePlaceholders();

		candidateLoop:
		for (ContainerRecipe candidate : typeInfo.getContainer().getRecipes()) {
			
			// Check that all inputs are present
			for (InputEntry input : candidate.getInputs()) {
				ItemStack inSlot = getInput(input.getInputSlotName());
				Ingredient ingredient = (Ingredient) input.getIngredient();
				if (!ingredient.accept(inSlot)) {
					continue candidateLoop;
				}
			}
			
			// Check that all other inputs are empty
			inputLoop:
			for (Entry<String, InputProps> inputEntry : typeInfo.getInputSlots()) {
				for (InputEntry usedInput : candidate.getInputs()) {
					
					// If this input is used, we shouldn't check if its empty
					if (usedInput.getInputSlotName().equals(inputEntry.getKey())) {
						continue inputLoop;
					}
				}
				
				// If this input slot is not used, it should be empty!
				ItemStack inSlot = inventory.getItem(inputEntry.getValue().getSlotIndex());
				if (!ItemUtils.isEmpty(inSlot)) {
					continue candidateLoop;
				}
			}
			
			for (OutputEntry output : candidate.getOutputs()) {
				ItemStack outSlot = getOutput(output.getOutputSlotName());
				OutputTable outputTable = output.getOutputTable();
				
				// If the output slot is empty, nothing could go wrong
				if (!ItemUtils.isEmpty(outSlot)) {
					
					// All possible output entries must be able to stack on top of
					// the current item stack in the output slot
					for (OutputTable.Entry entry : outputTable.getEntries()) {
						ItemStack potentialResult = (ItemStack) entry.getResult();
						if (!potentialResult.isSimilar(outSlot)) {
							continue candidateLoop;
						}
						if (ItemUtils.getMaxStacksize(outSlot) < outSlot.getAmount() + potentialResult.getAmount()) {
							continue candidateLoop;
						}
					}
				}
			}
			
			currentRecipe = candidate;
			break;
		}
		
		int oldCraftingProgress = currentCraftingProgress;
		if (oldRecipe != currentRecipe) {
			currentCraftingProgress = 0;
		}
		
		if (currentRecipe != null) {
			maybeStartBurning();
		}
		
		if (isBurning()) {
			if (currentRecipe != null) {
				currentCraftingProgress++;
				if (currentCraftingProgress >= currentRecipe.getDuration()) {
					
					// Decrease the stacksize of all relevant input slots by 1
					for (InputEntry input : currentRecipe.getInputs()) {
						
						int invIndex = typeInfo.getInputSlot(input.getInputSlotName()).getSlotIndex();
						ItemStack inputItem = inventory.getItem(invIndex);
						inputItem.setAmount(inputItem.getAmount() - 1);
						
						if (inputItem.getAmount() == 0) {
							inputItem = null;
						}
						
						inventory.setItem(invIndex, inputItem);
					}
					
					// Add the results to the output slots
					for (OutputEntry output : currentRecipe.getOutputs()) {
						
						int invIndex = typeInfo.getOutputSlot(output.getOutputSlotName()).getSlotIndex();
						ItemStack outputItem = inventory.getItem(invIndex);
						ItemStack result = (ItemStack) output.getOutputTable().pickResult(new Random());
						
						// result can be null because the chance to get something could be < 100%
						if (result != null) {
							// If the output slot is empty, set its item to the result
							// Otherwise increase its amount
							if (ItemUtils.isEmpty(outputItem)) {
								outputItem = result.clone();
							} else {
								outputItem.setAmount(outputItem.getAmount() + result.getAmount());
							}
							inventory.setItem(invIndex, outputItem);
						}
					}
					currentCraftingProgress = 0;
					storedExperience += currentRecipe.getExperience();
				}
			}
		}
		
		if (oldCraftingProgress != currentCraftingProgress) {
			for (IndicatorProps indicator : typeInfo.getCraftingIndicators()) {
				
				int newStacksize = 0;
				if (currentCraftingProgress > 0) {
					IndicatorDomain domain = indicator.getIndicatorDomain();
					newStacksize = domain.getStacksize(currentCraftingProgress, currentRecipe.getDuration());
				}
				
				if (newStacksize > 0) {
					ItemStack newItemStack = fromDisplay(indicator.getSlotDisplay());
					newItemStack.setAmount(newStacksize);
					inventory.setItem(indicator.getInventoryIndex(), newItemStack);
				} else {
					inventory.setItem(
							indicator.getInventoryIndex(), 
							fromDisplay(indicator.getPlaceholder()
					));
				}
			}
		}
		
		// Always decrease the burn times
		decrementBurnTimes();
	}
	
	private void updateFuelIndicator(String fuelSlotName) {
		
		for (IndicatorProps indicator : typeInfo.getFuelSlot(fuelSlotName).getIndicators()) {
			
			ItemStack indicatingStack = fromDisplay(indicator.getSlotDisplay());
			IndicatorDomain domain = indicator.getIndicatorDomain();
			
			FuelBurnEntry burn = fuelSlots.get(fuelSlotName);
			int indicatingStacksize = domain.getStacksize(burn.remainingBurnTime, burn.maxBurnTime);
			if (indicatingStacksize > 0) {
				indicatingStack.setAmount(indicatingStacksize);
			} else {
				indicatingStack = fromDisplay(indicator.getPlaceholder());
			}
			
			inventory.setItem(indicator.getInventoryIndex(), indicatingStack);
		}
	}
	
	private void decrementBurnTimes() {
		for (Entry<String, FuelBurnEntry> burnEntry : fuelSlots.entrySet()) {
			FuelBurnEntry burn = burnEntry.getValue();
			String fuelSlotName = burnEntry.getKey();
			if (burn.remainingBurnTime > 0) {
				burn.remainingBurnTime--;
				updateFuelIndicator(fuelSlotName);
			}
		}
	}
	
	private boolean isBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			for (FuelBurnEntry fuel : fuelSlots.values()) {
				if (fuel.remainingBurnTime == 0) {
					return false;
				}
			}
			return true;
			
		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			for (FuelBurnEntry fuel : fuelSlots.values()) {
				if (fuel.remainingBurnTime != 0) {
					return true;
				}
			}
			return false;
			
		} else {
			throw new Error("Unknown FuelMode: " + typeInfo.getContainer().getFuelMode());
		}
	}
	
	private boolean isFuel(String fuelSlotName, FuelBurnEntry slot, ItemStack candidateFuel) {
		return getBurnTime(fuelSlotName, slot, candidateFuel) != null;
	}
	
	private Integer getBurnTime(String fuelSlotName, FuelBurnEntry slot, ItemStack fuel) {
		if (ItemUtils.isEmpty(fuel)) {
			return null;
		}
		for (FuelEntry registryEntry : typeInfo.getFuelSlot(fuelSlotName).getRegistry().getEntries()) {
			Ingredient ingredient = (Ingredient) registryEntry.getFuel();
			if (ingredient.accept(fuel)) {
				return registryEntry.getBurnTime();
			}
		}
		
		return null;
	}
	
	private boolean canStartBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				// If this slot is not yet burning, check if it contains fuel
				if (fuel.remainingBurnTime == 0) {
					ItemStack fuelStack = getFuel(fuelSlotName);
					
					// If this slot doesn't contain proper fuel, it's lost
					if (!isFuel(fuelSlotName, fuel, fuelStack)) {
						return false;
					}
				}
				// If it's already burning, this entry is not a bottleneck
			}
			
			// If we reach this line, all fuel slots are either burning or have fuel
			return true;
		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				// If it's already burning, we are already done
				if (fuel.remainingBurnTime != 0) {
					return true;
				}
				
				// Check if it could start burning
				ItemStack fuelCandidate = getFuel(fuelSlotName);
				if (isFuel(fuelSlotName, fuel, fuelCandidate)) {
					return true;
				}
			}
			
			// If not a single candidate was found, return false
			return false;
		} else {
			throw new Error("Unknown FuelMode: " + typeInfo.getContainer().getFuelMode());
		}
	}
	
	/**
	 * Should only be called when canStartBurning() returned true
	 */
	private void startBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			// Make sure all fuel slots are burning
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				// Start burning if it isn't burning yet
				if (fuel.remainingBurnTime == 0) {
					
					ItemStack fuelStack = getFuel(fuelSlotName);
					Integer burnTime = getBurnTime(fuelSlotName, fuel, fuelStack);
					if (burnTime != null) {
						fuel.remainingBurnTime = burnTime;
						fuel.maxBurnTime = fuel.remainingBurnTime;
						fuelStack.setAmount(fuelStack.getAmount() - 1);

						setFuel(fuelSlotName, fuelStack);
						updateFuelIndicator(fuelSlotName);
					}
				}
			}
		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			// Start burning the first best valid fuel
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				
				String fuelSlotName = fuelEntry.getKey();
				FuelBurnEntry fuel = fuelEntry.getValue();
				
				ItemStack fuelStack = getFuel(fuelSlotName);
				Integer burnTime = getBurnTime(fuelSlotName, fuel, fuelStack);
				if (burnTime != null) {
					fuel.remainingBurnTime = burnTime;
					fuel.maxBurnTime = burnTime;
					fuelStack.setAmount(fuelStack.getAmount() - 1);
					
					setFuel(fuelSlotName, fuelStack);
					updateFuelIndicator(fuelSlotName);
					return;
				}
			}
			
			throw new IllegalStateException("Couldn't start burning " + typeInfo.getContainer().getName());
		} else {
			throw new Error("Unknown FuelMode: " + typeInfo.getContainer().getFuelMode());
		}
	}
	
	/**
	 * Starts burning if there is enough fuel and this instance is not yet burning
	 */
	private void maybeStartBurning() {
		
		// Only proceed if we are not yet burning
		if (!isBurning()) {
			
			// Only proceed if we have enough fuel to start
			if (canStartBurning()) {
				startBurning();
			}
		}
	}
	
	private static class FuelBurnEntry {
		
		int remainingBurnTime;
		int maxBurnTime;
		
		FuelBurnEntry() {
			this.remainingBurnTime = 0;
			this.maxBurnTime = 0;
		}
	}
}
