package nl.knokko.customitems.plugin.events;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.nms.CorruptedItemStackException;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.customitems.plugin.tasks.updater.ItemUpdater;

public class MiscellaneousEventHandler implements Listener {

	private final ItemSetWrapper itemSet;

	public MiscellaneousEventHandler(ItemSetWrapper itemSet) {
		this.itemSet = itemSet;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void cancelEnchanting(PrepareItemEnchantEvent event) {
		KciItem custom = itemSet.getItem(event.getItem());
		if (custom != null && !custom.allowEnchanting()) event.setCancelled(true);
	}

	private boolean fixCustomItemPickup(final ItemStack stack, ItemStack[] contents) {
		KciItem customItem = itemSet.getItem(stack);
		if (customItem != null) {
			int remainingAmount = stack.getAmount();
			for (ItemStack content : contents) {
				if (wrap(customItem).is(content)) {
					int remainingSpace = customItem.getMaxStacksize() - content.getAmount();
					if (remainingSpace > 0) {
						if (remainingSpace >= remainingAmount) {
							content.setAmount(content.getAmount() + remainingAmount);
							stack.setAmount(0);
							return true;
						} else {
							content.setAmount(customItem.getMaxStacksize());
							remainingAmount -= remainingSpace;
						}
					}
				}
			}

			if (remainingAmount != stack.getAmount()) {
				stack.setAmount(remainingAmount);

				// Apparently, canceling the event is necessary because it won't let me change
				// the picked up amount.
				return true;
			}
		}

		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemPickup(EntityPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			Inventory inv = player.getInventory();
			ItemStack[] contents = inv.getContents();
			int oldAmount = stack.getAmount();
			if (fixCustomItemPickup(stack, contents)) {
				event.setCancelled(true);
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3f, 2.0f);
			}
			if (stack.getAmount() != oldAmount) {
				if (stack.getAmount() > 0) {
					event.getItem().setItemStack(stack);
				} else {
					event.getItem().remove();
				}
			}
		}
	}

	private void updateEquipment(LivingEntity entity) {
		ItemUpdater updater = CustomItemsPlugin.getInstance().getItemUpdater();
		try {
			updater.updateEquipment(entity.getEquipment());
		} catch (CorruptedItemStackException corrupted) {
			Bukkit.getLogger().warning("Encountered corrupted item stack in equipment of just-spawned " + entity);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
	public void upgradeMobEquipment(CreatureSpawnEvent event) {
		updateEquipment(event.getEntity());

		/*
		 * This (somewhat dirty) code improves the integration with MythicMobs. For some reason,
		 * when a mythic mob is spawned with items, the items are given ~10 ticks after the mob
		 * is spawned. I don't know why this is, but I had better deal with it because MythicMobs
		 * is a very popular plug-in. I do multiple attempts because I don't want to rely on a single
		 * magic value. Note: even if all attempts are too early, the ItemUpdater will kick in within
		 * 5 seconds.
		 */
        for (int attempt = 1; attempt < 8; attempt++) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(
					CustomItemsPlugin.getInstance(), () -> updateEquipment(event.getEntity()), attempt * 4
			);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void fixHopperPickup(InventoryPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		int oldAmount = stack.getAmount();
		if (fixCustomItemPickup(stack, event.getInventory().getContents())) {
			event.setCancelled(true);
		}
		if (oldAmount != stack.getAmount()) {
			if (stack.getAmount() > 0) {
				event.getItem().setItemStack(stack);
			} else {
				event.getItem().remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void fixHopperTransport(InventoryMoveItemEvent event) {
		ItemStack stack = event.getItem();
		KciItem customStack = itemSet.getItem(stack);
		if (fixCustomItemPickup(stack, event.getDestination().getContents())) {
			event.setCancelled(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

				// We need to consume the transferred item from the source inventory,
				// but without letting it enter the destination inventory because we do that manually.
				// Simply canceling the event will not remove the item from the source inventory,
				// so we need to do that manually as well.
				ItemStack[] contents = event.getSource().getContents();
				for (ItemStack content : contents) {

					// customStack can't be null because fixCustomItemPickup would have returned false then
					if (wrap(customStack).is(content)) {

						// We rely here on the fact that hoppers won't move more than 1 item at a time
						content.setAmount(content.getAmount() - 1);
						break;
					}
				}
			});
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void fixShulkerBoxes(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getState() instanceof ShulkerBox) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.isDropItems()) {
				ShulkerBox shulker = (ShulkerBox) block.getState();
				event.setDropItems(false);

				ItemStack stackToDrop = KciNms.instance.items.createStack(KciNms.instance.items.getMaterialName(block), 1);
				ItemMeta meta = stackToDrop.getItemMeta();
				BlockStateMeta bms = (BlockStateMeta) meta;
				bms.setBlockState(shulker);
				if (shulker.getCustomName() != null) {
					bms.setDisplayName(shulker.getCustomName());
				}
				stackToDrop.setItemMeta(bms);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stackToDrop);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void enforceIndestructibleDroppedCustomItems(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.VOID && event.getEntity() instanceof Item) {
			Item droppedItem = (Item) event.getEntity();
			KciItem customItem = itemSet.getItem(droppedItem.getItemStack());

			if (customItem != null && customItem.isIndestructible()) {
				event.setCancelled(true);

				// Making the item invulnerable is not absolutely required, but it will prevent subsequent
				// EntityDamageEvent's from being fired for this item
				droppedItem.setInvulnerable(true);
			}
		}
	}
}
