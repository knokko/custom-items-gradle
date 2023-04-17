package nl.knokko.customitems.plugin.tasks.updater;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.*;
import static nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader.LAST_VANILLA_UPGRADE_KEY;
import static nl.knokko.customitems.plugin.util.AttributeMerger.convertAttributeModifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.nms.BooleanRepresentation;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.*;
import nl.knokko.customitems.plugin.util.AttributeMerger;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;

public class ItemUpdater {

	private final ItemSetWrapper itemSet;

	public ItemUpdater(ItemSetWrapper itemSet) {
		this.itemSet = itemSet;
	}

	public void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				updateInventory(player.getInventory(), true);
			}

			for (World world : Bukkit.getWorlds()) {
				for (LivingEntity entity : world.getLivingEntities()) {
					updateEquipment(entity.getEquipment());
				}

				for (ItemFrame itemFrame : world.getEntitiesByClass(ItemFrame.class)) {
					ItemStack item = itemFrame.getItem();
					ItemStack upgradedItem = maybeUpdate(item);

					if (item != upgradedItem) {
						itemFrame.setItem(upgradedItem);
					}
				}
			}
		}, 100, 100);
	}
	
	public void updateInventory(Inventory inventory, boolean clearPlaceholders) {
		int invSize = inventory.getSize();
		for (int index = 0; index < invSize; index++) {
			ItemStack currentStack = inventory.getItem(index);

			// ItemUtils.isEmpty will return true if currentStack is a placeholder item. If clearPlaceholder
			// is false, we shouldn't destroy it (upgrading a placeholder item will result in destroying it).
			if (clearPlaceholders || !ItemUtils.isEmpty(currentStack)) {
				ItemStack newStack = maybeUpdate(currentStack);
				if (newStack != currentStack) {
					inventory.setItem(index, newStack);
				}
			}
		}
	}

	public void updateEquipment(EntityEquipment equipment) {
		if (equipment != null) {
			updateEquipmentPiece(equipment.getItemInMainHand(), equipment::setItemInMainHand);
			updateEquipmentPiece(equipment.getItemInOffHand(), equipment::setItemInOffHand);
			updateEquipmentPiece(equipment.getHelmet(), equipment::setHelmet);
			updateEquipmentPiece(equipment.getChestplate(), equipment::setChestplate);
			updateEquipmentPiece(equipment.getLeggings(), equipment::setLeggings);
			updateEquipmentPiece(equipment.getBoots(), equipment::setBoots);
		}
	}

	private void updateEquipmentPiece(ItemStack original, Consumer<ItemStack> replace) {
		ItemStack upgraded = maybeUpdate(original);
		if (upgraded != original) {
			replace.accept(upgraded);
		}
	}
	
	public ItemStack maybeUpdate(ItemStack originalStack) {
		if (originalStack == null) {
			return null;
		}
		
		GeneralItemNBT preNbt = KciNms.instance.items.generalReadOnlyNbt(originalStack);
		if (
				// If players somehow obtain placeholder items, get rid of those!
				preNbt.getOrDefault(ContainerInstance.PLACEHOLDER_KEY, 0) == 1
						// And also destroy duplicated Dual Wield items
				|| DualWieldSupport.isCorrupted(preNbt)
		) {
			return null;
		}

		List<UUID> upgradeIDs = ItemUpgrader.getExistingUpgradeIDs(preNbt);
		long lastVanillaExportTime = Long.parseLong(preNbt.getOrDefault(LAST_VANILLA_UPGRADE_KEY, "0"));
		
		CustomItemValues[] pOldItem = {null};
		CustomItemValues[] pNewItem = {null};
		UpdateAction[] pAction = {null};
		
		KciNms.instance.items.customReadOnlyNbt(originalStack, nbt -> {
			if (nbt.hasOurNBT()) {
				String itemName = nbt.getName();
				
				Long lastExportTime = nbt.getLastExportTime();
				if (lastExportTime == null) {
					
					/*
					 * If this line is reached, the LastExportTime of the item stack is
					 * missing. That means the item stack wasn't created by this
					 * plug-in.
					 * 
					 * We use this case to make it easier for other plug-ins to
					 * create custom items of this plug-in: Some other plug-in creates
					 * an item stack with our nbt tag, and sets only the Name to the
					 * desired custom item name. As soon as this plug-in finds such
					 * an item stack, it will replace it by a proper item stack
					 * representation of the desired custom item.
					 */
					CustomItemValues currentItem = this.itemSet.getItem(itemName);
					if (currentItem != null) {
						pNewItem[0] = currentItem;
						pAction[0] = UpdateAction.INITIALIZE;
					} else {
						// I'm not really sure how this case should be handled
						pAction[0] = UpdateAction.DO_NOTHING;
					}
				} else if (lastExportTime == this.itemSet.get().getExportTime()) {
					
					/*
					 * If the exportTime of the item stack is the same as the 
					 * exportTime of the item set, there is no need to take any 
					 * action because the item stack properties should already be 
					 * up-to-date.
					 */
					pAction[0] = UpdateAction.DO_NOTHING;
				} else {
					CustomItemValues currentItem = this.itemSet.getItem(itemName);
					if (currentItem != null) {

						if (currentItem.shouldUpdateAutomatically()) {
							BooleanRepresentation oldBoolRepresentation = nbt.getBooleanRepresentation();
							BooleanRepresentation newBoolRepresentation = new BooleanRepresentation(currentItem.getBooleanRepresentation());
							if (oldBoolRepresentation.equals(newBoolRepresentation) && upgradeIDs.isEmpty()) {
								/*
								 * This case will happen when the item set is updated,
								 * but the current custom item stayed the same. If this
								 * happens, we don't need to upgrade the item stack,
								 * but we should update the LastExportTime to prevent the
								 * need for comparing the boolean representations over
								 * and over.
								 */
								pAction[0] = UpdateAction.UPDATE_LAST_EXPORT_TIME;
							} else {

								/*
								 * This case will happen when a custom item stack is
								 * given to a player, but the admin changes some stats
								 * of that custom item (using the Editor). Unfortunately,
								 * some stats of the custom item stack in that player
								 * inventory will keep the old values.
								 *
								 * To update these stats, we need to take action (this is
								 * the primary purpose of this class). This case is more
								 * complicated than it seems because we can't just
								 * completely recreate the item stack (that would for
								 * instance erase the enchantments and durability).
								 */
								try {
									CustomItemValues oldItem = CustomItemValues.loadFromBooleanRepresentation(oldBoolRepresentation.getAsBytes());
									pOldItem[0] = oldItem;
									pNewItem[0] = currentItem;
									pAction[0] = UpdateAction.UPGRADE;

								} catch (UnknownEncodingException bigProblem) {

									/*
									 * There are 2 possible causes for this
									 * situation:
									 *
									 * 1) This item stack was created by a newer version
									 *    of this plug-in. This must mean that someone
									 *    downgraded to an older version of the plug-in.
									 * 2) The nbt of the item got corrupted somehow.
									 *
									 * I don't think there is a good solution for this
									 * situation, but I think doing nothing is best.
									 */
									pAction[0] = UpdateAction.DO_NOTHING;

									Bukkit.getLogger().log(Level.SEVERE,
											"Encountered an unknown encoding while deserializing the" +
													"boolean representation stored in the nbt of the " +
													"following itemstack: " + originalStack
									);
									Bukkit.getLogger().log(Level.SEVERE, "The bad encoding was " + bigProblem.domain + "." + bigProblem.encoding);
								}
							}
						} else {
							// Do nothing if the item must not be updated automatically
							pAction[0] = UpdateAction.DO_NOTHING;
						}
					} else {
						/*
						 * This case will occur when an item stack is found with
						 * a custom item name that is not part of the current item
						 * set. There are roughly 3 possible causes for this:
						 * 1) The item stack belongs to a custom item of a different
						 * item set.
						 * 2) The custom item belonging to the item stack has been
						 * deleted.
						 * 3) The item stack was artificially created with commands
						 * or by some other plug-in.
						 * 
						 * We can check case (2) by comparing the custom item name
						 * of the item stack with all deleted custom items. If
						 * the corresponding custom item was indeed deleted, we
						 * destroy the item set.
						 * 
						 * I think case (1) and (3) are best handled by ignoring it.
						 * That way, the damage will be very limited if the wrong
						 * item set is used. (And there isn't really a good
						 * solution for case 3).
						 */
						pAction[0] = UpdateAction.DO_NOTHING;
						
						if (this.itemSet.get().hasItemBeenDeleted(itemName)) {
							pAction[0] = UpdateAction.DESTROY;
						}
					}
				}
			} else {
				if (lastVanillaExportTime == 0 || lastVanillaExportTime == this.itemSet.get().getExportTime()) {
					/*
					 * If the item stack doesn't have our nbt tag or custom upgrades, we assume it is not a
					 * custom item, and we thus shouldn't mess with it.
					 */
					pAction[0] = UpdateAction.DO_NOTHING;
				} else {
					/*
					 * This is an upgraded vanilla item, so we should ensure that the attribute modifiers and
					 * enchantments from the upgrades are up-to-date.
					 */
					pAction[0] = UpdateAction.REFRESH_VANILLA_UPGRADES;
				}
			}
		});
		
		UpdateAction action = pAction[0];
		
		if (action == UpdateAction.DO_NOTHING) {
			return originalStack;
		} else if (action == UpdateAction.DESTROY) {
			return null;
		} else if (action == UpdateAction.UPDATE_LAST_EXPORT_TIME) {
			ItemStack[] pResult = {null};
			KciNms.instance.items.customReadWriteNbt(originalStack, nbt -> {
				nbt.setLastExportTime(this.itemSet.get().getExportTime());
			}, result -> pResult[0] = result);
			return pResult[0];
		} else if (action == UpdateAction.REFRESH_VANILLA_UPGRADES) {
			return upgradeVanillaItem(originalStack);
		} else {
			
			CustomItemValues newItem = pNewItem[0];
			if (action == UpdateAction.INITIALIZE) {
				return wrap(newItem).create(originalStack.getAmount());
			} else if (action == UpdateAction.UPGRADE) {
				
				CustomItemValues oldItem = pOldItem[0];
				return upgradeCustomItem(originalStack, oldItem, newItem);
			} else {
				throw new Error("Unknown update action: " + action);
			}
		}
	}

	private ItemStack upgradeVanillaItem(ItemStack oldStack) {

		ItemStack newStack = upgradeAttributeModifiers(oldStack, null, null);

		newStack = upgradeEnchantments(newStack, null, null);

		GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(newStack);
		nbt.set(LAST_VANILLA_UPGRADE_KEY, Long.toString(itemSet.get().getExportTime()));
		return nbt.backToBukkit();
	}
	
	private ItemStack upgradeCustomItem(ItemStack oldStack, CustomItemValues oldItem, CustomItemValues newItem) {
		
		ItemStack newStack = upgradeAttributeModifiers(oldStack, oldItem, newItem);

		ItemStack[] pNewStack = {null};
		Long[] pOldDurability = {null};
		Long[] pNewDurability = {null};
		KciNms.instance.items.customReadWriteNbt(newStack, nbt -> {
			nbt.setLastExportTime(this.itemSet.get().getExportTime());
			nbt.setBooleanRepresentation(new BooleanRepresentation(newItem.getBooleanRepresentation()));
			Long currentDurability = nbt.getDurability();
			pOldDurability[0] = currentDurability;
			if (currentDurability != null) {
				if (newItem instanceof CustomToolValues && ((CustomToolValues) newItem).getMaxDurabilityNew() != null) {
					CustomToolValues oldTool = (CustomToolValues) oldItem;
					CustomToolValues newTool = (CustomToolValues) newItem;
					/*
					 * There was durability, and there still is. We will do the
					 * following: if the new maximum durability became bigger,
					 * we increase the current durability by the difference between
					 * the old and new max durability.
					 * 
					 * If the new maximum durability is smaller than the old
					 * maximum durability, the current durability will be set to
					 * the new maximum durability if (and only if) the current
					 * durability is bigger.
					 * 
					 * These decisions are not necessarily perfect, but decisions
					 * have to be made.
					 */
					if (oldTool.getMaxDurabilityNew() != null) {
						if (newTool.getMaxDurabilityNew() >= oldTool.getMaxDurabilityNew()) {
							pNewDurability[0] = currentDurability 
									+ newTool.getMaxDurabilityNew()
									- oldTool.getMaxDurabilityNew();
						} else {
							pNewDurability[0] = Math.min(
									currentDurability, 
									newTool.getMaxDurabilityNew()
							);
						}
					} else {
						// How is this even possible?
						// Anyway, this seems like the most logical response
						pNewDurability[0] = Math.min(
								currentDurability, 
								newTool.getMaxDurabilityNew()
						);
					}
				} else {
					// There was durability, but no more
					pNewDurability[0] = null;
				}
			} else {
				if (newItem instanceof CustomToolValues && ((CustomToolValues) newItem).getMaxDurabilityNew() != null) {
					// There was no durability, but now there is.
					// Let's just start with full durability
					pNewDurability[0] = ((CustomToolValues) newItem).getMaxDurabilityNew();
				} else {
					// There was no durability, and there shouldn't be durability
					pNewDurability[0] = null;
				}
			}
			
			if (pNewDurability[0] != null) {
				nbt.setDurability(pNewDurability[0]);
			} else {
				nbt.removeDurability();
			}
		}, afterNbt -> pNewStack[0] = afterNbt);
		newStack = pNewStack[0];
		
		GeneralItemNBT generalNbt = KciNms.instance.items.generalReadWriteNbt(newStack);
		for (ExtraItemNbtValues.Entry oldPair : oldItem.getExtraNbt().getEntries()) {
			generalNbt.remove(oldPair.getKey().toArray(new String[0]));
		}
		for (ExtraItemNbtValues.Entry newPair : newItem.getExtraNbt().getEntries()) {
			ExtraItemNbtValues.Value newValue = newPair.getValue();
			if (newValue.type == NbtValueType.INTEGER) {
				generalNbt.set(newPair.getKey().toArray(new String[0]), newValue.getIntValue());
			} else if (newValue.type == NbtValueType.STRING) {
				generalNbt.set(newPair.getKey().toArray(new String[0]), newValue.getStringValue());
			} else {
				throw new Error("Unknown nbt value type: " + newValue.type);
			}
		}
		if (newItem.getItemType() == CustomItemType.OTHER) {
			String[] customModelDataKey = { "CustomModelData" };
			generalNbt.set(customModelDataKey, newItem.getItemDamage());
		}
		
		newStack = generalNbt.backToBukkit();
		
		newStack = upgradeEnchantments(newStack, oldItem, newItem);

		KciNms.instance.items.setMaterial(newStack, CustomItemWrapper.getMaterial(newItem.getItemType(), newItem.getOtherMaterial()).name());
		
		ItemMeta meta = newStack.getItemMeta();
		
		upgradeDisplayName(meta, oldItem, newItem);
		upgradeLore(meta, oldItem, newItem, pOldDurability[0], pNewDurability[0]);
		upgradeItemFlags(meta, oldItem, newItem);
		if (newItem instanceof CustomArmorValues) {
			CustomArmorWrapper.colorItemMeta((CustomArmorValues) newItem, meta);
		}

		if (newItem.getItemType() != CustomItemType.OTHER) {
			meta.setUnbreakable(true);
		}
		newStack.setItemMeta(meta);

		if (newItem.getItemType() != CustomItemType.OTHER) {
			newStack.setDurability(newItem.getItemDamage());
		}
		
		return newStack;
	}
	
	private void upgradeDisplayName(ItemMeta toUpgrade, CustomItemValues oldItem, CustomItemValues newItem) {
		/*
		 * If the item allows anvil actions, it is possible that the player renamed
		 * the item in an anvil. It would be bad to 'reset' the name each time the
		 * item set is updated, so I will instead replace all occurrences of the
		 * display name of the old custom item with the display name of the new
		 * custom item. (Even this might not always be ideal, but I don't think
		 * there is some 'perfect' behavior for this.)
		 */
		if (newItem.allowAnvilActions()) {
			if (toUpgrade.hasDisplayName()) {
				String currentDisplayName = toUpgrade.getDisplayName();
				String newDisplayName = currentDisplayName.replace(oldItem.getDisplayName(), newItem.getDisplayName());
				toUpgrade.setDisplayName(newDisplayName);
			}
			// else... well... the player decided to remove the custom name entirely
			// so lets keep it that way
		} else {
			toUpgrade.setDisplayName(newItem.getDisplayName());
		}
	}
	
	private void upgradeLore(ItemMeta toUpgrade, CustomItemValues oldItem, CustomItemValues newItem, Long oldDurability, Long newDurability) {
		if (!Objects.equals(oldDurability, newDurability)) {
			Long oldMaxDurability = oldItem instanceof CustomToolValues ? ((CustomToolValues) oldItem).getMaxDurabilityNew() : null;
			Long newMaxDurability = newItem instanceof CustomToolValues ? ((CustomToolValues) newItem).getMaxDurabilityNew() : null;
			if (LoreUpdater.updateDurability(
					toUpgrade, oldDurability, newDurability, oldMaxDurability, newMaxDurability, CustomToolWrapper.prefix()
			)) {
				toUpgrade.setLore(wrap(newItem).createLore(newDurability));
				return;
			}
		}

		if (!Objects.deepEquals(oldItem.getLore(), newItem.getLore())) {
			if (LoreUpdater.updateBaseLore(toUpgrade, oldItem.getLore(), newItem.getLore())) {
				toUpgrade.setLore(wrap(newItem).createLore(newDurability));
			}
		}
	}
	
	private void upgradeItemFlags(ItemMeta toUpgrade, CustomItemValues oldItem, CustomItemValues newItem) {
		/*
		 * We will only update the item flags that changed for optimal preservation
		 * of the custom values of the item stack being upgraded.
		 */
		List<Boolean> oldFlags = oldItem.getItemFlags();
		List<Boolean> newFlags = newItem.getItemFlags();
		boolean hadAttributes = oldItem.getAttributeModifiers().size() > 0;
		boolean hasAttributes = newItem.getAttributeModifiers().size() > 0;
		ItemFlag[] allFlags = ItemFlag.values();
		for (int flagIndex = 0; flagIndex < allFlags.length; flagIndex++) {
			boolean oldHasFlag = flagIndex < oldFlags.size() && oldFlags.get(flagIndex);
			boolean newHasFlag = flagIndex < newFlags.size() && newFlags.get(flagIndex);
			ItemFlag currentFlag = allFlags[flagIndex];
			
			// Yeah... there is a special and nasty edge case for the HIDE_ATTRIBUTES flag
			if (currentFlag == ItemFlag.HIDE_ATTRIBUTES) {
				if (!hadAttributes && hasAttributes) {
					toUpgrade.removeItemFlags(
							org.bukkit.inventory.ItemFlag.valueOf(currentFlag.name())
					);
				}
				if (hadAttributes && !hasAttributes) {
					toUpgrade.addItemFlags(
							org.bukkit.inventory.ItemFlag.valueOf(currentFlag.name())
					);
				}
			} else if (oldHasFlag != newHasFlag) {
				if (newHasFlag) {
					toUpgrade.addItemFlags(
							org.bukkit.inventory.ItemFlag.valueOf(currentFlag.name())
					);
				} else {
					toUpgrade.removeItemFlags(
							org.bukkit.inventory.ItemFlag.valueOf(currentFlag.name())
					);
				}
			}
		}
	}
	
	private ItemStack upgradeAttributeModifiers(
			ItemStack oldStack, CustomItemValues oldItem, CustomItemValues newItem
	) {
		RawAttribute[] oldStackAttributes = KciNms.instance.items.getAttributes(oldStack);
		Collection<RawAttribute> newStackAttributes = new ArrayList<>(oldStackAttributes.length);

		GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(oldStack);
		boolean hasStoredExistingAttributes = ItemUpgrader.hasStoredExistingAttributeIDs(nbt);
		Collection<UUID> attributesToRemove = ItemUpgrader.getExistingAttributeIDs(nbt);
		
		oldStackLoop:
		for (RawAttribute oldStackAttribute : oldStackAttributes) {

			/*
			 * To update an item, we need to replace all old attribute modifiers from this plug-in with new attribute
			 * modifiers (because the attribute modifiers of the item or one of its upgrades could have been changed in
			 * the Editor). But, to improve compatibility with other plug-ins, we should NOT remove the attribute
			 * modifiers that were added by other plug-ins.
			 *
			 * Items created with KCI 12.0 or later will store all UUIDs of the attribute modifiers that were added by
			 * KCI (and `hasStoredExistingAttributes` will be true). This case is easy since we know exactly which
			 * attribute modifiers should be removed.
			 *
			 * Items created before KCI 12.0 (`hasStoredExistingAttributes` will be false) did not store the UUIDs,
			 * so we should instead try to remove all attribute modifiers that are equal to the attribute modifiers
			 * of the old custom item. This is not perfect, but usually works.
			 */
			if (hasStoredExistingAttributes) {
				if (attributesToRemove.contains(oldStackAttribute.id)) continue;
			} else {
				for (AttributeModifierValues rawOldAttribute : oldItem.getAttributeModifiers()) {
					RawAttribute oldItemAttribute = convertAttributeModifier(rawOldAttribute, null);
					if (oldStackAttribute.equalsIgnoreId(oldItemAttribute)) {
						continue oldStackLoop;
					}
				}
			}

			// Don't stack dummy attributes
			if (oldStackAttribute.isDummy()) {
				continue;
			}
			
			newStackAttributes.add(oldStackAttribute);
		}

		// Add new attribute modifiers
		RawAttribute[] newKciAttributes = AttributeMerger.merge(newItem, ItemUpgrader.getUpgrades(oldStack, itemSet));
		ItemUpgrader.setAttributeIDs(
				nbt,
				Arrays.stream(newKciAttributes).map(attribute -> attribute.id).collect(Collectors.toList())
		);
		Collections.addAll(newStackAttributes, newKciAttributes);

		return KciNms.instance.items.replaceAttributes(nbt.backToBukkit(), newStackAttributes.toArray(new RawAttribute[0]));
	}

	static void addEnchantmentsToMap(
			Map<EnchantmentType, Integer> enchantmentMap,
			Collection<EnchantmentValues> enchantments
	) {
		for (EnchantmentValues enchantment : enchantments) {
			enchantmentMap.put(
					enchantment.getType(),
					enchantmentMap.getOrDefault(enchantment.getType(), 0) + enchantment.getLevel()
			);
		}
	}

	static Map<EnchantmentType, Integer> determineEnchantmentAdjustments(
			Map<EnchantmentType, Integer> oldKciEnchantments,
			Map<EnchantmentType, Integer> newKciEnchantments
	) {
		Map<EnchantmentType, Integer> adjustments = new HashMap<>();
		oldKciEnchantments.forEach((enchantment, level) ->
				adjustments.put(enchantment, adjustments.getOrDefault(enchantment, 0) - level)
		);
		newKciEnchantments.forEach((enchantment, level) ->
				adjustments.put(enchantment, adjustments.getOrDefault(enchantment, 0) + level)
		);
		return adjustments;
	}

	static void applyEnchantmentAdjustments(ItemStack itemStack, Map<EnchantmentType, Integer> adjustments) {
		for (Map.Entry<EnchantmentType, Integer> enchantmentEntry : adjustments.entrySet()) {
			EnchantmentType enchantment = enchantmentEntry.getKey();
			int level = enchantmentEntry.getValue();

			if (level != 0) {
				int newLevel = BukkitEnchantments.getLevel(itemStack, enchantment) + level;
				if (newLevel > 0) BukkitEnchantments.add(itemStack, enchantment, newLevel);
				else BukkitEnchantments.remove(itemStack, enchantment);
			}
		}
	}
	
	private ItemStack upgradeEnchantments(ItemStack toUpgrade, CustomItemValues oldItem, CustomItemValues newItem) {

		/*
		 * To update an item, we need to replace all old enchantments given by this plug-in with new enchantments
		 * (because the default enchantments of the item or one of its upgrades could have been changed in
		 * the Editor). However, we should NOT just remove ALL enchantments because that would upset players who
		 * used an anvil and an enchanted book to add additional enchantments (or increased the level of a default
		 * enchantment).
		 *
		 * Items created with KCI 12.0 or later will store all names and levels of the enchantments that were added by
		 * KCI (either default enchantments or enchantments from upgrades). Items created before KCI 12.0 did not
		 * include this information. But, since there were no upgrades before KCI 12.0, all enchantments from KCI are
		 * the default enchantments of the old custom item, which we DO know.
		 */
		GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(toUpgrade);
		Map<EnchantmentType, Integer> oldKciEnchantments;
		if (ItemUpgrader.hasStoredEnchantmentUpgrades(nbt)) {
			oldKciEnchantments = ItemUpgrader.getEnchantmentUpgrades(nbt);
		} else {
			oldKciEnchantments = new HashMap<>();
			addEnchantmentsToMap(oldKciEnchantments, oldItem.getDefaultEnchantments());
		}

		Map<EnchantmentType, Integer> newKciEnchantments = new HashMap<>();
		if (newItem != null) addEnchantmentsToMap(newKciEnchantments, newItem.getDefaultEnchantments());
		for (UpgradeValues upgrade : ItemUpgrader.getUpgrades(toUpgrade, itemSet)) {
			addEnchantmentsToMap(newKciEnchantments, upgrade.getEnchantments());
		}
		ItemUpgrader.setEnchantmentUpgrades(nbt, newKciEnchantments);
		toUpgrade = nbt.backToBukkit();

		Map<EnchantmentType, Integer> adjustments = determineEnchantmentAdjustments(oldKciEnchantments, newKciEnchantments);
		applyEnchantmentAdjustments(toUpgrade, adjustments);

		return toUpgrade;
	}

	private enum UpdateAction {
		
		DO_NOTHING,
		UPDATE_LAST_EXPORT_TIME,
		INITIALIZE,
		UPGRADE,
		REFRESH_VANILLA_UPGRADES,
		DESTROY
	}
}
