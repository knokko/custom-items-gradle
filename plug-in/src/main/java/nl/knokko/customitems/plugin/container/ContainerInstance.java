package nl.knokko.customitems.plugin.container;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.container.energy.RecipeEnergyOperation;
import nl.knokko.customitems.container.energy.RecipeEnergyValues;
import nl.knokko.customitems.container.fuel.FuelEntryValues;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.StorageSlotValues;
import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.data.ContainerStorageKey;
import nl.knokko.customitems.plugin.data.StoredEnergy;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.container.ContainerInfo.DecorationProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.FuelProps;
import nl.knokko.customitems.plugin.container.ContainerInfo.IndicatorProps;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptItemStack;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

/**
 * An in-game instance of a custom container. While the CustomContainer class defines
 * which types of slots and recipes a custom container will have, a ContainerInstance
 * defines which items are currently present in those slots and optionally which
 * (smelting) recipes are currently in progress.
 */
public class ContainerInstance {
	
	public static final String[] PLACEHOLDER_KEY = {"KnokkosItemFlags", "IsSlotDisplay"};
	
	@SuppressWarnings("deprecation")
	public static ItemStack fromDisplay(SlotDisplayValues display) {
		ItemStack stack;
		boolean isCustom = display.getDisplayItem() instanceof CustomDisplayItemValues;
		if (isCustom) {
			CustomItemValues customItem = ((CustomDisplayItemValues) display.getDisplayItem()).getItem();
			stack = wrap(customItem).create(display.getAmount());
		} else {
			CIMaterial material;
			if (display.getDisplayItem() instanceof DataVanillaDisplayItemValues) {
				material = ((DataVanillaDisplayItemValues) display.getDisplayItem()).getMaterial();
			} else if (display.getDisplayItem() instanceof SimpleVanillaDisplayItemValues) {
				material = ((SimpleVanillaDisplayItemValues) display.getDisplayItem()).getMaterial();
			} else {
				throw new Error("Unknown display type: " + display);
			}
			stack = ItemHelper.createStack(material.name(), display.getAmount());
			if (display.getDisplayItem() instanceof DataVanillaDisplayItemValues) {
				MaterialData data = stack.getData();
				data.setData(((DataVanillaDisplayItemValues) display.getDisplayItem()).getDataValue());
				stack.setData(data);
				stack.setDurability(data.getData());
			}
		}
		
		ItemMeta meta = stack.getItemMeta();
		
		// If a custom item is used, only overwrite display name and lore if its
		// specifically specified
		if (!isCustom || !display.getDisplayName().isEmpty())
			meta.setDisplayName(display.getDisplayName());
		if (!isCustom || display.getLore().size() > 0)
			meta.setLore(Lists.newArrayList(display.getLore()));
		
		// Store changes in item meta
		stack.setItemMeta(meta);
		
		GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(stack);
		nbt.set(PLACEHOLDER_KEY, 1);
		return nbt.backToBukkit();
	}
	
	private static Inventory createInventory(ContainerInfo typeInfo) {
		CustomContainerValues container = typeInfo.getContainer();

		String displayName = container.getSelectionIcon().getDisplayName();
		if (container.getOverlayTexture() != null) {
			displayName = (char) 0xE000 + "" + (char) 167 + "f" + container.getOverlayChar() + displayName;
		}
		Inventory inv = Bukkit.createInventory(null, 9 * container.getHeight(), displayName);
		
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

	public static void discard3(BitInput input) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding != 1) throw new UnknownEncodingException("ContainerInstance", encoding);

