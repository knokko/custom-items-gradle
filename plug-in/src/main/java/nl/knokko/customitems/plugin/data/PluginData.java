package nl.knokko.customitems.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import nl.knokko.customitems.container.slot.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.set.item.CustomWand;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class PluginData {
	
	private static final byte ENCODING_1 = 1;
	private static final byte ENCODING_2 = 2;
	private static final byte ENCODING_3 = 3;
	
	private static File getDataFile() {
		return new File(CustomItemsPlugin.getInstance().getDataFolder() + "/gamedata.bin");
	}
	
	/**
	 * Attempts to read the plugin data that was saved previously. If the data can be found, it will be
	 * loaded and a PluginData instance with the loaded data will be returned.
	 * If no previously saved data was found, a new empty PluginData instance will be returned.
	 * 
	 * This method should be called exactly once in the onEnable() of CustomItemsPlugin.
	 * @return A new PluginData or the previously saved PluginData
	 */
	public static PluginData loadData() {
		File dataFile = getDataFile();
		if (dataFile.exists()) {
			try {
				BitInput input = ByteArrayBitInput.fromFile(dataFile);
				
				byte encoding = input.readByte();
				switch (encoding) {
					case ENCODING_1:
						return load1(input);
					case ENCODING_2:
						return load2(input);
					case ENCODING_3:
						return load3(input);
					default:
						throw new IllegalArgumentException("Unknown data encoding: " + encoding);
				}
			} catch (IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Failed to open the data file for CustomItems", e);
				Bukkit.getLogger().severe("The current data for CustomItems won't be overwritten when you stop the server.");
				return new CarefulPluginData();
			}
		} else {
			Bukkit.getLogger().warning("Couldn't find the data file for CustomItems. Is this the first time you are using CustomItems with version at least 6.0?");
			return new PluginData();
		}
	}
	
	private static Map<UUID, PlayerData> loadPlayerData1(BitInput input, ItemSet set) {
		int numPlayers = input.readInt();
		Map<UUID,PlayerData> playersMap = new HashMap<>(numPlayers);
		for (int counter = 0; counter < numPlayers; counter++) {
			UUID id = new UUID(input.readLong(), input.readLong());
			PlayerData data = PlayerData.load1(input, set, Bukkit.getLogger());
			playersMap.put(id, data);
		}
		
		return playersMap;
	}
	
	private static PluginData load1(BitInput input) {
		long currentTick = input.readLong();
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		
		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, plugin.getSet());
		
		// There were no persistent containers in this version
		return new PluginData(currentTick, playersMap, new HashMap<>());
	}
	
	private static PluginData load2(BitInput input) {
		long currentTick = input.readLong();
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		
		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, plugin.getSet());
		
		int numPersistentContainers = input.readInt();
		Map<ContainerLocation, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);
		
		for (int counter = 0; counter < numPersistentContainers; counter++) {
			
			UUID worldId = new UUID(input.readLong(), input.readLong());
			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();
			String typeName = input.readString();
			
			ContainerInfo typeInfo = plugin.getSet().getContainerInfo(typeName);
			
			if (typeInfo != null) {
				ContainerInstance instance = ContainerInstance.load1(input, typeInfo);
				ContainerLocation location = new ContainerLocation(new PassiveLocation(worldId, x, y, z), typeInfo.getContainer());
				persistentContainers.put(location, instance);
			} else {
				ContainerInstance.discard1(input);
			}
		}
		
		return new PluginData(currentTick, playersMap, persistentContainers);
	}

	private static PluginData load3(BitInput input) {
		long currentTick = input.readLong();
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();

		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, plugin.getSet());

		int numPersistentContainers = input.readInt();
		Map<ContainerLocation, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

		for (int counter = 0; counter < numPersistentContainers; counter++) {

			UUID worldId = new UUID(input.readLong(), input.readLong());
			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();
			String typeName = input.readString();

			ContainerInfo typeInfo = plugin.getSet().getContainerInfo(typeName);

			if (typeInfo != null) {
				ContainerInstance instance = ContainerInstance.load2(input, typeInfo);
				ContainerLocation location = new ContainerLocation(new PassiveLocation(worldId, x, y, z), typeInfo.getContainer());
				persistentContainers.put(location, instance);
			} else {
				ContainerInstance.discard2(input);
			}
		}

		return new PluginData(currentTick, playersMap, persistentContainers);
	}

	// Persisting data
	private final Map<UUID,PlayerData> playerData;
	private final Map<ContainerLocation,ContainerInstance> persistentContainers;
	
	private long currentTick;
	
	// Non-persisting data
	private Collection<TempContainerInstance> tempContainers;
	private List<Player> shootingPlayers;
	private Map<VanillaContainerType, List<CustomContainer>> containerTypeMap;
	private Map<VanillaContainerType, Inventory> containerSelectionMap;

	private PluginData() {
		playerData = new HashMap<>();
		persistentContainers = new HashMap<>();
		currentTick = 0;
		
		init();
	}
	
	private PluginData(long currentTick, Map<UUID,PlayerData> playerData,
			Map<ContainerLocation, ContainerInstance> persistentContainers) {
		this.playerData = playerData;
		this.persistentContainers = persistentContainers;
		this.currentTick = currentTick;
		
		init();
	}
	
	private void init() {
		tempContainers = new LinkedList<>();
		shootingPlayers = new LinkedList<>();
		initContainerTypeMap();
		
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::update, 1, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::quickClean, 50, 10);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::clean, 200, 40);
	}
	
	private void initContainerTypeMap() {
		containerTypeMap = new EnumMap<>(VanillaContainerType.class);
		containerSelectionMap = new EnumMap<>(VanillaContainerType.class);
		// TODO Wait... what happens when /kci reload is used to add a new container?
		
		ItemSet set = CustomItemsPlugin.getInstance().getSet();
		for (VanillaContainerType vanillaType : VanillaContainerType.values()) {
			
			List<CustomContainer> containersForType = new ArrayList<>();
			for (CustomContainer container : set.getContainers()) {
				if (container.getVanillaType() == vanillaType) {
					containersForType.add(container);
				}
			}
			
			containerTypeMap.put(vanillaType, containersForType);
			if (containersForType.size() > 1) {
				containerSelectionMap.put(vanillaType, createContainerSelectionMenu(containersForType));
			}
		}
	}
	
	private Inventory createContainerSelectionMenu(
			List<CustomContainer> containers) {
		int invSize = 1 + containers.size();
		if (invSize % 9 != 0) {
			invSize = 9 + 9 * (invSize / 9);
		}
		
		Inventory menu = Bukkit.createInventory(null, invSize, "Choose custom container");
		{
			ItemStack cancelStack = ItemHelper.createStack(CIMaterial.BARRIER.name(), 1);
			ItemMeta meta = cancelStack.getItemMeta();
			meta.setDisplayName("Cancel");
			cancelStack.setItemMeta(meta);
			menu.setItem(0, cancelStack);
		}
		
		for (int listIndex = 0; listIndex < containers.size(); listIndex++) {
			int invIndex = listIndex + 1;
			CustomContainer container = containers.get(listIndex);
			
			menu.setItem(invIndex, ContainerInstance.fromDisplay(container.getSelectionIcon()));
		}
		
		return menu;
	}
	
	private void update() {
		currentTick++;
		
		updateShooting();
		updateContainers();
	}
	
	private void updateShooting() {
		ItemSet set = CustomItemsPlugin.getInstance().getSet();
		Iterator<Player> iterator = shootingPlayers.iterator();
		while (iterator.hasNext()) {
			Player current = iterator.next();
			PlayerData data = getPlayerData(current);
			if (data.isShooting(currentTick)) {
				CustomItem mainItem = set.getItem(current.getInventory().getItemInMainHand());
				CustomItem offItem = set.getItem(current.getInventory().getItemInOffHand());
				
				if (data.shootIfAllowed(mainItem, currentTick)) {
					fire(current, data, mainItem, current.getInventory().getItemInMainHand());
				}
				if (data.shootIfAllowed(offItem, currentTick)) {
					fire(current, data, offItem, current.getInventory().getItemInOffHand());
				}
			} else {
				iterator.remove();
			}
		}
	}
	
	private void updateContainers() {
		persistentContainers.values().forEach(ContainerInstance::update);
		tempContainers.forEach(temp -> temp.instance.update());
	}
	
	private void quickClean() {
		Iterator<TempContainerInstance> tempIterator = tempContainers.iterator();
		while (tempIterator.hasNext()) {
			TempContainerInstance tempInstance = tempIterator.next();
			if (tempInstance.viewer.getOpenInventory() != tempInstance.instance.getInventory()) {
				tempIterator.remove();
				tempInstance.instance.dropAllItems(tempInstance.viewer.getLocation());
				Bukkit.broadcastMessage("Closed temp container session");
				// TODO Ehm... why is this still here?
			}
		}
	}
	
	private void clean() {
		Iterator<Entry<UUID, PlayerData>> it = playerData.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getValue().clean(currentTick)) {
				it.remove();
			}
		}
		cleanEmptyContainers();
	}
	
	private void fire(Player player, PlayerData data, CustomItem weapon, ItemStack weaponStack) {
		if (weapon instanceof CustomWand) {
			CustomWand wand = (CustomWand) weapon;
			
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
			
			for (int counter = 0; counter < wand.amountPerShot; counter++)
				CustomItemsPlugin.getInstance().getProjectileManager().fireProjectile(player, wand.projectile);
		}
		// TODO Add a clause for CustomGun, once it's added
	}
	
	/**
	 * Saves the data such that a call to loadData() will return a PluginData with the same data.
	 * 
	 * This method should be called in the onDisable() of CustomItemsPlugin, but could be called on 
	 * additional moments.
	 */
	public void saveData() {
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		output.addByte(ENCODING_2);
		save3(output);
		try {
			OutputStream fileOutput = Files.newOutputStream(getDataFile().toPath());
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException io) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save the CustomItems data", io);
		}
	}
	
	private void save1(BitOutput output) {
		output.addLong(currentTick);
		
		output.addInt(playerData.size());
		for (Entry<UUID,PlayerData> entry : playerData.entrySet()) {
			output.addLong(entry.getKey().getMostSignificantBits());
			output.addLong(entry.getKey().getLeastSignificantBits());
			entry.getValue().save1(output, currentTick);
		}
	}
	
	private void cleanEmptyContainers() {
		
		// Clean up any empty custom containers
		Iterator<Entry<ContainerLocation, ContainerInstance>> entryIterator = persistentContainers.entrySet().iterator();
		entryLoop:
		while (entryIterator.hasNext()) {
			
			Entry<ContainerLocation, ContainerInstance> entry = entryIterator.next();
			ContainerInstance instance = entry.getValue();
			
			// Don't close it if anyone is still viewing it
			if (!instance.getInventory().getViewers().isEmpty()) {
				continue;
			}
			
			// Check if its still burning or still has some crafting progress
			if (instance.getCurrentCraftingProgress() != 0 || instance.isAnySlotBurning()) {
				continue;
			}
			
			// Check if any of its input/output/fuel slots is non-empty
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < instance.getType().getHeight(); y++) {

					CustomSlot slot = instance.getType().getSlot(x, y);
					if (slot instanceof InputCustomSlot || slot instanceof OutputCustomSlot || slot instanceof FuelCustomSlot || slot instanceof StorageCustomSlot) {

						int invIndex = x + 9 * y;
						if (!ItemUtils.isEmpty(instance.getInventory().getItem(invIndex))) {
							continue entryLoop;
						}
					}

					// TODO Test this
				}
			}

			// TODO Add something similar for pocket containers
		}
	}
	
	private void save2(BitOutput output) {
		save1(output);
		
		cleanEmptyContainers();
		output.addInt(persistentContainers.size());
		for (Entry<ContainerLocation, ContainerInstance> entry : persistentContainers.entrySet()) {
			
			// Save container location
			ContainerLocation loc = entry.getKey();
			output.addLong(loc.location.getWorldId().getMostSignificantBits());
			output.addLong(loc.location.getWorldId().getLeastSignificantBits());
			output.addInts(loc.location.getX(), loc.location.getY(), loc.location.getZ());
			output.addString(loc.type.getName());
			
			// Save container state
			ContainerInstance state = entry.getValue();
			state.save1(output);
		}
	}

	private void save3(BitOutput output) {
		save1(output);

		cleanEmptyContainers();
		output.addInt(persistentContainers.size());
		for (Entry<ContainerLocation, ContainerInstance> entry : persistentContainers.entrySet()) {

			// Save container location
			ContainerLocation loc = entry.getKey();
			output.addLong(loc.location.getWorldId().getMostSignificantBits());
			output.addLong(loc.location.getWorldId().getLeastSignificantBits());
			output.addInts(loc.location.getX(), loc.location.getY(), loc.location.getZ());
			output.addString(loc.type.getName());

			// Save container state
			ContainerInstance state = entry.getValue();
			state.save2(output);
		}
	}

	/**
	 * Sets the given player in the so-called shooting state for the next 10 ticks (a half second). If the
	 * player is already in the shooting state, nothing will happen. The player will leave the shooting state
	 * if this method is not called again within 10 ticks after this call.
	 * 
	 * @param player The player that wants to start shooting
	 */
	public void setShooting(Player player) {
		getPlayerData(player).setShooting(currentTick);
		if (!shootingPlayers.contains(player)) {
			shootingPlayers.add(player);
		}
	}
	
	private ContainerInfo infoFor(CustomContainer container) {
		return CustomItemsPlugin.getInstance().getSet().getContainerInfo(container);
	}
	
	private PlayerData getPlayerData(Player player) {
		PlayerData data = playerData.get(player.getUniqueId());
		if (data == null) {
			data = new PlayerData();
			playerData.put(player.getUniqueId(), data);
		}
		return data;
	}
	
	/**
	 * @return The number of ticks passed since the first use of this plug-in of at least version 6.0
	 */
	public long getCurrentTick() {
		return currentTick;
	}
	
	public ContainerInstance getCustomContainer(Player viewer) {
		
		// Check temp sessions
		for (TempContainerInstance temp : tempContainers) {
			if (temp.instance.getInventory().getViewers().contains(viewer)) {
				return temp.instance;
			}
		}
		
		for (ContainerInstance persistent : persistentContainers.values()) {
			if (persistent.getInventory().getViewers().contains(viewer)) {
				return persistent;
			}
		}
		
		return null;
	}
	
	public ContainerInstance getCustomContainer(Location location, Player newViewer, CustomContainer prototype) {
		
		/*
		 * There are 2 kinds of custom containers: those with persistent storage
		 * that is shared between players and saved when the server stops (for
		 * instance furnaces). And there are those without persistent storage that
		 * allocate space when the player opens it and drops all items when the
		 * player closes it (like crafting table).
		 */
		if (prototype.hasPersistentStorage()) {
			
			// Container storage is shared between players, so we need to check if
			// there is an existing inventory
			ContainerLocation key = new ContainerLocation(location, prototype);
			ContainerInstance instance = persistentContainers.get(key);
			if (instance == null) {
				instance = new ContainerInstance(infoFor(prototype));
				persistentContainers.put(key, instance);
			}
			return instance;
		} else {
			
			// Not shared between players, so just create a new instance
			TempContainerInstance tempInstance = new TempContainerInstance(
					new ContainerInstance(infoFor(prototype)), newViewer
			);
			tempContainers.add(tempInstance);
			return tempInstance.instance;
		}
	}
	
	public Inventory getCustomContainerMenu(
			Location location, Player player, VanillaContainerType containerType
	) {
		
		if (containerType == null) {
			return null;
		}
		
		List<CustomContainer> correspondingContainers = containerTypeMap.get(containerType);
		if (correspondingContainers.isEmpty()) {
			return null;
		} else if (correspondingContainers.size() == 1) {
			return getCustomContainer(
					location, player, correspondingContainers.get(0)
			).getInventory();
		} else {
			PlayerData pd = getPlayerData(player);
			pd.containerSelectionLocation = new PassiveLocation(location);
			return containerSelectionMap.get(containerType);
		}
	}
	
	public List<CustomContainer> getCustomContainerSelection(HumanEntity player) {
		for (Entry<VanillaContainerType, Inventory> entry : containerSelectionMap.entrySet()) {
			if (entry.getValue().getViewers().contains(player)) {
				return containerTypeMap.get(entry.getKey());
			}
		}
		
		return null;
	}
	
	public void selectCustomContainer(Player player, CustomContainer selected) {
		PlayerData pd = getPlayerData(player);
		if (pd.containerSelectionLocation == null) {
			throw new IllegalStateException("Player " + player + " hasn't opened any container selection");
		}
		
		Location containerLocation = pd.containerSelectionLocation.toBukkitLocation();
		pd.containerSelectionLocation = null;
		CIMaterial blockMaterial = CIMaterial.valueOf(
				ItemHelper.getMaterialName(containerLocation.getBlock())
		);
		VanillaContainerType vanillaType = VanillaContainerType.fromMaterial(blockMaterial);
		
		/*
		 * It may happen that a player opens the container selection, but that the
		 * block is broken before the player makes his choice. That situation would
		 * cause a somewhat corrupted state, which is avoided by simply closing the
		 * players inventory.
		 */
		if (vanillaType == selected.getVanillaType()) {
			player.openInventory(getCustomContainer(
					containerLocation, player, selected
			).getInventory());
		} else {
			player.closeInventory();
		}
	}
	
	public void clearContainerSelectionLocation(Player player) {
		getPlayerData(player).containerSelectionLocation = null;
	}
	
	public void destroyCustomContainersAt(Location location) {
		
		Iterator<Entry<ContainerLocation, ContainerInstance>> persistentIterator = 
				persistentContainers.entrySet().iterator();
		PassiveLocation passiveLocation = new PassiveLocation(location);
		
		while (persistentIterator.hasNext()) {
			Entry<ContainerLocation, ContainerInstance> entry = persistentIterator.next();
			
			// Only the containers at this exact location are affected
			if (passiveLocation.equals(entry.getKey().location)) {
				
				// Scan over all slots that the players can access in any way
				entry.getValue().dropAllItems(location);
				new ArrayList<>(entry.getValue().getInventory().getViewers()).forEach(HumanEntity::closeInventory);
				persistentIterator.remove();
			}
		}
		
		for (Entry<UUID,PlayerData> playerEntry : playerData.entrySet()) {
			PlayerData pd = playerEntry.getValue();
			if (passiveLocation.equals(pd.containerSelectionLocation)) {
				pd.containerSelectionLocation = null;
				Player player = Bukkit.getPlayer(playerEntry.getKey());
				if (player != null) {
					player.closeInventory();
				}
			}
		}
	}
	
	private static class ContainerLocation {
		
		final PassiveLocation location;
		
		final CustomContainer type;
		
		ContainerLocation(PassiveLocation location, CustomContainer type){
			this.location = location;
			this.type = type;
			if (type == null) throw new NullPointerException("type");
			if (location == null) throw new NullPointerException("location");
		}
		
		ContainerLocation(Location location, CustomContainer type) {
			this(new PassiveLocation(location), type);
		}
		
		@Override
		public int hashCode() {
			return 17 * location.hashCode() - 71 * type.getName().hashCode();
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof ContainerLocation) {
				ContainerLocation loc = (ContainerLocation) other;
				return loc.type.getName().equals(type.getName()) && 
						loc.location.equals(location);
			} else {
				return false;
			}
		}
	}
	
	private static class TempContainerInstance {
		
		final ContainerInstance instance;
		final Player viewer;
		
		TempContainerInstance(ContainerInstance instance, Player viewer) {
			this.instance = instance;
			this.viewer = viewer;
		}
	}
	
	private static class CarefulPluginData extends PluginData {
		
		@Override
		public void saveData() {
			File dataFile = getDataFile();
			if (dataFile.exists()) {
				super.saveData();
			} else {
				Bukkit.getLogger().warning("The CustomItems data wasn't saved to protect the original data");
			}
		}
	}
}
