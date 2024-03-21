package nl.knokko.customitems.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;

import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.spell.MageSpell;
import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.container.ContainerManager;
import nl.knokko.customitems.plugin.container.ContainerSelectionManager;
import nl.knokko.customitems.plugin.data.container.ContainerStorage;
import nl.knokko.customitems.plugin.data.container.PersistentContainerStorage;
import nl.knokko.customitems.plugin.data.container.StoredEnergy;
import nl.knokko.customitems.plugin.events.CustomFoodEatEvent;
import nl.knokko.customitems.plugin.multisupport.magic.MagicSupport;
import nl.knokko.customitems.plugin.util.SoundPlayer;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomGunWrapper;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
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

	public static PluginData dummy() {
		ItemSetWrapper dummyItemSet = new ItemSetWrapper();
		dummyItemSet.setItemSet(new ItemSet(ItemSet.Side.PLUGIN));
		return new PluginData(dummyItemSet);
	}
	
	/**
	 * Attempts to read the plugin data that was saved previously. If the data can be found, it will be
	 * loaded and a PluginData instance with the loaded data will be returned.
	 * If no previously saved data was found, a new empty PluginData instance will be returned.
	 * <br>
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
			Bukkit.getLogger().warning("Couldn't find the data file for CustomItems. Is this the first time you are using CustomItems?");
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
		return new PluginData(itemSet, currentTick, playersMap, new ContainerStorage(
				new PersistentContainerStorage(), new StoredEnergy()
		));
	}
	
	private static PluginData load2(BitInput input, ItemSetWrapper itemSet) {
		long currentTick = input.readLong();
		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, itemSet);
		ContainerStorage containerStorage = ContainerStorage.load2(input, itemSet);
		
		return new PluginData(itemSet, currentTick, playersMap, containerStorage);
	}

	private static PluginData load3(BitInput input, ItemSetWrapper itemSet) {
		long currentTick = input.readLong();
		Map<UUID, PlayerData> playersMap = loadPlayerData1(input, itemSet);
		ContainerStorage containerStorage = ContainerStorage.load3(input, itemSet);

		return new PluginData(itemSet, currentTick, playersMap, containerStorage);
	}

	private static PluginData load4(BitInput input, ItemSetWrapper itemSet) throws UnknownEncodingException {
		long currentTick = input.readLong();
		Map<UUID, PlayerData> playersMap = loadPlayerData2(input, itemSet);
		ContainerStorage containerStorage = ContainerStorage.load3(input, itemSet);

		return new PluginData(itemSet, currentTick, playersMap, containerStorage);
	}

	private static PluginData load5(BitInput input, ItemSetWrapper itemSet) throws UnknownEncodingException {
		long currentTick = input.readLong();
		Map<UUID, PlayerData> playersMap = loadPlayerData2(input, itemSet);
		ContainerStorage containerStorage = ContainerStorage.load5(input, itemSet);

		return new PluginData(itemSet, currentTick, playersMap, containerStorage);
	}

	// Persisting data
	private final Map<UUID,PlayerData> playerData;
	private final ContainerStorage containerStorage;

	private long currentTick;
	
	// Non-persisting data
	private final ItemSetWrapper itemSet;
	public final ContainerManager containerManager;
	public final ContainerSelectionManager containerSelections;
	private List<Player> shootingPlayers;
	private List<Player> eatingPlayers;

	private PluginData(ItemSetWrapper itemSet) {
		this.itemSet = itemSet;
		playerData = new HashMap<>();
		containerStorage = new ContainerStorage(new PersistentContainerStorage(), new StoredEnergy());
		containerManager = new ContainerManager(itemSet, containerStorage, playerData);
		containerSelections = new ContainerSelectionManager(itemSet, containerManager, playerData);
		currentTick = 0;
		
		init();
	}
	
	private PluginData(
			ItemSetWrapper itemSet, long currentTick,
			Map<UUID,PlayerData> playerData,
			ContainerStorage containerStorage
	) {
		this.itemSet = itemSet;
		this.playerData = playerData;
		this.containerStorage = containerStorage;
		this.containerManager = new ContainerManager(itemSet, containerStorage, playerData);
		this.containerSelections = new ContainerSelectionManager(itemSet, containerManager, playerData);
		this.currentTick = currentTick;
		
		init();
	}
	
	private void init() {
		shootingPlayers = new LinkedList<>();
		eatingPlayers = new LinkedList<>();

		CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::update, 1, 1);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, containerStorage::cleanTemporary, 50, 10);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::clean, 200, 40);
	}

	private void update() {
		currentTick++;
		
		updateShooting();
		containerManager.update();
		manageReloadingGuns();
		updateEating();
	}
	
	private void updateShooting() {
		Iterator<Player> iterator = shootingPlayers.iterator();
		while (iterator.hasNext()) {
			Player current = iterator.next();
			PlayerData data = PlayerData.get(current, playerData);
			if (data.isShooting(currentTick)) {
				CustomItemValues mainItem = itemSet.getItem(current.getInventory().getItemInMainHand());
				CustomItemValues offItem = itemSet.getItem(current.getInventory().getItemInOffHand());

				float[] pMana = { 0f };
				Mage mage = MagicSupport.MAGIC != null ? MagicSupport.MAGIC.getController().getMage(current) : null;
				if (mage != null) pMana[0] = mage.getMana();
				if (hasPermissionToShoot(current, mainItem) && data.shootIfAllowed(mainItem, currentTick, true, pMana)) {
					fire(current, data, mainItem, current.getInventory().getItemInMainHand(), true);
				}
				if (hasPermissionToShoot(current, offItem) && data.shootIfAllowed(offItem, currentTick, false, pMana)) {
					fire(current, data, offItem, current.getInventory().getItemInOffHand(), false);
				}
				if (mage != null) mage.setMana(pMana[0]);
			} else {
				iterator.remove();
			}
		}
	}

	public static void consumeCustomFood(
			Player player, ItemStack oldStack,
			CustomFoodValues food, Consumer<ItemStack> updateStack
	) {
		CustomFoodEatEvent event = new CustomFoodEatEvent(player, oldStack, food, CustomItemsPlugin.getInstance().getSet());
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			player.setFoodLevel(player.getFoodLevel() + food.getFoodValue());
			food.getEatEffects().forEach(eatEffect ->
					player.addPotionEffect(new PotionEffect(
							PotionEffectType.getByName(eatEffect.getType().name()),
							eatEffect.getDuration(),
							eatEffect.getLevel() - 1
					))
			);
			oldStack.setAmount(oldStack.getAmount() - 1);
		}
		updateStack.accept(oldStack);
	}

	private void updateEating() {

		Iterator<Player> iterator = eatingPlayers.iterator();
		while (iterator.hasNext()) {
		    Player player = iterator.next();
		    PlayerData pd = PlayerData.get(player, playerData);

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
							consumeCustomFood(player, mainItemStack, mainFood, player.getInventory()::setItemInMainHand);
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
							consumeCustomFood(player, offItemStack, offFood, player.getInventory()::setItemInOffHand);
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
								new CustomGunWrapper(gun).reload(currentItem);
								player.getInventory().setItemInMainHand(currentItem);
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
								new CustomGunWrapper(gun).reload(currentItem);
                                player.getInventory().setItemInOffHand(currentItem);
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

	public PlayerWandInfo getWandInfo(Player player, CustomWandValues wand) {
		int currentMana = 0;
		int maxMana = 0;
		if (MagicSupport.MAGIC != null) {
			Mage mage = MagicSupport.MAGIC.getController().getMage(player);
			currentMana = (int) mage.getMana();
			maxMana = mage.getManaMax();
		}

		PlayerData targetPlayerData = playerData.get(player.getUniqueId());
		if (targetPlayerData != null) {
		    PlayerWandData wandData = targetPlayerData.wandsData.get(wand);
		    if (wandData != null) {
		    	return new PlayerWandInfo(
		    			wandData.getRemainingCooldown(currentTick),
						wandData.getCurrentCharges(wand, currentTick),
						wandData.getTimeUntilNextRecharge(wand, currentTick),
						currentMana, maxMana
				);
			}
		}

		if (currentMana < maxMana) {
			WandChargeValues charges = wand.getCharges();
			int maxCharges = charges != null ? charges.getMaxCharges() : 1;
			return new PlayerWandInfo(0, maxCharges, 0, currentMana, maxMana);
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
		PlayerData.get(player, playerData).commandCooldowns.setOnCooldown(customItem, event, commandIndex, currentTick);
	}

	public void onPlayerQuit(Player player) {
		containerManager.quit(player);
	}

	private void clean() {
		playerData.entrySet().removeIf(entry -> entry.getValue().clean(currentTick));
		containerStorage.cleanEmpty();
		containerSelections.clean();
	}
	
	private void fire(Player player, PlayerData data, CustomItemValues weapon, ItemStack weaponStack, boolean isMainhand) {
		if (weapon instanceof CustomWandValues) {
			CustomWandValues wand = (CustomWandValues) weapon;

			if (wand.getProjectile() != null) {
				for (int counter = 0; counter < wand.getAmountPerShot(); counter++)
					CustomItemsPlugin.getInstance().getProjectileManager().fireProjectile(player, wand.getProjectile());
			}

			if (MagicSupport.MAGIC == null && !wand.getMagicSpells().isEmpty()) {
				Bukkit.getLogger().warning("Wand " + wand.getName() + " has Magic spells, but Magic isn't installed");
			}

			if (MagicSupport.MAGIC != null) {
				Mage mage = MagicSupport.MAGIC.getController().getMage(player);
				boolean wasCostFree = mage.isCostFree();
				mage.setCostFree(true);

				for (String spellName : wand.getMagicSpells()) {
					SpellTemplate template = MagicSupport.MAGIC.getController().getSpellTemplate(spellName);
					if (template == null) {
						Bukkit.getLogger().warning("Can't find Magic spell " + spellName);
						continue;
					}
					MageSpell spell = (MageSpell) template.createSpell();
					if (spell == null) {
						Bukkit.getLogger().warning("Failed to create Magic spell " + spellName + " from template");
						continue;
					}
					spell.setMage(mage);
					if (!spell.cast()) {
						Bukkit.getLogger().warning("Failed to cast Magic spell " + spellName);
					}
				}

				mage.setCostFree(wasCostFree);
			}
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
		} else if (weapon instanceof CustomThrowableValues) {
			CustomThrowableValues throwable = (CustomThrowableValues) weapon;
			for (int counter = 0; counter < throwable.getAmountPerShot(); counter++) {
				CustomItemsPlugin.getInstance().getProjectileManager().fireProjectile(player, throwable.getProjectile());
			}

			weaponStack.setAmount(weaponStack.getAmount() - 1);
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
	 * <br>
	 * This method should be called in the onDisable() of CustomItemsPlugin, but could be called on 
	 * additional moments.
	 * <br>
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

	private void save5(BitOutput output) {
		output.addLong(currentTick);

		output.addInt(playerData.size());
		for (Entry<UUID,PlayerData> entry : playerData.entrySet()) {
			output.addLong(entry.getKey().getMostSignificantBits());
			output.addLong(entry.getKey().getLeastSignificantBits());
			entry.getValue().save2(output, itemSet, currentTick);
		}
		containerStorage.saveEnergy(output);
		containerStorage.cleanEmpty();
		containerStorage.savePersistent(output);
		containerManager.closeAllNonStorage();
	}

	public boolean hasPermissionToShoot(Player player, CustomItemValues item) {
		boolean needsPermission;
		if (item instanceof CustomWandValues) {
			needsPermission = ((CustomWandValues) item).requiresPermission();
		} else if (item instanceof CustomGunValues) {
			needsPermission = ((CustomGunValues) item).requiresPermission();
		} else if (item instanceof CustomThrowableValues) {
			needsPermission = ((CustomThrowableValues) item).shouldRequirePermission();
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
		PlayerData.get(player, playerData).setShooting(currentTick);
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
		PlayerData.get(player, playerData).setEating(currentTick);
		if (!eatingPlayers.contains(player)) {
			eatingPlayers.add(player);
		}
	}

	/**
	 * @return The number of ticks passed since the first use of this plug-in of at least version 6.0
	 */
	public long getCurrentTick() {
		return currentTick;
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
