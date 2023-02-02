package nl.knokko.customitems.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.ContainerStorageMode;
import nl.knokko.customitems.container.CustomContainerHost;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.encoding.ContainerEncoding;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.util.SoundPlayer;
import nl.knokko.customitems.plugin.multisupport.worldguard.WorldGuardSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.set.item.CustomGunWrapper;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptItemStack;

public class PluginData {
	
	private static final byte ENCODING_1 = 1;
	private static final byte ENCODING_2 = 2;
	private static final byte ENCODING_3 = 3;
	private static final byte ENCODING_4 = 4;
	private static final byte ENCODING_5 = 5;
	
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
	public static PluginData loadData(ItemSetWrapper itemSet) {
		File dataFile = getDataFile();
		if (dataFile.exists()) {
			try {
				BitInput input = ByteArrayBitInput.fromFile(dataFile);
				
				byte encoding = input.readByte();
				switch (encoding) {
					case ENCODING_1:
						return load1(input, itemSet);
					case ENCODING_2:
						return load2(input, itemSet);
					case ENCODING_3:
						return load3(input, itemSet);
					case ENCODING_4:
						return load4(input, itemSet);
					case ENCODING_5:
						return load5(input, itemSet);
					default:
						throw new IllegalArgumentException("Unknown data encoding: " + encoding);
				}
			} catch (IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Failed to open the data file for CustomItems", e);
				Bukkit.getLogger().severe("The current data for CustomItems won't be overwritten when you stop the server.");
				return new CarefulPluginData(itemSet);
			} catch (UnknownEncodingException outdated) {
				Bukkit.getLogger().log(Level.SEVERE, "Failed to open the data file for CustomItems because it was created by a newer version of the plug-in.");
				Bukkit.getLogger().severe("The current data for CustomItems won't be overwritten when you stop the server.");
				return new CarefulPluginData(itemSet);
			}
		} else {
			Bukkit.getLogger().warning("Couldn't find the data file for CustomItems. Is this the first time you are using CustomItems with version at least 6.0?");
			return new PluginData(itemSet);
		}
	}
	
	private static Map<UUID, PlayerData> loadPlayerData1(BitInput input, ItemSetWrapper set) {
		int numPlayers = input.readInt();
		Map<UUID,PlayerData> playersMap = new HashMap<>(numPlayers);
		for (int counter = 0; counter < numPlayers; counter++) {
			UUID id = new UUID(input.readLong(), input.readLong());
			PlayerData data = PlayerData.load1(input, set, Bukkit.getLogger());
			playersMap.put(id, data);
		}
		
		return playersMap;
	}

	private static Map<UUID, PlayerData> loadPlayerData2(BitInput input, ItemSetWrapper set) throws UnknownEncodingException {
		int numPlayers = input.readInt();
		Map<UUID,PlayerData> playersMap = new HashMap<>(numPlayers);
		for (int counter = 0; counter < numPlayers; counter++) {
			UUID id = new UUID(input.readLong(), input.readLong());
			PlayerData data = PlayerData.load2(input, set, Bukkit.getLogger());
			playersMap.put(id, data);
		}

		return playersMap;
	}
	
	private static PluginData load1(BitInput input, ItemSetWrapper itemSet) {
		long currentTick = input.readLong();

		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, itemSet);
		