		discard2(input);
		int numPermissions = input.readInt();
		for (int counter = 0; counter < numPermissions; counter++) input.readString();
	}
	
	public static ContainerInstance load1(
			BitInput input, ContainerInfo typeInfo, UUID ownerID,
			ContainerStorageKey storageKey, StoredEnergy storedEnergy, Collection<ItemStack> orphanStacks
	) {
		ContainerInstance instance = new ContainerInstance(typeInfo, ownerID, storageKey, storedEnergy);

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
					Iterable<Integer> thirdSlots, Collection<ItemStack> orphanStacks
			) {
				
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
				
				// If this line is reached, this item will become an orphan stack
				orphanStacks.add(stack);
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
				inputSlotName -> takeFrom(typeInfo.getInputSlot(inputSlotName), ContainerInfo.PlaceholderProps::getSlotIndex),
		inv));
		outputStacks.removeIf(outputStack -> outputStack.putSimple(
				outputSlotName -> takeFrom(typeInfo.getOutputSlot(outputSlotName), ContainerInfo.PlaceholderProps::getSlotIndex),
		inv));
		fuelStacks.removeIf(fuelStack -> fuelStack.putSimple(
				fuelSlotName -> takeFrom(typeInfo.getFuelSlot(fuelSlotName), FuelProps::getSlotIndex),
		inv));
		
		// Now the annoying cases where the slot is changed or renamed
		for (StringStack inputStack : inputStacks) {
			inputStack.putInEmptySlot(
					takeValues(typeInfo.getInputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex),
					takeValues(typeInfo.getOutputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex),
					takeValues(typeInfo.getFuelSlots(), FuelProps::getSlotIndex),
					orphanStacks
			);
		}
		
		for (StringStack outputStack : outputStacks) {
			outputStack.putInEmptySlot(
					takeValues(typeInfo.getOutputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex),
					takeValues(typeInfo.getFuelSlots(), FuelProps::getSlotIndex),
					takeValues(typeInfo.getInputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex),
					orphanStacks
			);
		}
		
		for (StringStack fuelStack : fuelStacks) {
			fuelStack.putInEmptySlot(
					takeValues(typeInfo.getFuelSlots(), FuelProps::getSlotIndex),
					takeValues(typeInfo.getOutputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex),
					takeValues(typeInfo.getInputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex),
					orphanStacks
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

		// This is needed to prevent resetting the currentCraftingProgress in the first update
		// (due to the check if oldRecipe != currentRecipe).
		instance.currentRecipe = instance.determineCurrentRecipe(null);
		
		return instance;
	}

	private static <T> Integer takeFrom(T nullableSource, Function<T, Integer> takeFromSource) {
		if (nullableSource == null) return null;
		return takeFromSource.apply(nullableSource);
	}

	public static ContainerInstance load2(
			BitInput input, ContainerInfo typeInfo, UUID ownerID,
			ContainerStorageKey storageKey, StoredEnergy storedEnergy
	) {
		Collection<ItemStack> orphanStacks = new ArrayList<>(0);

		ContainerInstance base = load1(input, typeInfo, ownerID, storageKey, storedEnergy, orphanStacks);

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

			ContainerSlotValues slot = typeInfo.getContainer().getSlot(x, y);
			if (slot instanceof StorageSlotValues) {
			    base.inventory.setItem(invIndex, storedStack);
			} else {
				// This can happen if the admin (re)moved the storage slot
				orphanStacks.add(storedStack);
			}
		}

		// If there are 'orphan' stacks (due to changes in the container layout between world saves), we should
		// try to put them in free storage slots or in input/fuel slots. Preferably storage slots
		if (!orphanStacks.isEmpty()) {
			List<Integer> freeSlots = new ArrayList<>(40);

			// Collect the indices of the free storage slots first
			for (int y = 0; y < typeInfo.getContainer().getHeight(); y++) {
				for (int x = 0; x < 9; x++) {
					int invIndex = x + 9 * y;
					ContainerSlotValues slot = typeInfo.getContainer().getSlot(x, y);
					if (slot instanceof StorageSlotValues) {
						ItemStack currentStack = base.inventory.getItem(invIndex);
						if (ItemUtils.isEmpty(currentStack)) {
							freeSlots.add(invIndex);
						}
					}
				}
			}

			// Collect the indices of all free input, output, and fuel slots, explicitly in that order
			Stream.concat(Stream.concat(
					StreamSupport.stream(typeInfo.getInputSlots().spliterator(), false),
					StreamSupport.stream(typeInfo.getOutputSlots().spliterator(), false)
			).map(entry -> entry.getValue().getSlotIndex()),
					StreamSupport.stream(typeInfo.getFuelSlots().spliterator(), false)
					.map(entry -> entry.getValue().getSlotIndex())
			).forEach(slotIndex -> {
				if (ItemUtils.isEmpty(base.inventory.getItem(slotIndex))) {
					freeSlots.add(slotIndex);
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

		return base;
	}

	public static ContainerInstance load3(
			BitInput input, ContainerInfo typeInfo, UUID ownerID,
			ContainerStorageKey storageKey, StoredEnergy storedEnergy
	) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding != 1) throw new UnknownEncodingException("ContainerInstance", encoding);

		ContainerInstance instance = load2(input, typeInfo, ownerID, storageKey, storedEnergy);

		instance.relevantPermissions.clear();
		int numPermissions = input.readInt();
		for (int counter = 0; counter < numPermissions; counter++) {
			instance.relevantPermissions.add(input.readString());
		}

		return instance;
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
			Entry<T, Integer> resultEntry = new SimpleEntry<>(entry.getKey(), getIndex.apply(entry.getValue()));
			return resultEntry;
		}).collect(Collectors.toList());
	}

	private final ContainerInfo typeInfo;
	private final ContainerStorageKey storageKey;
	public final StoredEnergy storedEnergy;
	
	private final Inventory inventory;
	private final Set<String> relevantPermissions;
	private final UUID ownerID;
	
	// Will stay empty if there are no fuel slots
	private final Map<String, FuelBurnEntry> fuelSlots;
	
	// Will be ignored if the crafting for this container type is instant
	private int currentCraftingProgress;

	private int remainingHotTime;
	
	private ContainerRecipeValues currentRecipe;
	
	private int storedExperience;
	
	public ContainerInstance(
			ContainerInfo typeInfo, UUID ownerID,
			ContainerStorageKey storageKey, StoredEnergy storedEnergy
	) {
		if (typeInfo == null) throw new NullPointerException("typeInfo");
		this.typeInfo = typeInfo;
		this.storageKey = storageKey;
		this.storedEnergy = storedEnergy;
		this.inventory = createInventory(typeInfo);

		this.ownerID = ownerID;
		this.relevantPermissions = new HashSet<>();
		this.updateRelevantPermissions();

		this.fuelSlots = new HashMap<>();
		this.initFuelSlots();
		
		this.currentCraftingProgress = 0;
		this.markHot();
	}

	private void updateRelevantPermissions(Player owner) {
		relevantPermissions.clear();
		if (owner.hasPermission("customitems.container.recipe.any")) {
			relevantPermissions.add("customitems.container.recipe.any");
		}
		for (ContainerRecipeValues recipe : getType().getRecipes()) {
			String permission = recipe.getRequiredPermission();
			if (permission != null && owner.hasPermission(permission)) {
				relevantPermissions.add(permission);
			}
		}
	}

	private void updateRelevantPermissions() {
		if (ownerID != null) {
			Player owner = Bukkit.getPlayer(ownerID);
			if (owner != null) {
				updateRelevantPermissions(owner);
			}
		}
	}

	private void markHot() {
		remainingHotTime = 20;
	}

	private boolean isHot() {
		return remainingHotTime > 0;
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
	
	private void save1(BitOutput output) {
		
		// Since the container layout of type could change, we need to be careful
		// We will store entries of (slotName, itemStack) to store all items
		BiConsumer<String, Integer> stackSaver = (slotName, invIndex) -> {
			output.addString(slotName);
			ItemStack itemStack = inventory.getItem(invIndex);
			
			// Serializing item stacks is a little effort
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
				typeInfo.getInputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex
		));
		slotsSaver.accept(mapEntries(
				typeInfo.getOutputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex
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

	private void save2(BitOutput output) {
		save1(output);

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
		typeInfo.getStorageSlots().forEach(slotProps -> {
			int invIndex = slotProps.getSlotIndex();
			int x = invIndex % 9;
			int y = invIndex / 9;
			if (!ItemUtils.isEmpty(inventory.getItem(invIndex))) {
				stacksToSave.add(new StackEntry(x, y, inventory.getItem(invIndex)));
			}
		});

		output.addInt(stacksToSave.size());
		stacksToSave.forEach(entry -> {
			output.addByte(entry.x);
			output.addByte(entry.y);
			saveStack(output, entry.stack);
		});
	}

	public void save3(BitOutput output) {
		output.addByte((byte) 1);
		save2(output);
		output.addInt(relevantPermissions.size());
		for (String permission : relevantPermissions) {
			output.addString(permission);
		}
	}
	
	public CustomContainerValues getType() {
		return typeInfo.getContainer();
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public void dropAllItems(Location location) {
		dropAllItems(location, typeInfo.getInputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex);
		dropAllItems(location, typeInfo.getOutputSlots(), ContainerInfo.PlaceholderProps::getSlotIndex);
		dropAllItems(location, typeInfo.getFuelSlots(), FuelProps::getSlotIndex);
		typeInfo.getStorageSlots().forEach(props -> {
			if (!ItemUtils.isEmpty(inventory.getItem(props.getSlotIndex()))) {
				location.getWorld().dropItem(location, inventory.getItem(props.getSlotIndex()));
				inventory.setItem(props.getSlotIndex(), null);
			}
		});
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

	/**
	 * NOTE: This might be out of date. Confirm with determineCurrentRecipe to be certain!
	 */
	ContainerRecipeValues getCurrentRecipe() {
		return this.currentRecipe;
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

		for (Entry<String, ContainerInfo.PlaceholderProps> manualSlot : typeInfo.getManualOutputSlots()) {
			int slotIndex = manualSlot.getValue().getSlotIndex();
			if (currentRecipe == null || !manualSlot.getKey().equals(currentRecipe.getManualOutputSlotName())) {
				if (manualSlot.getValue().getPlaceholder() == null) {
					inventory.setItem(slotIndex, null);
				} else {
					inventory.setItem(slotIndex, fromDisplay(manualSlot.getValue().getPlaceholder()));
				}
			} else {
				inventory.setItem(slotIndex, convertResultToItemStack(currentRecipe.getManualOutput()));
			}
		}

		Stream.concat(Stream.concat(
				StreamSupport.stream(typeInfo.getInputSlots().spliterator(), false),
				StreamSupport.stream(typeInfo.getOutputSlots().spliterator(), false)
		).map(Entry::getValue), StreamSupport.stream(typeInfo.getStorageSlots().spliterator(), false)
		).forEach(props -> {
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

	private boolean canPerformRecipe(ContainerRecipeValues candidate) {

		String permission = candidate.getRequiredPermission();
		if (
				permission != null && ownerID != null && !relevantPermissions.contains(permission) &&
						!relevantPermissions.contains("customitems.container.recipe.any")
		) {
			return false;
		}

		for (RecipeEnergyValues energyRequirement : candidate.getEnergy()) {

			if (energyRequirement.getOperation() == RecipeEnergyOperation.REQUIRE_AT_LEAST) {
				int actualAmount = storedEnergy.getEnergy(energyRequirement.getEnergyType(), storageKey);
				if (actualAmount < energyRequirement.getAmount()) return false;
			}

			if (energyRequirement.getOperation() == RecipeEnergyOperation.REQUIRE_AT_MOST) {
				int actualAmount = storedEnergy.getEnergy(energyRequirement.getEnergyType(), storageKey);
				if (actualAmount > energyRequirement.getAmount()) return false;
			}

			// There is no need to check the INCREASE and DECREASE operations
		}

		// Check that all inputs are present
		for (Map.Entry<String, IngredientValues> input : candidate.getInputs().entrySet()) {
			ItemStack inSlot = getInput(input.getKey());
			IngredientValues ingredient = input.getValue();
			if (!shouldIngredientAcceptItemStack(ingredient, inSlot)) {
				return false;
			}
		}

		// Check that all other inputs are empty
		inputLoop:
		for (Entry<String, ContainerInfo.PlaceholderProps> inputEntry : typeInfo.getInputSlots()) {
			for (Map.Entry<String, IngredientValues> usedInput : candidate.getInputs().entrySet()) {

				// If this input is used, we shouldn't check if its empty
				if (usedInput.getKey().equals(inputEntry.getKey())) {
					continue inputLoop;
				}
			}

			// If this input slot is not used, it should be empty!
			ItemStack inSlot = inventory.getItem(inputEntry.getValue().getSlotIndex());
			if (!ItemUtils.isEmpty(inSlot)) {
				return false;
			}
		}

		for (Entry<String, OutputTableValues> output : candidate.getOutputs().entrySet()) {
			ItemStack outSlot = getOutput(output.getKey());
			OutputTableValues outputTable = output.getValue();

			// If the output slot is empty, nothing could go wrong
			if (!ItemUtils.isEmpty(outSlot)) {

				// All possible output entries must be able to stack on top of
				// the current item stack in the output slot
				for (OutputTableValues.Entry entry : outputTable.getEntries()) {
					ItemStack potentialResult = convertResultToItemStack(entry.getResult());
					if (!potentialResult.isSimilar(outSlot)) {
						return false;
					}
					if (ItemUtils.getMaxStacksize(outSlot) < outSlot.getAmount() + potentialResult.getAmount()) {
					    return false;
					}
				}
			}
		}

		// No special check is needed for manual output slots, since their content can simply be overwritten

		return true;
	}

	ContainerRecipeValues determineCurrentRecipe(ContainerRecipeValues mostLikely) {
		// Ensure that the container recipe permissions are always up-to-date before determining the recipe
		updateRelevantPermissions();

		// Performance trick: first check the recipe that is most likely. If we can perform that recipe,
		// we don't need to waste time with checking the other recipes.
		if (mostLikely != null && canPerformRecipe(mostLikely)) {
			return mostLikely;
		}

		// If the most likely candidate can't be performed, we will have to try all other recipes
		for (ContainerRecipeValues candidate : typeInfo.getContainer().getRecipes()) {
			if (canPerformRecipe(candidate)) {
				return candidate;
			}
		}

		return null;
	}
	
	public void update() {
		boolean hasViewers = !inventory.getViewers().isEmpty();
	    if (hasViewers) {
	    	markHot();
		}

	    // For the sake of performance, we only update *hot* containers. Containers are considered hot when
		// they have been viewed by a player very recently (or are currently being viewed), or have very
		// recently performed a container recipe (or is currently busy with a recipe).
		// Also, all containers are *hot* right after this plugin is enabled (so that each container can
		// check whether it has to do recipes).
		if (isHot()) {
			if (hasViewers) {
				updatePlaceholders();
			}

			updateEnergyIndicators();

			ContainerRecipeValues oldRecipe = currentRecipe;
			currentRecipe = null;

			// Performance improvement: rather than always checking current recipe 20 times per second,
			// we should only check 20 times per second when either there is no current recipe (and at
			// least 1 player is viewing it). When the recipe is in progress, we only update 2 times
			// per second (which is still a quite fast response).
			// We also need to validate it right before producing the result (to avoid any potential
			// duplicate glitches).
			if (currentCraftingProgress % 10 == 0) {
				currentRecipe = determineCurrentRecipe(oldRecipe);
			} else {
				currentRecipe = oldRecipe;
			}

			if (currentRecipe != null && currentRecipe.getManualOutput() == null) {
				markHot();
			}

			int oldCraftingProgress = currentCraftingProgress;
			if (oldRecipe != currentRecipe) {
				currentCraftingProgress = 0;
			}

			if (currentRecipe != null && currentRecipe.getManualOutput() == null) {
				maybeStartBurning();
			}

			if (isBurning()) {
				if (currentRecipe != null && currentRecipe.getManualOutput() == null) {
					currentCraftingProgress++;
					if (currentCraftingProgress >= currentRecipe.getDuration()) {

						// Now that we only check the inputs periodically, we should be absolutely sure
						// that all inputs are still present before producing a result.
					    if (currentRecipe == determineCurrentRecipe(currentRecipe)) {

							// Decrease the stacksize of all relevant input slots
							this.consumeIngredientsAndEnergyOfCurrentRecipe();

							// Add the results to the output slots
							for (Map.Entry<String, OutputTableValues> output : currentRecipe.getOutputs().entrySet()) {

								int invIndex = typeInfo.getOutputSlot(output.getKey()).getSlotIndex();
								ItemStack outputItem = inventory.getItem(invIndex);
								ItemStack result = convertResultToItemStack(output.getValue().pickResult(new Random()));

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
							storedExperience += currentRecipe.getExperience();
						} else {
					    	// The current could be updating during the next tick, but this tick is lost
					    	currentRecipe = null;
						}

						currentCraftingProgress = 0;
					}
				}
			}

			if (oldCraftingProgress != currentCraftingProgress) {

				// For performance reasons, we should only update the crafting indicators when there is a
				// player who can actually see it, but also when the crafting stops (see the comments in
				// decrementBurnTimes for an explanation about this).
			    if (hasViewers || currentCraftingProgress == 0) {
					for (IndicatorProps indicator : typeInfo.getCraftingIndicators()) {

						int newStacksize = 0;
						if (currentCraftingProgress > 0) {
							IndicatorDomain domain = indicator.getIndicatorDomain();
							newStacksize = domain.getStacksize(currentCraftingProgress, 0, currentRecipe.getDuration());
						}

						if (newStacksize > 0) {
							ItemStack newItemStack = fromDisplay(indicator.getSlotDisplay());
							newItemStack.setAmount(newStacksize);
							inventory.setItem(indicator.getInventoryIndex(), newItemStack);
						} else {
							inventory.setItem(
									indicator.getInventoryIndex(),
									fromDisplay(indicator.getPlaceholder())
							);
						}
					}
				}
			}

			remainingHotTime--;
		}

		// Always decrease the burn times
		decrementBurnTimes();
	}

	private void updateEnergyIndicators() {
		for (ContainerInfo.EnergyIndicatorProps indicator : typeInfo.getEnergyIndicators()) {
			ItemStack indicatingStack = fromDisplay(indicator.getSlotDisplay());
			IndicatorDomain domain = indicator.getIndicatorDomain();

			EnergyTypeValues energyType = indicator.getEnergyType().get();
			int currentAmount = storedEnergy.getEnergy(energyType, storageKey);

			int indicatingStacksize = domain.getStacksize(currentAmount, energyType.getMinValue(), energyType.getMaxValue());
			if (indicatingStacksize > 0) {
				indicatingStack.setAmount(indicatingStacksize);
			} else {
				indicatingStack = fromDisplay(indicator.getPlaceholder());
			}

			inventory.setItem(indicator.getInventoryIndex(), indicatingStack);
		}
	}

	void consumeIngredientsAndEnergyOfCurrentRecipe() {

		for (RecipeEnergyValues energyAction : currentRecipe.getEnergy()) {

			if (energyAction.getOperation() == RecipeEnergyOperation.DECREASE) {
				storedEnergy.decreaseEnergy(energyAction.getEnergyType(), storageKey, energyAction.getAmount());
			}

			if (energyAction.getOperation() == RecipeEnergyOperation.INCREASE) {
				storedEnergy.increaseEnergy(energyAction.getEnergyType(), storageKey, energyAction.getAmount());
			}

			// There is no need to do anything with REQUIRE_AT_LEAST and REQUIRE_AT_MOST here
		}

		for (Map.Entry<String, IngredientValues> input : currentRecipe.getInputs().entrySet()) {

			int invIndex = typeInfo.getInputSlot(input.getKey()).getSlotIndex();

			ItemStack remainingItem = convertResultToItemStack(input.getValue().getRemainingItem());
			if (remainingItem != null) {
				inventory.setItem(invIndex, remainingItem);
			} else {
				ItemStack inputItem = inventory.getItem(invIndex);
				inputItem.setAmount(inputItem.getAmount() - input.getValue().getAmount());

				if (inputItem.getAmount() == 0) {
					inputItem = null;
				}

				inventory.setItem(invIndex, inputItem);
			}
		}
	}
	
	private void updateFuelIndicator(String fuelSlotName) {
		
		for (IndicatorProps indicator : typeInfo.getFuelSlot(fuelSlotName).getIndicators()) {
			
			ItemStack indicatingStack = fromDisplay(indicator.getSlotDisplay());
			IndicatorDomain domain = indicator.getIndicatorDomain();
			
			FuelBurnEntry burn = fuelSlots.get(fuelSlotName);
			int indicatingStacksize = domain.getStacksize(burn.remainingBurnTime, 0, burn.maxBurnTime);
			if (indicatingStacksize > 0) {
				indicatingStack.setAmount(indicatingStacksize);
			} else {
				indicatingStack = fromDisplay(indicator.getPlaceholder());
			}
			
			inventory.setItem(indicator.getInventoryIndex(), indicatingStack);
		}
	}
	
	private void decrementBurnTimes() {
		boolean hasViewers = !inventory.getViewers().isEmpty();
		for (Entry<String, FuelBurnEntry> burnEntry : fuelSlots.entrySet()) {
			FuelBurnEntry burn = burnEntry.getValue();
			String fuelSlotName = burnEntry.getKey();
			if (burn.remainingBurnTime > 0) {
				burn.remainingBurnTime--;

				// When at least 1 player is viewing this container, we should always update the fuel
				// indicators. If nobody is viewing it, we generally don't need to update the indicator
				// (because there is no-one to see it). But, if the remaining burn time is 0, it will
				// stop burning. If we wouldn't update this, the next time a player opens the container,
				// he would still see the old burning indicator even though it is not burning anymore.
				if (burn.remainingBurnTime == 0 || hasViewers) {
					updateFuelIndicator(fuelSlotName);
				}
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
		return getSuitableFuelEntry(fuelSlotName, slot, candidateFuel) != null;
	}
	
	private FuelEntryValues getSuitableFuelEntry(String fuelSlotName, FuelBurnEntry slot, ItemStack fuel) {
		if (ItemUtils.isEmpty(fuel)) {
			return null;
		}
		for (FuelEntryValues registryEntry : typeInfo.getFuelSlot(fuelSlotName).getRegistry().getEntries()) {
			IngredientValues ingredient = registryEntry.getFuel();
			if (shouldIngredientAcceptItemStack(ingredient, fuel)) {
				return registryEntry;
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

	private boolean startBurningIfIdle(String fuelSlotName, FuelBurnEntry fuel) {
		// Start burning if it isn't burning yet
		if (fuel.remainingBurnTime == 0) {

			ItemStack fuelStack = getFuel(fuelSlotName);
			FuelEntryValues entryToBurn = getSuitableFuelEntry(fuelSlotName, fuel, fuelStack);
			if (entryToBurn != null) {
				fuel.remainingBurnTime = entryToBurn.getBurnTime();
				fuel.maxBurnTime = fuel.remainingBurnTime;

				IngredientValues fuelIngredient = entryToBurn.getFuel();
				if (fuelIngredient.getRemainingItem() == null) {
					fuelStack.setAmount(fuelStack.getAmount() - fuelIngredient.getAmount());
					setFuel(fuelSlotName, fuelStack);
				} else {
					setFuel(fuelSlotName, convertResultToItemStack(fuelIngredient.getRemainingItem()));
				}

				updateFuelIndicator(fuelSlotName);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Should only be called when canStartBurning() returned true
	 */
	private void startBurning() {
		if (typeInfo.getContainer().getFuelMode() == FuelMode.ALL) {
			
			// Make sure all fuel slots are burning
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				startBurningIfIdle(fuelEntry.getKey(), fuelEntry.getValue());
			}

		} else if (typeInfo.getContainer().getFuelMode() == FuelMode.ANY) {
			
			// Start burning the first best valid fuel
			for (Entry<String, FuelBurnEntry> fuelEntry : fuelSlots.entrySet()) {
				if (startBurningIfIdle(fuelEntry.getKey(), fuelEntry.getValue())) {
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
