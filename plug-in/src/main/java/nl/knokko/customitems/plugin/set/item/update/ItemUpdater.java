package nl.knokko.customitems.plugin.set.item.update;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.*;
import static org.bukkit.enchantments.Enchantment.getByName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
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

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.item.BooleanRepresentation;
import nl.knokko.customitems.plugin.set.item.CustomItemNBT;

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
		updateEquipmentPiece(equipment.getItemInMainHand(), equipment::setItemInMainHand);
		updateEquipmentPiece(equipment.getItemInOffHand(), equipment::setItemInOffHand);
		updateEquipmentPiece(equipment.getHelmet(), equipment::setHelmet);
		updateEquipmentPiece(equipment.getChestplate(), equipment::setChestplate);
		updateEquipmentPiece(equipment.getLeggings(), equipment::setLeggings);
		updateEquipmentPiece(equipment.getBoots(), equipment::setBoots);
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
		
		GeneralItemNBT preNbt = GeneralItemNBT.readOnlyInstance(originalStack);
		if (
				// If players somehow obtain placeholder items, get rid of those!
				preNbt.getOrDefault(ContainerInstance.PLACEHOLDER_KEY, 0) == 1
						// And also destroy duplicated Dual Wield items
				|| DualWieldSupport.isCorrupted(preNbt)
		) {
			return null;
		}
		
		CustomItemValues[] pOldItem = {null};
		CustomItemValues[] pNewItem = {null};
		UpdateAction[] pAction = {null};
		
		CustomItemNBT.readOnly(originalStack, nbt -> {
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
						
						BooleanRepresentation oldBoolRepresentation = nbt.getBooleanRepresentation();
						BooleanRepresentation newBoolRepresentation = new BooleanRepresentation(currentItem.getBooleanRepresentation());
						if (oldBoolRepresentation.equals(newBoolRepresentation)) {
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
								//CustomItem oldItem = ItemSet.loadOldItem(oldBoolRepresentation, currentItem);
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
				/*
				 * If the item stack doesn't have our nbt tag, we assume it is not a
				 * custom item and we thus shouldn't mess with it.
				 */
				pAction[0] = UpdateAction.DO_NOTHING;
			}
		});
		
		UpdateAction action = pAction[0];
		
		if (action == UpdateAction.DO_NOTHING) {
			return originalStack;
		} else if (action == UpdateAction.DESTROY) {
			return null;
		} else if (action == UpdateAction.UPDATE_LAST_EXPORT_TIME) {
			ItemStack[] pResult = {null};
			CustomItemNBT.readWrite(originalStack, nbt -> {
				nbt.setLastExportTime(this.itemSet.get().getExportTime());
			}, result -> pResult[0] = result);
			return pResult[0];
		} else {
			
			CustomItemValues newItem = pNewItem[0];
			if (action == UpdateAction.INITIALIZE) {
				return wrap(newItem).create(originalStack.getAmount());
			} else if (action == UpdateAction.UPGRADE) {
				
				CustomItemValues oldItem = pOldItem[0];
				return upgradeItem(originalStack, oldItem, newItem);
			} else {
				throw new Error("Unknown update action: " + action);
			}
		}
	}
	
	private ItemStack upgradeItem(ItemStack oldStack, CustomItemValues oldItem, CustomItemValues newItem) {
		
		// We start with the attribute modifiers
		ItemAttributes.Single[] newStackAttributes = determineUpgradedAttributes(
				oldStack, oldItem, newItem
		);
		
		ItemStack newStack = ItemAttributes.replaceAttributes(oldStack, newStackAttributes);

		ItemStack[] pNewStack = {null};
		Long[] pOldDurability = {null};
		Long[] pNewDurability = {null};
		CustomItemNBT.readWrite(newStack, nbt -> {
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
		
		GeneralItemNBT generalNbt = GeneralItemNBT.readWriteInstance(newStack);
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
		
		upgradeEnchantments(newStack, oldItem, newItem);

		ItemHelper.setMaterial(newStack, CustomItemWrapper.getMaterial(newItem.getItemType(), newItem.getOtherMaterial()).name());
		
		ItemMeta meta = newStack.getItemMeta();
		
		upgradeDisplayName(meta, oldItem, newItem);
		upgradeLore(meta, oldItem, newItem, pOldDurability[0], pNewDurability[0]);
		upgradeItemFlags(meta, oldItem, newItem);

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
		if (!Objects.equals(oldDurability, newDurability) || !Objects.deepEquals(oldItem.getLore(), newItem.getLore())) {
			/*
			 * I will do no attempt to 'upgrade' the lore rather than replacing it,
			 * because tools will overwrite lore each time they take durability
			 * anyway.
			 */
			toUpgrade.setLore(wrap(newItem).createLore(newDurability));
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
	
	private ItemAttributes.Single[] determineUpgradedAttributes(
			ItemStack oldStack, CustomItemValues oldItem, CustomItemValues newItem
	) {
		
		/*
		 * If the oldStack had attribute modifiers that didn't come from oldItem,
		 * we should keep them. (Except if that attribute modifier is already part
		 * of the attribute modifiers of newItem.) This makes integration with other 
		 * plug-ins that assign attribute modifiers a bit nicer.
		 */
		ItemAttributes.Single[] oldStackAttributes = ItemAttributes.getAttributes(oldStack);
		Collection<ItemAttributes.Single> newStackAttributes = new ArrayList<>(
				oldStackAttributes.length - oldItem.getAttributeModifiers().size()
				+ newItem.getAttributeModifiers().size()
		);
		
		oldStackLoop:
		for (ItemAttributes.Single oldStackAttribute : oldStackAttributes) {
			for (AttributeModifierValues rawOldAttribute : oldItem.getAttributeModifiers()) {
				ItemAttributes.Single oldItemAttribute = convertAttributeModifier(rawOldAttribute);
				if (oldStackAttribute.equals(oldItemAttribute)) {
					continue oldStackLoop;
				}
			}
			for (AttributeModifierValues rawNewAttribute : newItem.getAttributeModifiers()) {
				ItemAttributes.Single newItemAttribute = convertAttributeModifier(rawNewAttribute);
				if (oldStackAttribute.equals(newItemAttribute)) {
					continue oldStackLoop;
				}
			}
			
			// Don't stack dummy attributes
			if (oldStackAttribute.isDummy()) {
				continue oldStackLoop;
			}
			
			newStackAttributes.add(oldStackAttribute);
		}
		
		// Obviously, we should also add the attribute modifiers of newItem
		for (AttributeModifierValues rawNewAttribute : newItem.getAttributeModifiers()) {
			newStackAttributes.add(convertAttributeModifier(rawNewAttribute));
		}
		
		return newStackAttributes.toArray(new ItemAttributes.Single[0]);
	}
	
	private void upgradeEnchantments(ItemStack toUpgrade, CustomItemValues oldItem, CustomItemValues newItem) {
		
		/*
		 * If the new item doesn't allow anvil actions, it should not be possible to
		 * add enchantments to it, so the item to upgrade should get only the default
		 * enchantments of the new item.
		 */
		if (!newItem.allowAnvilActions()) {
			toUpgrade.getEnchantments().keySet().forEach(toUpgrade::removeEnchantment);
			for (EnchantmentValues enchantment : newItem.getDefaultEnchantments()) {
				toUpgrade.addUnsafeEnchantment(
						getByName(enchantment.getType().name()), 
						enchantment.getLevel()
				);
			}
		} else {
			
			class ChangedEnchantment {
				
				final EnchantmentType type;
				final int oldLevel, newLevel;
				
				ChangedEnchantment(EnchantmentType type, int oldLevel, int newLevel) {
					this.type = type;
					this.oldLevel = oldLevel;
					this.newLevel = newLevel;
				}
			}
			
			Collection<EnchantmentValues> removedEnchantments = new ArrayList<>();
			Collection<EnchantmentValues> addedEnchantments = new ArrayList<>();
			Collection<ChangedEnchantment> changedEnchantments = new ArrayList<>();
			
			// Find out which enchantments are removed and which are upgraded
			outerLoop:
			for (EnchantmentValues oldEnchantment : oldItem.getDefaultEnchantments()) {
				for (EnchantmentValues newEnchantment : newItem.getDefaultEnchantments()) {
					if (oldEnchantment.getType() == newEnchantment.getType()) {
						if (oldEnchantment.getLevel() != newEnchantment.getLevel()) {
							changedEnchantments.add(new ChangedEnchantment(
									oldEnchantment.getType(), 
									oldEnchantment.getLevel(),
									newEnchantment.getLevel()
							));
						}
						continue outerLoop;
					}
				}
				
				removedEnchantments.add(oldEnchantment);
			}
			
			// Find out which enchantments are added
			outerLoop:
			for (EnchantmentValues newEnchantment : newItem.getDefaultEnchantments()) {
				for (EnchantmentValues oldEnchantment : oldItem.getDefaultEnchantments()) {
					if (newEnchantment.getType() == oldEnchantment.getType()) {
						continue outerLoop;
					}
				}
				
				addedEnchantments.add(newEnchantment);
			}
			
			for (EnchantmentValues removed : removedEnchantments) {
				int currentLevel = toUpgrade.getEnchantmentLevel(
						getByName(removed.getType().name())
				);
				
				/*
				 * This case is a bit nasty because it is possible that the item to
				 * upgrade has the removed enchantment at a higher level than the
				 * level of the default enchantment of the old custom item. I'm not
				 * really sure what the desired behavior should be, but I will go
				 * with the following decision:
				 * 
				 * The enchantment level of the item to upgrade will be decreased by
				 * the level of the default enchantment of the old custom item.
				 */
				int newLevel = currentLevel - removed.getLevel();
				if (newLevel > 0) {
					toUpgrade.addUnsafeEnchantment(getByName(removed.getType().name()), newLevel);
				} else {
					toUpgrade.removeEnchantment(getByName(removed.getType().name()));
				}
			}
			
			for (EnchantmentValues added : addedEnchantments) {
				int currentLevel = toUpgrade.getEnchantmentLevel(
						getByName(added.getType().name())
				);
				
				/*
				 * It is possible that the item to upgrade already has the
				 * enchantment of the new default enchantment. Again, I'm not
				 * entirely sure what the desired behavior would be, but I will go
				 * with the following decision:
				 * 
				 * The enchantment level of the item to upgrade will be set to the
				 * maximum of the current level and the level of the new default
				 * enchantment.
				 */
				if (added.getLevel() > currentLevel) {
					toUpgrade.addUnsafeEnchantment(
							getByName(added.getType().name()), 
							added.getLevel()
					);
				}
			}
			
			for (ChangedEnchantment changed : changedEnchantments) {
				int currentLevel = toUpgrade.getEnchantmentLevel(
						getByName(changed.type.name())
				);
				
				if (changed.newLevel > changed.oldLevel) {
					
					// Make the same decision as for adding a new enchantment
					if (changed.newLevel > currentLevel) {
						toUpgrade.addUnsafeEnchantment(
								getByName(changed.type.name()), 
								changed.newLevel
						);
					}
				} else {
					// Decrease the current enchantment level
					int newLevel = currentLevel + changed.newLevel - changed.oldLevel;
					if (newLevel > 0) {
						toUpgrade.addUnsafeEnchantment(getByName(changed.type.name()), newLevel);
					} else {
						toUpgrade.removeEnchantment(getByName(changed.type.name()));
					}
				}
			}
		}
	}

	private enum UpdateAction {
		
		DO_NOTHING,
		UPDATE_LAST_EXPORT_TIME,
		INITIALIZE,
		UPGRADE,
		DESTROY
	}
}