		// There were no persistent containers in this version
		return new PluginData(itemSet, currentTick, playersMap, new HashMap<>(), new StoredEnergy());
	}
	
	private static PluginData load2(BitInput input, ItemSetWrapper itemSet) {
		long currentTick = input.readLong();

		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, itemSet);
		
		int numPersistentContainers = input.readInt();
		Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

		StoredEnergy storedEnergy = new StoredEnergy();

		for (int counter = 0; counter < numPersistentContainers; counter++) {
			
			UUID worldId = new UUID(input.readLong(), input.readLong());
			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();
			String typeName = input.readString();
			
			ContainerInfo typeInfo = itemSet.getContainerInfo(typeName);
			
			if (typeInfo != null) {
				ContainerStorageKey location = new ContainerStorageKey(typeName, new PassiveLocation(worldId, x, y, z), null, null);
				ContainerInstance instance = ContainerInstance.load1(input, typeInfo, null, location, storedEnergy, new ArrayList<>());
				persistentContainers.put(location, instance);
			} else {
				ContainerInstance.discard1(input);
			}
		}
		
		return new PluginData(itemSet, currentTick, playersMap, persistentContainers, storedEnergy);
	}

	private static PluginData load3(BitInput input, ItemSetWrapper itemSet) {
		long currentTick = input.readLong();

		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, itemSet);

		int numPersistentContainers = input.readInt();
		Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

		StoredEnergy storedEnergy = new StoredEnergy();
		for (int counter = 0; counter < numPersistentContainers; counter++) {

			UUID worldId = new UUID(input.readLong(), input.readLong());
			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();
			String typeName = input.readString();

			ContainerInfo typeInfo = itemSet.getContainerInfo(typeName);

			if (typeInfo != null) {
				ContainerStorageKey location = new ContainerStorageKey(typeName, new PassiveLocation(worldId, x, y, z), null, null);
				ContainerInstance instance = ContainerInstance.load2(input, typeInfo, null, location, storedEnergy);
				persistentContainers.put(location, instance);
			} else {
				ContainerInstance.discard2(input);
			}
		}

		return new PluginData(itemSet, currentTick, playersMap, persistentContainers, storedEnergy);
	}

	private static PluginData load4(BitInput input, ItemSetWrapper itemSet) throws UnknownEncodingException {
		long currentTick = input.readLong();

		Map<UUID, PlayerData> playersMap = loadPlayerData2(input, itemSet);

		int numPersistentContainers = input.readInt();
		Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

		StoredEnergy storedEnergy = new StoredEnergy();
		for (int counter = 0; counter < numPersistentContainers; counter++) {

			UUID worldId = new UUID(input.readLong(), input.readLong());
			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();
			String typeName = input.readString();

			ContainerInfo typeInfo = itemSet.getContainerInfo(typeName);

			if (typeInfo != null) {
				ContainerStorageKey location = new ContainerStorageKey(typeName, new PassiveLocation(worldId, x, y, z), null, null);
				ContainerInstance instance = ContainerInstance.load2(input, typeInfo, null, location, storedEnergy);
				persistentContainers.put(location, instance);
			} else {
				ContainerInstance.discard2(input);
			}
		}

		return new PluginData(itemSet, currentTick, playersMap, persistentContainers, storedEnergy);
	}

	private static PluginData load5(BitInput input, ItemSetWrapper itemSet) throws UnknownEncodingException {
		long currentTick = input.readLong();

		Map<UUID, PlayerData> playersMap = loadPlayerData2(input, itemSet);

		StoredEnergy storedEnergy = StoredEnergy.load(input);

		int numPersistentContainers = input.readInt();
		Map<ContainerStorageKey, ContainerInstance> persistentContainers = new HashMap<>(numPersistentContainers);

		for (int counter = 0; counter < numPersistentContainers; counter++) {

			ContainerStorageKey storageKey = ContainerStorageKey.load(input);
			ContainerInfo typeInfo = itemSet.getContainerInfo(storageKey.containerName);

			if (typeInfo != null) {
				ContainerInstance instance = ContainerInstance.load3(
						input, typeInfo, storageKey.playerID, storageKey, storedEnergy
				);
				persistentContainers.put(storageKey, instance);
			} else {
				ContainerInstance.discard3(input);
			}
		}


		return new PluginData(itemSet, currentTick, playersMap, persistentContainers, storedEnergy);
	}

	// Persisting data
	private final Map<UUID,PlayerData> playerData;
	private final Map<ContainerStorageKey, ContainerInstance> persistentContainers;
	private final StoredEnergy storedEnergy;

	private long currentTick;
	
	// Non-persisting data
	private final ItemSetWrapper itemSet;
	private Collection<TempContainerInstance> tempContainers;
	private List<Player> shootingPlayers;
	private List<Player> eatingPlayers;
	private Map<CustomContainerHost, Inventory> containerSelectionMap;
	private Map<String, Inventory> pocketContainerSelectionMap;

	private PluginData(ItemSetWrapper itemSet) {
		this.itemSet = itemSet;
		playerData = new HashMap<>();
		persistentContainers = new HashMap<>();
		storedEnergy = new StoredEnergy();
		currentTick = 0;
		
		init();
	}
	
	private PluginData(ItemSetWrapper itemSet, long currentTick, Map<UUID,PlayerData> playerData,
			Map<ContainerStorageKey, ContainerInstance> persistentContainers, StoredEnergy storedEnergy) {
		this.itemSet = itemSet;
		this.playerData = playerData;
		this.persistentContainers = persistentContainers;
		this.storedEnergy = storedEnergy;
		this.currentTick = currentTick;
		
		init();
	}
	
	private void init() {
		tempContainers = new LinkedList<>();
		shootingPlayers = new LinkedList<>();
		eatingPlayers = new LinkedList<>();
		initContainerTypeMap();
		initPocketContainerMap();
		
		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::update, 1, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::quickClean, 50, 10);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::clean, 200, 40);
	}
	
	private void initContainerTypeMap() {
		containerSelectionMap = new HashMap<>();
		for (Map.Entry<CustomContainerHost, List<CustomContainerValues>> hostEntry : itemSet.getContainerHostMap().entrySet()) {
			Collection<CustomContainerValues> visibleContainers = hostEntry.getValue().stream().filter(
					candidate -> !candidate.isHidden()
			).collect(Collectors.toList());
			if (visibleContainers.size() > 1) {
				containerSelectionMap.put(hostEntry.getKey(), createContainerSelectionMenu(visibleContainers));
			}
		}
	}

	private void initPocketContainerMap() {
		pocketContainerSelectionMap = new HashMap<>();

		for (CustomItemValues item : itemSet.get().getItems()) {
			if (item instanceof CustomPocketContainerValues) {
				CustomPocketContainerValues pocketContainer = (CustomPocketContainerValues) item;
				Collection<CustomContainerValues> visibleContainers = pocketContainer.getContainers().stream().filter(
						candidate -> !candidate.isHidden()
				).collect(Collectors.toList());
				if (visibleContainers.size() > 1) {
					pocketContainerSelectionMap.put(
							item.getName(),
							createContainerSelectionMenu(new ArrayList<>(visibleContainers))
					);
				}
			}
		}
	}

	private boolean hasPermission(Player player, CustomContainerValues container) {
		return !container.requiresPermission() || player.hasPermission("customitems.container.openany") ||
				player.hasPermission("customitems.container.open." + container.getName());
	}

	private Collection<CustomContainerValues> getContainersToChooseFrom(
			Player player, Collection<CustomContainerValues> candidates
	) {
		return candidates.stream().filter(
				candidate -> hasPermission(player, candidate) && !candidate.isHidden()
		).collect(Collectors.toList());
	}
	
	private Inventory createContainerSelectionMenu(
			Collection<CustomContainerValues> containers) {
		int invSize = 1 + containers.size();
		if (invSize % 9 != 0) {
			invSize = 9 + 9 * (invSize / 9);
		}
		
		Inventory menu = Bukkit.createInventory(null, invSize, "Choose custom container");
		{
			ItemStack cancelStack = KciNms.instance.items.createStack(CIMaterial.BARRIER.name(), 1);
			ItemMeta meta = cancelStack.getItemMeta();
			meta.setDisplayName("Cancel");
			cancelStack.setItemMeta(meta);
			menu.setItem(0, cancelStack);
		}

		int listIndex = 0;
		for (CustomContainerValues container : containers) {
			menu.setItem(listIndex + 1, ContainerInstance.fromDisplay(container.getSelectionIcon()));
			listIndex++;
		}
		
		return menu;
	}
	
	private void update() {
		currentTick++;
		
		updateShooting();
		updateContainers();
		handleClosedPocketContainers();
		manageReloadingGuns();
		updateEating();
	}
	
	private void updateShooting() {
		Iterator<Player> iterator = shootingPlayers.iterator();
		while (iterator.hasNext()) {
			Player current = iterator.next();
			PlayerData data = getPlayerData(current);
			if (data.isShooting(currentTick)) {
				CustomItemValues mainItem = itemSet.getItem(current.getInventory().getItemInMainHand());
				CustomItemValues offItem = itemSet.getItem(current.getInventory().getItemInOffHand());
				
				if (hasPermissionToShoot(current, mainItem) && data.shootIfAllowed(mainItem, currentTick, true)) {
					fire(current, data, mainItem, current.getInventory().getItemInMainHand(), true);
				}
				if (hasPermissionToShoot(current, offItem) && data.shootIfAllowed(offItem, currentTick, false)) {
					fire(current, data, offItem, current.getInventory().getItemInOffHand(), false);
				}
			} else {
				iterator.remove();
			}
		}
	}

	private void updateEating() {

		Iterator<Player> iterator = eatingPlayers.iterator();
		while (iterator.hasNext()) {
		    Player player = iterator.next();
		    PlayerData pd = getPlayerData(player);

			if (pd.isEating(currentTick)) {

				ItemStack mainItemStack = player.getInventory().getItemInMainHand();
				ItemStack offItemStack = player.getInventory().getItemInOffHand();
				CustomItemValues mainItem = itemSet.getItem(mainItemStack);
				CustomItemValues offItem = itemSet.getItem(offItemStack);

				if (mainItem instanceof CustomFoodValues) {

					CustomFoodValues mainFood = (CustomFoodValues) mainItem;
					if (mainFood != pd.mainhandFood) {
						if (!mainFood.getEatEffects().isEmpty() || player.getFoodLevel() < 20 || mainFood.getFoodValue() < 0) {
							pd.mainhandFood = mainFood;
							pd.startMainhandEatTime = currentTick;
						} else {
							pd.mainhandFood = null;
							pd.startMainhandEatTime = -1;
						}
					}

					if (pd.mainhandFood != null) {
						long elapsedTime = currentTick - pd.startMainhandEatTime;
						if (elapsedTime % mainFood.getSoundPeriod() == 0) {
							SoundPlayer.playSound(player, mainFood.getEatSound());
						}

						if (elapsedTime >= mainFood.getEatTime()) {
							player.setFoodLevel(player.getFoodLevel() + mainFood.getFoodValue());
							mainFood.getEatEffects().forEach(eatEffect ->
									player.addPotionEffect(new PotionEffect(
											PotionEffectType.getByName(eatEffect.getType().name()),
											eatEffect.getDuration(),
											eatEffect.getLevel() - 1
									))
							);
							mainItemStack.setAmount(mainItemStack.getAmount() - 1);
							player.getInventory().setItemInMainHand(mainItemStack);
							pd.mainhandFood = null;
							pd.startMainhandEatTime = -1;
						}
					}
				} else {
					pd.mainhandFood = null;
					pd.startMainhandEatTime = -1;
				}

				if (offItem instanceof CustomFoodValues) {

					CustomFoodValues offFood = (CustomFoodValues) offItem;
					if (pd.offhandFood != offFood) {
						if (!offFood.getEatEffects().isEmpty() || player.getFoodLevel() < 20 || offFood.getFoodValue() < 0) {
							pd.offhandFood = offFood;
							pd.startOffhandEatTime = currentTick;
						} else {
							pd.offhandFood = null;
							pd.startOffhandEatTime = -1;
						}
					}

					if (pd.offhandFood != null) {
						long elapsedTime = currentTick - pd.startOffhandEatTime;
						if (elapsedTime % offFood.getSoundPeriod() == 0) {
							SoundPlayer.playSound(player, offFood.getEatSound());
						}

						if (elapsedTime >= offFood.getEatTime()) {
							player.setFoodLevel(player.getFoodLevel() + offFood.getFoodValue());
							offFood.getEatEffects().forEach(eatEffect ->
									player.addPotionEffect(new PotionEffect(
											PotionEffectType.getByName(eatEffect.getType().name()),
											eatEffect.getDuration(),
											eatEffect.getLevel() - 1
									))
							);
							offItemStack.setAmount(offItemStack.getAmount() - 1);
							player.getInventory().setItemInOffHand(offItemStack);
							pd.offhandFood = null;
							pd.startOffhandEatTime = -1;
						}
					}
				} else {
					pd.offhandFood = null;
					pd.startOffhandEatTime = -1;
				}
			} else {
			    pd.mainhandFood = null;
			    pd.offhandFood = null;
			    pd.startMainhandEatTime = -1;
			    pd.startOffhandEatTime = -1;
				iterator.remove();
			}
        }
	}

	private void manageReloadingGuns() {
		playerData.forEach((playerId, data) -> {

			// Check if we need to reload the gun in the main hand
			if (data.mainhandGunToReload != null && currentTick >= data.finishMainhandGunReloadTick) {

				CustomGunValues gun = data.mainhandGunToReload;
				Player player = Bukkit.getPlayer(playerId);
				if (player != null) {

					ItemStack currentItem = player.getInventory().getItemInMainHand();
					CustomItemValues currentCustomItem = itemSet.getItem(currentItem);
					if (currentCustomItem == gun) {
						if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

							IndirectGunAmmoValues indirectAmmo = (IndirectGunAmmoValues) gun.getAmmo();
							if (checkAmmo(player.getInventory(), indirectAmmo.getReloadItem(), true)) {
								player.getInventory().setItemInMainHand(new CustomGunWrapper(gun).reload(currentItem));
								if (indirectAmmo.getEndReloadSound() != null) {
									SoundPlayer.playSound(player, indirectAmmo.getEndReloadSound());
								}
							}
						} else {
							throw new Error("Unsupported indirect ammo: " + gun.getAmmo().getClass());
						}
					}
				}

				data.mainhandGunToReload = null;
				data.finishMainhandGunReloadTick = -1;
			}

			// Check if we need to reload the gun in the off hand
			if (data.offhandGunToReload != null && currentTick >= data.finishOffhandGunReloadTick) {

				CustomGunValues gun = data.offhandGunToReload;
				Player player = Bukkit.getPlayer(playerId);
				if (player != null) {

					ItemStack currentItem = player.getInventory().getItemInOffHand();
					CustomItemValues currentCustomItem = itemSet.getItem(currentItem);
					if (currentCustomItem == gun) {
						if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

							IndirectGunAmmoValues indirectAmmo = (IndirectGunAmmoValues) gun.getAmmo();
							if (checkAmmo(player.getInventory(), indirectAmmo.getReloadItem(), true)) {
                                player.getInventory().setItemInOffHand(new CustomGunWrapper(gun).reload(currentItem));
                                if (indirectAmmo.getEndReloadSound() != null) {
                                	SoundPlayer.playSound(player, indirectAmmo.getEndReloadSound());
								}

							}
						} else {
							throw new Error("Unsupported indirect ammo: " + gun.getAmmo().getClass());
						}
					}
				}

				data.offhandGunToReload = null;
				data.finishOffhandGunReloadTick = -1;
			}
		});
	}
	
	private void updateContainers() {
		persistentContainers.values().forEach(ContainerInstance::update);
		tempContainers.forEach(temp -> temp.instance.update());
		playerData.values().forEach(pd -> {
			if (pd.openPocketContainer != null) {
				pd.openPocketContainer.update();
			}
		});
	}
	
	private void quickClean() {
		Iterator<TempContainerInstance> tempIterator = tempContainers.iterator();
		while (tempIterator.hasNext()) {
			TempContainerInstance tempInstance = tempIterator.next();
			if (!tempInstance.viewer.getOpenInventory().getTopInventory().equals(tempInstance.instance.getInventory())) {
				tempIterator.remove();
				tempInstance.instance.dropAllItems(tempInstance.viewer.getLocation());
			}
		}
	}

	public PlayerWandInfo getWandInfo(Player player, CustomWandValues wand) {
		PlayerData targetPlayerData = playerData.get(player.getUniqueId());
		if (targetPlayerData != null) {
		    PlayerWandData wandData = targetPlayerData.wandsData.get(wand);
		    if (wandData != null) {
		    	return new PlayerWandInfo(
		    			wandData.getRemainingCooldown(currentTick),
						wandData.getCurrentCharges(wand, currentTick),
						wandData.getTimeUntilNextRecharge(wand, currentTick)
				);
			}
		}

		return null;
	}

	public PlayerGunInfo getGunInfo(Player player, CustomGunValues gun, ItemStack gunStack, boolean isMainhand) {
		PlayerData targetPlayerData = playerData.get(player.getUniqueId());

		if (gun.getAmmo() instanceof DirectGunAmmoValues) {

			if (targetPlayerData != null) {
				if (isMainhand) {
					if (targetPlayerData.nextMainhandGunShootTick > currentTick) {
						return PlayerGunInfo.directCooldown(targetPlayerData.nextMainhandGunShootTick - currentTick);
					} else {
						return null;
					}
				} else {
					if (targetPlayerData.nextOffhandGunShootTick > currentTick) {
						return PlayerGunInfo.directCooldown(targetPlayerData.nextOffhandGunShootTick - currentTick);
					} else {
						return null;
					}
				}
			} else {
				return null;
			}
		} else if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

			if (targetPlayerData != null) {
				if (isMainhand) {
					if (targetPlayerData.mainhandGunToReload == gun && targetPlayerData.finishMainhandGunReloadTick > currentTick) {
						return PlayerGunInfo.indirectReloading((int) (targetPlayerData.finishMainhandGunReloadTick - currentTick));
					} else {
						long remainingCooldown = 0;
						if (targetPlayerData.nextMainhandGunShootTick > currentTick) {
							remainingCooldown = targetPlayerData.nextMainhandGunShootTick - currentTick;
						}

						return PlayerGunInfo.indirect(remainingCooldown, new CustomGunWrapper(gun).getCurrentAmmo(gunStack));
					}
				} else {
					if (targetPlayerData.offhandGunToReload == gun && targetPlayerData.finishOffhandGunReloadTick > currentTick) {
						return PlayerGunInfo.indirectReloading((int) (targetPlayerData.finishOffhandGunReloadTick - currentTick));
					} else {
						long remainingCooldown = 0;
						if (targetPlayerData.nextOffhandGunShootTick > currentTick) {
							remainingCooldown = targetPlayerData.nextOffhandGunShootTick - currentTick;
						}

						return PlayerGunInfo.indirect(remainingCooldown, new CustomGunWrapper(gun).getCurrentAmmo(gunStack));
					}
				}
			} else {
				return PlayerGunInfo.indirect(0, new CustomGunWrapper(gun).getCurrentAmmo(gunStack));
			}
		} else {
			throw new Error("Unknown ammo system: " + gun.getAmmo().getClass());
		}
	}

	public boolean isOnCooldown(Player player, CustomItemValues customItem, ItemCommandEvent event, int commandIndex) {
		PlayerData pd = playerData.get(player.getUniqueId());
		return pd != null && pd.commandCooldowns.isOnCooldown(customItem, event, commandIndex, currentTick);
	}

	public void setOnCooldown(Player player, CustomItemValues customItem, ItemCommandEvent event, int commandIndex) {
		getPlayerData(player).commandCooldowns.setOnCooldown(customItem, event, commandIndex, currentTick);
	}

	public void onPlayerQuit(Player player) {
		PlayerData pd = playerData.get(player.getUniqueId());
		if (pd != null) {
			pd.pocketContainerSelection = false;
			pd.containerSelectionLocation = null;

			if (pd.openPocketContainer != null) {
				maybeClosePocketContainer(pd, player, true);
			}
		}

		Iterator<TempContainerInstance> tempIterator = tempContainers.iterator();
		while (tempIterator.hasNext()) {
			TempContainerInstance tempInstance = tempIterator.next();
			if (tempInstance.viewer.getUniqueId().equals(player.getUniqueId())) {
				tempIterator.remove();
				tempInstance.instance.dropAllItems(tempInstance.viewer.getLocation());
			}
		}
	}

	private void maybeClosePocketContainer(PlayerData pd, Player player, boolean force) {

		PlayerInventory inv = player.getInventory();

		boolean closeContainerInv = false;
		ItemStack closeDestination = null;
		boolean putBackInMainHand = false;

		if (pd.pocketContainerInMainHand) {
			ItemStack mainItem = inv.getItemInMainHand();
			if (!(itemSet.getItem(mainItem) instanceof CustomPocketContainerValues)) {
				closeContainerInv = true;
			} else if (!pd.openPocketContainer.getInventory().getViewers().contains(player) || force) {
				closeContainerInv = true;
				closeDestination = mainItem;
				putBackInMainHand = true;
			}
		} else {
			ItemStack offItem = inv.getItemInOffHand();
			if (!(itemSet.getItem(offItem) instanceof CustomPocketContainerValues)) {
				closeContainerInv = true;
			} else if (!pd.openPocketContainer.getInventory().getViewers().contains(player) || force) {
				closeContainerInv = true;
				closeDestination = offItem;
			}
		}

		if (closeContainerInv) {

			ContainerStorageMode storageMode = pd.openPocketContainer.getType().getStorageMode();

			// If the storage mode doesn't depend on the location, we shouldn't store it
			// If the storage mode is not persistent, we should only store the energy
			if (storageMode == ContainerStorageMode.GLOBAL || storageMode == ContainerStorageMode.PER_PLAYER) {
				closeDestination = null;
			}

			if (closeDestination != null) {

				CustomPocketContainerValues pocketContainer = (CustomPocketContainerValues) itemSet.getItem(closeDestination);
				boolean acceptsCurrentContainer = false;
				for (CustomContainerValues candidate : pocketContainer.getContainers()) {
					if (candidate == pd.openPocketContainer.getType()) {
						acceptsCurrentContainer = true;
						break;
					}
				}

				if (acceptsCurrentContainer) {
					String[] nbtKey = storageMode != ContainerStorageMode.NOT_PERSISTENT ? getPocketContainerNbtKey(pd.openPocketContainer.getType(), player) : null;
					GeneralItemNBT destNbt = KciNms.instance.items.generalReadWriteNbt(closeDestination);

					if (storageMode != ContainerStorageMode.NOT_PERSISTENT && destNbt.getOrDefault(nbtKey, null) != null) {
						// Don't overwrite the contents of another pocket container
						// (This can happen in some edge case where the pocket container in the hand
						// is replaced with another pocket container.)
						// To handle such cases, we drop all items on the floor rather than storing them.
						storageMode = ContainerStorageMode.NOT_PERSISTENT;
					} else {

						// When the storage is not persistent, we should only store the energy
						if (storageMode != ContainerStorageMode.NOT_PERSISTENT) {
							ByteArrayBitOutput containerStateOutput = new ByteArrayBitOutput();
							containerStateOutput.addByte(ContainerEncoding.ENCODING_3);
							pd.openPocketContainer.save3(containerStateOutput);
							destNbt.set(nbtKey, new String(StringEncoder.encodeTextyBytes(
									containerStateOutput.getBytes(),
									false), StandardCharsets.US_ASCII)
							);
						}

						ByteArrayBitOutput storedEnergyOutput = new ByteArrayBitOutput();
						if (pd.openPocketContainer.storedEnergy instanceof PocketStoredEnergy) {
							pd.openPocketContainer.storedEnergy.save(storedEnergyOutput);
							destNbt.set(POCKET_CONTAINER_ENERGY_KEY, new String(StringEncoder.encodeTextyBytes(
									storedEnergyOutput.getBytes(), false
							), StandardCharsets.US_ASCII));
						} else {
							Bukkit.getLogger().severe("Stored energy of a pocket container is NOT of type PocketStoredEnergy");
						}

						if (putBackInMainHand) {
							inv.setItemInMainHand(destNbt.backToBukkit());
						} else {
							inv.setItemInOffHand(destNbt.backToBukkit());
						}
					}
				} else {

					// Don't store the pocket container data in a pocket container that doesn't accept
					// this type of container. This can happen in some edge cases where the pocket
					// container in the hand is replaced with another kind of pocket container.
					// To handle such edge cases, we simply drop the items on the floor.
					storageMode = ContainerStorageMode.NOT_PERSISTENT;
				}
			}

			if (storageMode == ContainerStorageMode.NOT_PERSISTENT) {
				pd.openPocketContainer.dropAllItems(player.getLocation());
			}

			pd.openPocketContainer = null;
			player.closeInventory();
		}
	}

	private void handleClosedPocketContainers() {
		playerData.forEach((playerId, pd) -> {
			if (pd.openPocketContainer != null) {
				Player player = Bukkit.getPlayer(playerId);

				if (player == null) {
					Bukkit.getLogger().log(Level.SEVERE, "Lost pocket container for player " + Bukkit.getOfflinePlayer(playerId).getName());
					return;
				}

				maybeClosePocketContainer(pd, player, false);
			}
		});
	}
	
	private void clean() {
		playerData.entrySet().removeIf(entry -> entry.getValue().clean(currentTick));
		cleanEmptyContainers();
	}
	
	private void fire(Player player, PlayerData data, CustomItemValues weapon, ItemStack weaponStack, boolean isMainhand) {
		if (weapon instanceof CustomWandValues) {
			CustomWandValues wand = (CustomWandValues) weapon;
			
			for (int counter = 0; counter < wand.getAmountPerShot(); counter++)
				CustomItemsPlugin.getInstance().getProjectileManager().fireProjectile(player, wand.getProjectile());
		} else if (weapon instanceof CustomGunValues) {

			CustomGunValues gun = (CustomGunValues) weapon;

			boolean fireGun = false;
			if (gun.getAmmo() instanceof DirectGunAmmoValues) {

				DirectGunAmmoValues directAmmo = (DirectGunAmmoValues) gun.getAmmo();
				if (checkAmmo(player.getInventory(), directAmmo.getAmmoItem(), true)) {
					fireGun = true;
				}
			} else if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

				IndirectGunAmmoValues indirectAmmo = (IndirectGunAmmoValues) gun.getAmmo();
				ItemStack newWeaponStack = new CustomGunWrapper(gun).decrementAmmo(weaponStack);
				if (newWeaponStack != null) {

                    if (isMainhand) {
                    	player.getInventory().setItemInMainHand(newWeaponStack);
					} else {
                    	player.getInventory().setItemInOffHand(newWeaponStack);
					}

					fireGun = true;
				} else {

					if (checkAmmo(player.getInventory(), indirectAmmo.getReloadItem(), false)) {
						if (indirectAmmo.getStartReloadSound() != null) {
							SoundPlayer.playSound(player, indirectAmmo.getStartReloadSound());
						}

                        if (isMainhand) {
                        	data.finishMainhandGunReloadTick = currentTick + indirectAmmo.getReloadTime();
                        	data.mainhandGunToReload = gun;
						} else {
                        	data.finishOffhandGunReloadTick = currentTick + indirectAmmo.getReloadTime();
                        	data.offhandGunToReload = gun;
						}
					}
				}
			} else {
				throw new IllegalArgumentException("Unknown gun ammo system: " + gun.getAmmo().getClass());
			}

			if (fireGun) {
				for (int counter = 0; counter < gun.getAmountPerShot(); counter++) {
					CustomItemsPlugin.getInstance().getProjectileManager().fireProjectile(player, gun.getProjectile());
				}
			} else {

				// If shooting failed, we discard the cooldown
				if (isMainhand) {
					data.nextMainhandGunShootTick = -1;
				} else {
					data.nextOffhandGunShootTick = -1;
				}
			}
		}
	}

	private boolean checkAmmo(Inventory inv, IngredientValues ammo, boolean consume) {

		if (ammo instanceof NoIngredientValues) {
			return true;
		}

		ItemStack[] contents = inv.getContents();
		for (int index = 0; index < contents.length; index++) {
			ItemStack candidate = contents[index];
			if (shouldIngredientAcceptItemStack(ammo, candidate)) {

				if (consume) {
					if (ammo.getRemainingItem() == null) {
						candidate.setAmount(candidate.getAmount() - ammo.getAmount());
					} else {
						contents[index] = convertResultToItemStack(ammo.getRemainingItem());
					}

					inv.setContents(contents);
				}

				return true;
			}
		}

		return false;
	}
	
	/**
	 * Saves the data such that a call to loadData() will return a PluginData with the same data.
	 * 
	 * This method should be called in the onDisable() of CustomItemsPlugin, but could be called on 
	 * additional moments.
	 *
	 * Returns true if and only if the data was saved successfully
	 */
	public boolean saveData() {
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		output.addByte(ENCODING_5);
		save5(output);
		try {
			OutputStream fileOutput = Files.newOutputStream(getDataFile().toPath());
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
			return true;
		} catch (IOException io) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save the CustomItems data", io);
			return false;
		}
	}
	
	private void cleanEmptyContainers() {
		
		// Clean up any empty custom containers
		Iterator<Entry<ContainerStorageKey, ContainerInstance>> entryIterator = persistentContainers.entrySet().iterator();
		entryLoop:
		while (entryIterator.hasNext()) {
			
			Entry<ContainerStorageKey, ContainerInstance> entry = entryIterator.next();
			ContainerInstance instance = entry.getValue();
			
			// Don't close it if anyone is still viewing it
			if (!instance.getInventory().getViewers().isEmpty()) {
				continue;
			}
			
			// Check if its still burning or still has some crafting progress
			if (instance.getCurrentCraftingProgress() != 0 || instance.isAnySlotBurning()) {
				continue;
			}
			
			// Check if any of its input/output/fuel/storage slots is non-empty
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < instance.getType().getHeight(); y++) {

					ContainerSlotValues slot = instance.getType().getSlot(x, y);
					if (slot instanceof InputSlotValues || slot instanceof OutputSlotValues || slot instanceof FuelSlotValues || slot instanceof StorageSlotValues) {

						int invIndex = x + 9 * y;
						if (!ItemUtils.isEmpty(instance.getInventory().getItem(invIndex))) {
							continue entryLoop;
						}
					}
				}
			}

			// If we reach this line, the container is empty and idle, so no need to keep it in memory anymore
			entryIterator.remove();
		}
	}

	private void save5(BitOutput output) {
		output.addLong(currentTick);

		output.addInt(playerData.size());
		for (Entry<UUID,PlayerData> entry : playerData.entrySet()) {
			output.addLong(entry.getKey().getMostSignificantBits());
			output.addLong(entry.getKey().getLeastSignificantBits());
			entry.getValue().save2(output, itemSet, currentTick);
		}

		storedEnergy.save(output);

		cleanEmptyContainers();
		output.addInt(persistentContainers.size());
		for (Entry<ContainerStorageKey, ContainerInstance> entry : persistentContainers.entrySet()) {

			// Save container location
			ContainerStorageKey storageKey = entry.getKey();
			storageKey.save(output);

			// Save container state
			ContainerInstance state = entry.getValue();
			state.save3(output);
			new ArrayList<>(state.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
			state.getInventory().clear();
		}

		playerData.forEach((playerId, pd) -> {
			if (pd.openPocketContainer != null) {
				Player player = Bukkit.getPlayer(playerId);

				if (player == null) {
					Bukkit.getLogger().log(Level.SEVERE, "Lost pocket container for player " + Bukkit.getOfflinePlayer(playerId).getName());
					return;
				}

				maybeClosePocketContainer(pd, player, true);
			}
		});

		tempContainers.forEach(entry -> {
			entry.instance.dropAllItems(entry.viewer.getLocation());
			new ArrayList<>(entry.instance.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
		});
		tempContainers.clear();
	}

	public boolean hasPermissionToShoot(Player player, CustomItemValues item) {
		boolean needsPermission;
		if (item instanceof CustomWandValues) {
			needsPermission = ((CustomWandValues) item).requiresPermission();
		} else if (item instanceof CustomGunValues) {
			needsPermission = ((CustomGunValues) item).requiresPermission();
		} else {
			return false;
		}

		if (needsPermission) {
			return player.hasPermission("customitems.shootall") || player.hasPermission("customitems.shoot." + item.getName());
		} else {
			return true;
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

	/**
	 * Sets the given player in the so-called eating state for the next 10 ticks (a half second). If the
	 * player is already in the eating state, nothing will happen. The player will leave the eating state
	 * if this method is not called again within 10 ticks after this call.
	 *
	 * @param player The player that wants to start eating
	 */
	public void setEating(Player player) {
		getPlayerData(player).setEating(currentTick);
		if (!eatingPlayers.contains(player)) {
			eatingPlayers.add(player);
		}
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

		PlayerData pd = getPlayerData(viewer);
		return pd.openPocketContainer;
	}

	public int destroyCustomContainer(
			CustomContainerValues prototype, String stringHost, Location dropLocation
	) {
		int numDestroyedContainers = 0;

		Iterator<Entry<ContainerStorageKey, ContainerInstance>> containerIterator = persistentContainers.entrySet().iterator();
		while (containerIterator.hasNext()) {
			Entry<ContainerStorageKey, ContainerInstance> entry = containerIterator.next();

			ContainerStorageKey storageKey = entry.getKey();
			if (storageKey.containerName.equals(prototype.getName()) && stringHost.equals(storageKey.stringHost)) {
				ContainerInstance containerInstance = entry.getValue();
				if (dropLocation != null) {
					containerInstance.dropAllItems(dropLocation);
				}
				containerInstance.getInventory().getViewers().forEach(HumanEntity::closeInventory);
				containerIterator.remove();
				numDestroyedContainers += 1;
			}
		}

		storedEnergy.removeStoredEnergyAt(prototype, stringHost);

		return numDestroyedContainers;
	}
	
	public ContainerInstance getCustomContainer(
			Location location, String stringHost, Player newViewer, CustomContainerValues prototype
	) {

		ContainerStorageMode storageMode = prototype.getStorageMode();
		if (storageMode == ContainerStorageMode.NOT_PERSISTENT) {

			/*
			* Non-persistent containers aren't really stored, but still need a storage key to store their energy.
			* The great question is: where should its energy be stored? I think there are 3 ways to tackle this problem:
			*
			* 1) Forbid non-persistent container recipes from using energy. I dislike this option because it takes
			* away some interesting possibilities for using shared energy in the recipes of a non-persistent container.
			* 2) Give non-persistent containers their own private energy storage. This has the same drawback as option
			* (1).
			* 3) Give non-persistent containers the same energy storage mechanism as containers with the
			* PER_LOCATION_PER_PLAYER storage mode. This option would cause energy to be shared if and only if the
			* energy FORCES itself to be shared with other locations and/or players. This option would have full
			* energy sharing support. However, it also has a drawback: the energy will be kept when the container is
			* closed, which goes against the idea of non-persistent containers.
			*
			* It would be nice if I were able to use option (2) when the energy does NOT force sharing and to use
			* option (3) when the energy DOES force sharing. However, 1 container (recipe) can require multiple
			* energy types, so it would be possible to encounter 'conflicting' energy types.
			*
			* I think option (3) is the least bad solution.
			*/
			ContainerStorageKey fakeStorageKey = new ContainerStorageKey(
					prototype.getName(), location != null ? new PassiveLocation(location) : null, stringHost, newViewer.getUniqueId()
			);

			// Not shared between players, so just create a new instance
			TempContainerInstance tempInstance = new TempContainerInstance(
					new ContainerInstance(itemSet.getContainerInfo(prototype), newViewer.getUniqueId(), fakeStorageKey, storedEnergy), newViewer
			);
			tempContainers.add(tempInstance);
			return tempInstance.instance;

		} else {
			ContainerStorageKey storageKey;
			UUID owner;

			if (storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
				storageKey = new ContainerStorageKey(
						prototype.getName(), location != null ? new PassiveLocation(location) : null, stringHost, newViewer.getUniqueId()
				);
				owner = newViewer.getUniqueId();
			} else if (storageMode == ContainerStorageMode.PER_LOCATION) {
				storageKey = new ContainerStorageKey(
						prototype.getName(), location != null ? new PassiveLocation(location) : null, stringHost, null
				);
				owner = null;
			} else if (storageMode == ContainerStorageMode.PER_PLAYER) {
				storageKey = new ContainerStorageKey(
						prototype.getName(), null, null, newViewer.getUniqueId()
				);
				owner = newViewer.getUniqueId();
			} else if (storageMode == ContainerStorageMode.GLOBAL) {
				storageKey = new ContainerStorageKey(
						prototype.getName(), null, null, null
				);
				owner = null;
			} else {
				throw new IllegalArgumentException("Unknown storage mode: " + storageMode);
			}

			ContainerInstance instance = persistentContainers.get(storageKey);
			if (instance == null) {
				instance = new ContainerInstance(itemSet.getContainerInfo(prototype), owner, storageKey, storedEnergy);
				persistentContainers.put(storageKey, instance);
			}
			return instance;
		}
	}

	public void attemptToSwitchToLinkedContainer(Player player, CustomContainerValues newContainer) {
		if (hasPermission(player, newContainer)) {
			ContainerInstance oldInstance = getCustomContainer(player);
			if (oldInstance != null) {

				PlayerData pd = getPlayerData(player);
				if (pd.openPocketContainer != null) {

					// If the player is currently viewing a pocket container, we must close it and save its state
					maybeClosePocketContainer(pd, player, true);
					pd.pocketContainerSelection = true;
					selectCustomContainer(player, newContainer);
				} else {
					ContainerStorageKey oldKey = oldInstance.getStorageKey();
					Location oldBukkitLocation = oldKey.location != null ? oldKey.location.toBukkitLocation() : null;
					ContainerInstance newInstance = getCustomContainer(oldBukkitLocation, oldKey.stringHost, player, newContainer);
					player.openInventory(newInstance.getInventory());
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You don't have permission to open this container");
		}
	}

	public Inventory getCustomContainerMenu(
			Location location, Player player, CustomContainerHost host
	) {

		if (!WorldGuardSupport.canInteract(location.getBlock(), player)) {
			return null;
		}

		Collection<CustomContainerValues> correspondingContainers = getContainersToChooseFrom(player, itemSet.getContainers(host));

		if (correspondingContainers.isEmpty()) {
			return null;
		} else if (correspondingContainers.size() == 1) {
			return getCustomContainer(location, null, player, correspondingContainers.iterator().next()).getInventory();
		} else {
			PlayerData pd = getPlayerData(player);
			pd.containerSelectionLocation = new PassiveLocation(location);
			return containerSelectionMap.get(host);
		}
	}

	public void openPocketContainerMenu(Player player, CustomPocketContainerValues pocketContainer) {
		Collection<CustomContainerValues> containers = getContainersToChooseFrom(player, pocketContainer.getContainers());
		PlayerData pd = getPlayerData(player);
		pd.pocketContainerSelection = !containers.isEmpty();

		if (containers.isEmpty()) {
			player.sendMessage(ChatColor.DARK_RED + "You don't have permission to open any container of this item");
		} else if (containers.size() == 1) {
		    selectCustomContainer(player, containers.iterator().next());
		} else {
            player.openInventory(pocketContainerSelectionMap.get(pocketContainer.getName()));
		}
	}
	
	public List<CustomContainerValues> getCustomContainerSelection(HumanEntity player) {
		for (Entry<CustomContainerHost, Inventory> entry : containerSelectionMap.entrySet()) {
			if (entry.getValue().getViewers().contains(player)) {
				return itemSet.getContainers(entry.getKey()).stream().filter(
						candidate -> !candidate.isHidden()
				).collect(Collectors.toList());
			}
		}

		for (Entry<String, Inventory> entry : pocketContainerSelectionMap.entrySet()) {
			if (entry.getValue().getViewers().contains(player)) {
				CustomItemValues pocketContainer = CustomItemsPlugin.getInstance().getSet().getItem(entry.getKey());
				return ((CustomPocketContainerValues) pocketContainer).getContainers().stream().filter(
						candidate -> !candidate.isHidden()
				).collect(Collectors.toList());
			}
		}

		return null;
	}

	// This location is used for pocket containers. Its coordinates don't really matter, but they must be consistent
	private static final PassiveLocation DUMMY_POCKET_LOCATION = new PassiveLocation(
			new UUID(1, 2), 3, 4, 5
	);

	private static final String[] POCKET_CONTAINER_ENERGY_KEY = { "KnokkosPocketContainer", "StoredEnergy" };

	private static String[] getPocketContainerNbtKey(CustomContainerValues containerType, Player player) {
		if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION) {
			return new String[] {"KnokkosPocketContainer", "State", containerType.getName()};
		} else if (containerType.getStorageMode() == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
			return new String[] {"KnokkosPocketContainer", "State-" + player.getUniqueId(), containerType.getName()};
		} else {
			throw new IllegalArgumentException("Storage mode must be PER_LOCATION or PER_LOCATION_PER_PLAYER, but is " + containerType.getStorageMode());
		}
	}

	public void selectCustomContainer(Player player, CustomContainerValues selected) {
		if (!hasPermission(player, selected)) {
			player.sendMessage(ChatColor.DARK_RED + "You don't have permission to open this custom container");
			return;
		}

		PlayerData pd = getPlayerData(player);

		if (pd.containerSelectionLocation != null) {
			Location containerLocation = pd.containerSelectionLocation.toBukkitLocation();
			pd.containerSelectionLocation = null;

			boolean hostBlockStillValid;
			if (selected.getHost().getVanillaType() != null) {
				CIMaterial blockMaterial = CIMaterial.valueOf(
						KciNms.instance.items.getMaterialName(containerLocation.getBlock())
				);
				VanillaContainerType vanillaType = VanillaContainerType.fromMaterial(blockMaterial);
				hostBlockStillValid = selected.getHost().getVanillaType() == vanillaType;
			} else if (selected.getHost().getVanillaMaterial() != null) {
				CIMaterial blockMaterial = CIMaterial.valueOf(
						KciNms.instance.items.getMaterialName(containerLocation.getBlock())
				);
				hostBlockStillValid = selected.getHost().getVanillaMaterial() == blockMaterial;
			} else if (selected.getHost().getCustomBlockReference() != null) {
				CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(containerLocation.getBlock());
				hostBlockStillValid = customBlock != null && customBlock.getInternalID() == selected.getHost().getCustomBlockReference().get().getInternalID();
			} else {
				throw new IllegalStateException("Custom container " + selected.getName() + " has an invalid host");
			}

			/*
			 * It may happen that a player opens the container selection, but that the
			 * block is broken before the player makes his choice. That situation would
			 * cause a somewhat corrupted state, which is avoided by simply closing the
			 * players inventory.
			 */
			if (hostBlockStillValid) {
				player.openInventory(getCustomContainer(
						containerLocation, null, player, selected
				).getInventory());
			} else {
				player.closeInventory();
			}
		} else if (pd.pocketContainerSelection) {

			PlayerInventory inv = player.getInventory();
			ItemStack mainItem = inv.getItemInMainHand();
			ItemStack offItem = inv.getItemInOffHand();
			CustomItemValues customMain = itemSet.getItem(mainItem);
			CustomItemValues customOff = itemSet.getItem(offItem);

			CustomPocketContainerValues pocketContainer = null;
			ItemStack pocketContainerStack = null;
			boolean isMainHand = false;
			if (customMain instanceof CustomPocketContainerValues) {
				pocketContainer = (CustomPocketContainerValues) customMain;
				pocketContainerStack = mainItem;
				isMainHand = true;
			} else if (customOff instanceof CustomPocketContainerValues) {
				pocketContainer = (CustomPocketContainerValues) customOff;
				pocketContainerStack = offItem;
			}

			if (pocketContainer != null && pocketContainer.getContainers().stream().noneMatch(
					candidate -> candidate.getName().equals(selected.getName())
			)) {
				pocketContainer = null;
				pocketContainerStack = null;
				player.sendMessage(ChatColor.RED + "This pocket container can't hold this custom container");
			}

			if (pocketContainer != null) {

				GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(pocketContainerStack);

				String stringContainerState = null;
				ContainerStorageMode storageMode = selected.getStorageMode();
				if (storageMode == ContainerStorageMode.PER_LOCATION || storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
					String[] nbtKey = getPocketContainerNbtKey(selected, player);
					stringContainerState = nbt.getOrDefault(nbtKey, null);
					nbt.remove(nbtKey);
				}

				String stringStoredEnergy = nbt.getOrDefault(POCKET_CONTAINER_ENERGY_KEY, null);

				StoredEnergy loadedEnergy = new StoredEnergy();
				if (stringStoredEnergy != null) {
					try {
						loadedEnergy = StoredEnergy.load(new ByteArrayBitInput(StringEncoder.decodeTextyBytes(
								stringStoredEnergy.getBytes(StandardCharsets.US_ASCII)
						)));
						nbt.remove(POCKET_CONTAINER_ENERGY_KEY);
					} catch (UnknownEncodingException corruptedEnergy) {
						Bukkit.getLogger().warning("Failed to load the stored energy of a pocket container");
					}
				}

				StoredEnergy pocketStoredEnergy = new PocketStoredEnergy(loadedEnergy, this.storedEnergy);

				if (isMainHand) {
					player.getInventory().setItemInMainHand(nbt.backToBukkit());
				} else {
					player.getInventory().setItemInOffHand(nbt.backToBukkit());
				}

				ContainerInstance instance;
				if (stringContainerState != null) {
					byte[] byteContainerState = StringEncoder.decodeTextyBytes(
							stringContainerState.getBytes(StandardCharsets.US_ASCII)
					);

					BitInput containerStateInput = new ByteArrayBitInput(byteContainerState);
					byte stateEncoding = containerStateInput.readByte();
					if (stateEncoding == ContainerEncoding.ENCODING_2) {

						ContainerStorageKey energyStorageKey = new ContainerStorageKey(
								selected.getName(), DUMMY_POCKET_LOCATION, null, null
						);

						instance = ContainerInstance.load2(
								containerStateInput,
								itemSet.getContainerInfo(selected),
								null, energyStorageKey, pocketStoredEnergy
						);
					} else if (stateEncoding == ContainerEncoding.ENCODING_3) {
						UUID ownerID = storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER ? player.getUniqueId() : null;

						ContainerStorageKey energyStorageKey = new ContainerStorageKey(
								selected.getName(), DUMMY_POCKET_LOCATION, null, ownerID
						);

						try {
							instance = ContainerInstance.load3(
									containerStateInput,
									itemSet.getContainerInfo(selected),
									ownerID, energyStorageKey, pocketStoredEnergy
							);
						} catch (UnknownEncodingException corrupted) {
							throw new IllegalStateException("Corrupted stored pocket container contents in inventory of " + player.getName());
						}
					} else {
						throw new IllegalStateException("Illegal stored pocket container contents in inventory of " + player.getName());
					}

				} else {
					if (storageMode == ContainerStorageMode.GLOBAL) {
						instance = this.getCustomContainer(null, null, null, selected);
					} else if (storageMode == ContainerStorageMode.PER_PLAYER) {
						instance = this.getCustomContainer(null, null, player, selected);
					} else {
						// In this case, the container is either non-persistent or both location-bound and empty
						UUID ownerID = null;
						if (storageMode == ContainerStorageMode.NOT_PERSISTENT || storageMode == ContainerStorageMode.PER_LOCATION_PER_PLAYER) {
							ownerID = player.getUniqueId();
						}

						ContainerStorageKey energyStorageKey = new ContainerStorageKey(
								selected.getName(), DUMMY_POCKET_LOCATION, null, ownerID
						);

						instance = new ContainerInstance(
								itemSet.getContainerInfo(selected), ownerID, energyStorageKey, pocketStoredEnergy
						);
					}
				}

				player.openInventory(instance.getInventory());
				pd.openPocketContainer = instance;
				pd.pocketContainerInMainHand = isMainHand;
			}

			pd.pocketContainerSelection = false;
		}
	}
	
	public void onInventoryClose(Player player) {
		PlayerData pd = getPlayerData(player);
		pd.containerSelectionLocation = null;
		pd.pocketContainerSelection = false;
	}
	
	public void destroyCustomContainersAt(Location location) {

		Iterator<Entry<ContainerStorageKey, ContainerInstance>> persistentIterator =
				persistentContainers.entrySet().iterator();
		PassiveLocation passiveLocation = new PassiveLocation(location);
		
		while (persistentIterator.hasNext()) {
			Entry<ContainerStorageKey, ContainerInstance> entry = persistentIterator.next();
			
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

		storedEnergy.removeStoredEnergyAt(passiveLocation);
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

		private CarefulPluginData(ItemSetWrapper itemSet) {
			super(itemSet);
		}

		@Override
		public boolean saveData() {
			File dataFile = getDataFile();
			if (!dataFile.exists()) {
				return super.saveData();
			} else {
				Bukkit.getLogger().warning("The CustomItems data wasn't saved to protect the original data");
				// I think returning true is the least-bad option from a practical perspective
				return true;
			}
		}
	}
}
