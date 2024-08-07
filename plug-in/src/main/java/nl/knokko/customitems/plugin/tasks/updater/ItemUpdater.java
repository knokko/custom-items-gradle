package nl.knokko.customitems.plugin.tasks.updater;

import static nl.knokko.customitems.MCVersions.VERSION1_14;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.*;
import static nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader.LAST_VANILLA_UPGRADE_KEY;
import static nl.knokko.customitems.plugin.util.AttributeMerger.convertAttributeModifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.nms.BooleanRepresentation;
import nl.knokko.customitems.nms.CorruptedItemStackException;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.*;
import nl.knokko.customitems.plugin.util.AttributeMerger;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.plugin.util.NbtHelper;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
					try {
						updateEquipment(entity.getEquipment());
					} catch (CorruptedItemStackException e) {
						Bukkit.getLogger().warning("Encountered corrupted item stack in equipment of " + entity);
					}
				}

				for (ItemFrame itemFrame : world.getEntitiesByClass(ItemFrame.class)) {
					ItemStack item = itemFrame.getItem();
					ItemStack upgradedItem;
					try {
						upgradedItem = maybeUpdate(item);
					} catch (CorruptedItemStackException e) {
						Bukkit.getLogger().warning("Encountered corrupted item stack in item frame at " + itemFrame.getLocation());
						continue;
					}

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
				ItemStack newStack;
				try {
					newStack = maybeUpdate(currentStack);
				} catch (CorruptedItemStackException e) {
					Bukkit.getLogger().warning(
							"Encountered corrupted item stack in inventory of " +
									inventory.getHolder() + ": " + currentStack
					);
					continue;
				}
				if (newStack != currentStack) {
					inventory.setItem(index, newStack);
				}
			}
		}
	}

	public void updateEquipment(EntityEquipment equipment) throws CorruptedItemStackException {
		if (equipment != null) {
			updateEquipmentPiece(equipment.getItemInMainHand(), equipment::setItemInMainHand);
			updateEquipmentPiece(equipment.getItemInOffHand(), equipment::setItemInOffHand);
			updateEquipmentPiece(equipment.getHelmet(), equipment::setHelmet);
			updateEquipmentPiece(equipment.getChestplate(), equipment::setChestplate);
			updateEquipmentPiece(equipment.getLeggings(), equipment::setLeggings);
			updateEquipmentPiece(equipment.getBoots(), equipment::setBoots);
		}
	}

	private void updateEquipmentPiece(ItemStack original, Consumer<ItemStack> replace) throws CorruptedItemStackException {
		ItemStack upgraded = maybeUpdate(original);
		if (upgraded != original) {
			replace.accept(upgraded);
		}
	}
	
	public ItemStack maybeUpdate(ItemStack originalStack) throws CorruptedItemStackException {
		if (originalStack == null || originalStack.getAmount() == 0 || originalStack.getType() == Material.AIR) {
			return null;
		}

		// If players somehow obtain placeholder items, get rid of those!
		// And also destroy duplicated Dual Wield items
		boolean isFake = NBT.get(originalStack, nbt ->
				NbtHelper.getNested(nbt, ContainerInstance.PLACEHOLDER_KEY, 0) == 1 ||
				DualWieldSupport.isCorrupted(nbt)
		);
		if (isFake) return null;

		List<UUID> upgradeIDs = NBT.get(originalStack, ItemUpgrader::getExistingUpgradeIDs);
		long lastVanillaExportTime = NBT.get(originalStack, nbt -> {
			return Long.parseLong(NbtHelper.getNested(nbt, LAST_VANILLA_UPGRADE_KEY, "0"));
		});

		KciItem[] pOldItem = {null};
		KciItem[] pNewItem = {null};
		UpdateAction[] pAction = {null};

		NBT.get(originalStack, nbt -> {
			ReadableNBT customNbt = nbt.getCompound(NBT_KEY);
			if (customNbt != null) {
				String itemName = customNbt.getString("Name");

				Long lastExportTime = null;
				if (customNbt.hasTag("LastExportTime", NBTType.NBTTagLong)) {
					lastExportTime = customNbt.getLong("LastExportTime");
				}

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
					KciItem currentItem = this.itemSet.getItem(itemName);
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
					KciItem currentItem = this.itemSet.getItem(itemName);
					if (currentItem != null) {

						if (currentItem.shouldUpdateAutomatically()) {
							BooleanRepresentation oldBoolRepresentation = new BooleanRepresentation(
									customNbt.getByteArray("BooleanRepresentation")
							);
							BooleanRepresentation newBoolRepresentation = new BooleanRepresentation(
									currentItem.getBooleanRepresentation()
							);
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
									KciItem oldItem = KciItem.loadFromBooleanRepresentation(oldBoolRepresentation.getAsBytes());
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
			ItemStack newStack = originalStack.clone();
			NBT.modify(newStack, nbt -> {
				ReadWriteNBT customNbt = nbt.getOrCreateCompound(NBT_KEY);
				customNbt.setLong("LastExportTime", this.itemSet.get().getExportTime());
			});
			return newStack;
		} else if (action == UpdateAction.REFRESH_VANILLA_UPGRADES) {
			return upgradeVanillaItem(originalStack);
		} else {
			
			KciItem newItem = pNewItem[0];
			if (action == UpdateAction.INITIALIZE) {
				return wrap(newItem).create(originalStack.getAmount());
			} else if (action == UpdateAction.UPGRADE) {
				
				KciItem oldItem = pOldItem[0];
				return upgradeCustomItem(originalStack, oldItem, newItem);
			} else {
				throw new Error("Unknown update action: " + action);
			}
		}
	}

	private ItemStack upgradeVanillaItem(ItemStack oldStack) throws CorruptedItemStackException {

		ItemStack newStack = upgradeAttributeModifiers(oldStack, null, null);

		newStack = upgradeEnchantments(newStack, null, null);
		NBT.modify(newStack, nbt -> {
			NbtHelper.setNested(nbt, LAST_VANILLA_UPGRADE_KEY, Long.toString(itemSet.get().getExportTime()));
		});
		return newStack;
	}
	
	private ItemStack upgradeCustomItem(
			ItemStack oldStack, KciItem oldItem, KciItem newItem
	) throws CorruptedItemStackException {
		
		ItemStack newStack = upgradeAttributeModifiers(oldStack, oldItem, newItem);

		Long[] pOldDurability = {null};
		Long[] pNewDurability = {null};

		NBT.modify(newStack, generalNbt -> {
			ReadWriteNBT customNbt = generalNbt.getOrCreateCompound(NBT_KEY);
			customNbt.setLong("LastExportTime", this.itemSet.get().getExportTime());
			customNbt.setByteArray("BooleanRepresentation", newItem.getBooleanRepresentation());
			Long currentDurability = null;
			if (customNbt.hasTag("Durability", NBTType.NBTTagLong)) {
				currentDurability = customNbt.getLong("Durability");
			}

			pOldDurability[0] = currentDurability;
			if (currentDurability != null) {
				if (newItem instanceof KciTool && ((KciTool) newItem).getMaxDurabilityNew() != null) {
					KciTool oldTool = (KciTool) oldItem;
					KciTool newTool = (KciTool) newItem;
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
				if (newItem instanceof KciTool && ((KciTool) newItem).getMaxDurabilityNew() != null) {
					// There was no durability, but now there is.
					// Let's just start with full durability
					pNewDurability[0] = ((KciTool) newItem).getMaxDurabilityNew();
				} else {
					// There was no durability, and there shouldn't be durability
					pNewDurability[0] = null;
				}
			}

			if (pNewDurability[0] != null) {
				customNbt.setLong("Durability", pNewDurability[0]);
			} else {
				customNbt.removeKey("Durability");
			}

			for (String rawOldNbt : oldItem.getExtraNbt()) {
				ReadableNBT oldNbt = NBT.parseNBT(rawOldNbt);
				removeOldNbt(generalNbt, oldNbt);
			}
			for (String newNbt : newItem.getExtraNbt()) {
				generalNbt.mergeCompound(NBT.parseNBT(newNbt));
			}
		});

		newStack = upgradeEnchantments(newStack, oldItem, newItem);

		KciNms.instance.items.setMaterial(newStack, newItem.getVMaterial(KciNms.mcVersion).name());
		
		ItemMeta meta = newStack.getItemMeta();
		
		upgradeDisplayName(newStack, meta, oldItem, newItem);
		upgradeLore1(meta, oldItem, newItem, pOldDurability[0], pNewDurability[0]);
		upgradeItemFlags(meta, oldItem, newItem);
		if (newItem instanceof KciArmor) {
			CustomArmorWrapper.colorItemMeta((KciArmor) newItem, meta);
		}

		meta.setUnbreakable(KciNms.mcVersion < VERSION1_14 || !wrap(newItem).showDurabilityBar());
		if (KciNms.mcVersion >= VERSION1_14) KciNms.instance.items.setCustomModelData(meta, newItem.getItemDamage());
		newStack.setItemMeta(meta);
		upgradeLore2(newStack, oldItem, newItem);

		if (KciNms.mcVersion < VERSION1_14) newStack.setDurability(newItem.getItemDamage());
		else {
			CustomItemWrapper newWrapper = wrap(newItem);
			if (newWrapper instanceof CustomToolWrapper) {
				CustomToolWrapper toolWrapper = (CustomToolWrapper) newWrapper;
				toolWrapper.updateDurabilityBar(newStack);
			}
		}

		return newStack;
	}

	private void removeOldNbt(ReadWriteNBT nbt, ReadableNBT old) {
		for (String key : old.getKeys()) {
			if (old.getType(key) == NBTType.NBTTagCompound && nbt.getType(key) == NBTType.NBTTagCompound) {
				ReadWriteNBT child = nbt.getCompound(key);
				ReadableNBT oldChild = old.getCompound(key);
                assert oldChild != null;
                removeOldNbt(child, oldChild);
                assert child != null;
                if (child.getKeys().isEmpty()) nbt.removeKey(key);
			} else nbt.removeKey(key);
		}
	}
	
	private void upgradeDisplayName(
			ItemStack stackToUpgrade, ItemMeta toUpgrade,
			KciItem oldItem, KciItem newItem
	) {
		if (newItem.getTranslations().isEmpty()) {
			/*
			 * If the item allows anvil actions, it is possible that the player renamed
			 * the item in an anvil. It would be bad to 'reset' the name each time the
			 * item set is updated, so I will instead replace all occurrences of the
			 * display name of the old custom item with the display name of the new
			 * custom item. (Even this might not always be ideal, but I don't think
			 * there is some 'perfect' behavior for this.)
			 */
			if (newItem.allowAnvilActions() && oldItem.getTranslations().isEmpty()) {
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
		} else if (newItem.getTranslations().size() != oldItem.getTranslations().size()) {
			NBT.modify(stackToUpgrade, nbt -> {
				wrap(newItem).translateName(nbt);
			});
		}
	}
	
	private void upgradeLore1(
			ItemMeta toUpgrade,
			KciItem oldItem, KciItem newItem,
			Long oldDurability, Long newDurability
	) {
		int oldTranslateLoreSize = 0;
		int newTranslateLoreSize = 0;
		if (!oldItem.getTranslations().isEmpty()) {
			oldTranslateLoreSize = oldItem.getTranslations().iterator().next().getLore().size();
		}
		if (!newItem.getTranslations().isEmpty()) {
			newTranslateLoreSize = newItem.getTranslations().iterator().next().getLore().size();
		}

		if (oldTranslateLoreSize == 0 && newTranslateLoreSize == 0) {
			if (!Objects.equals(oldDurability, newDurability)) {
				Long oldMaxDurability = oldItem instanceof KciTool ? ((KciTool) oldItem).getMaxDurabilityNew() : null;
				Long newMaxDurability = newItem instanceof KciTool ? ((KciTool) newItem).getMaxDurabilityNew() : null;
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

		if (oldTranslateLoreSize != 0 && newTranslateLoreSize == 0) {
			toUpgrade.setLore(wrap(newItem).createLore(newDurability));
		}
	}

	private void upgradeLore2(
			ItemStack stackToUpgrade, KciItem oldItem, KciItem newItem
	) {
		int oldTranslateLoreSize = 0;
		int newTranslateLoreSize = 0;
		if (!oldItem.getTranslations().isEmpty()) {
			oldTranslateLoreSize = oldItem.getTranslations().iterator().next().getLore().size();
		}
		if (!newItem.getTranslations().isEmpty()) {
			newTranslateLoreSize = newItem.getTranslations().iterator().next().getLore().size();
		}

		if (oldTranslateLoreSize != newTranslateLoreSize && newTranslateLoreSize != 0) {
			NBT.modify(stackToUpgrade, nbt -> {
				wrap(newItem).translateLore(nbt);
			});
		}
	}
	
	private void upgradeItemFlags(ItemMeta toUpgrade, KciItem oldItem, KciItem newItem) {
		/*
		 * We will only update the item flags that changed for optimal preservation
		 * of the custom values of the item stack being upgraded.
		 */
		List<Boolean> oldFlags = oldItem.getItemFlags();
		List<Boolean> newFlags = newItem.getItemFlags();
		boolean hadAttributes = !oldItem.getAttributeModifiers().isEmpty();
		boolean hasAttributes = !newItem.getAttributeModifiers().isEmpty();
		VItemFlag[] allFlags = VItemFlag.values();
		for (int flagIndex = 0; flagIndex < allFlags.length; flagIndex++) {
			boolean oldHasFlag = flagIndex < oldFlags.size() && oldFlags.get(flagIndex);
			boolean newHasFlag = flagIndex < newFlags.size() && newFlags.get(flagIndex);
			VItemFlag currentFlag = allFlags[flagIndex];
			
			// Yeah... there is a special and nasty edge case for the HIDE_ATTRIBUTES flag
			if (currentFlag == VItemFlag.HIDE_ATTRIBUTES) {
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
			}
			if ((currentFlag != VItemFlag.HIDE_ATTRIBUTES || hadAttributes == hasAttributes) && oldHasFlag != newHasFlag) {
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
			ItemStack oldStack, KciItem oldItem, KciItem newItem
	) throws CorruptedItemStackException {
		RawAttribute[] oldStackAttributes = KciNms.instance.items.getAttributes(oldStack);
		Collection<RawAttribute> newStackAttributes = new ArrayList<>(oldStackAttributes.length);

		ItemStack newStack = oldStack.clone();
		NBT.modify(newStack, nbt -> {
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
					for (KciAttributeModifier rawOldAttribute : oldItem.getAttributeModifiers()) {
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
		});

		return KciNms.instance.items.replaceAttributes(newStack, newStackAttributes.toArray(new RawAttribute[0]));
	}

	static void addEnchantmentsToMap(
			Map<VEnchantmentType, Integer> enchantmentMap,
			Collection<LeveledEnchantment> enchantments
	) {
		for (LeveledEnchantment enchantment : enchantments) {
			enchantmentMap.put(
					enchantment.getType(),
					enchantmentMap.getOrDefault(enchantment.getType(), 0) + enchantment.getLevel()
			);
		}
	}

	static Map<VEnchantmentType, Integer> determineEnchantmentAdjustments(
			Map<VEnchantmentType, Integer> oldKciEnchantments,
			Map<VEnchantmentType, Integer> newKciEnchantments
	) {
		Map<VEnchantmentType, Integer> adjustments = new HashMap<>();
		oldKciEnchantments.forEach((enchantment, level) ->
				adjustments.put(enchantment, adjustments.getOrDefault(enchantment, 0) - level)
		);
		newKciEnchantments.forEach((enchantment, level) ->
				adjustments.put(enchantment, adjustments.getOrDefault(enchantment, 0) + level)
		);
		return adjustments;
	}

	static ItemStack applyEnchantmentAdjustments(ItemStack itemStack, Map<VEnchantmentType, Integer> adjustments) {
		for (Map.Entry<VEnchantmentType, Integer> enchantmentEntry : adjustments.entrySet()) {
			VEnchantmentType enchantment = enchantmentEntry.getKey();
			int level = enchantmentEntry.getValue();

			if (level != 0) {
				int newLevel = BukkitEnchantments.getLevel(itemStack, enchantment) + level;
				if (newLevel > 0) itemStack = BukkitEnchantments.add(itemStack, enchantment, newLevel);
				else itemStack = BukkitEnchantments.remove(itemStack, enchantment);
			}
		}

		return itemStack;
	}
	
	private ItemStack upgradeEnchantments(ItemStack toUpgrade, KciItem oldItem, KciItem newItem) {

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
		ItemStack rememberToUpgrade = toUpgrade;
		class Result {
			Map<VEnchantmentType, Integer> oldKciEnchantments;
			Map<VEnchantmentType, Integer> newKciEnchantments;
		}

		Result modifyResult = NBT.modify(toUpgrade, nbt -> {
			Result result = new Result();
			if (ItemUpgrader.hasStoredEnchantmentUpgrades(nbt)) {
				result.oldKciEnchantments = ItemUpgrader.getEnchantmentUpgrades(nbt);
			} else {
				result.oldKciEnchantments = new HashMap<>();
				addEnchantmentsToMap(result.oldKciEnchantments, oldItem.getDefaultEnchantments());
			}

			result.newKciEnchantments = new HashMap<>();
			if (newItem != null) addEnchantmentsToMap(result.newKciEnchantments, newItem.getDefaultEnchantments());
			for (Upgrade upgrade : ItemUpgrader.getUpgrades(rememberToUpgrade, itemSet)) {
				addEnchantmentsToMap(result.newKciEnchantments, upgrade.getEnchantments());
			}
			ItemUpgrader.setEnchantmentUpgrades(nbt, result.newKciEnchantments);
			return result;
		});

		Map<VEnchantmentType, Integer> adjustments = determineEnchantmentAdjustments(
				modifyResult.oldKciEnchantments, modifyResult.newKciEnchantments
		);
		toUpgrade = applyEnchantmentAdjustments(toUpgrade, adjustments);

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
