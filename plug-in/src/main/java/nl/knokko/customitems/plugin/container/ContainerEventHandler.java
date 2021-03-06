package nl.knokko.customitems.plugin.container;

import java.util.List;

import nl.knokko.customitems.plugin.set.item.CustomPocketContainer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.inventory.PlayerInventory;

public class ContainerEventHandler implements Listener {
	
	private static PluginData pluginData() {
		return CustomItemsPlugin.getInstance().getData();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		// Delay it to prevent the items to be dropped while the block is still there
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), 
				() -> pluginData().destroyCustomContainersAt(
						event.getBlock().getLocation()
				)
		);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent event) {
		
		// Delay to prevent items from being destroyed by the explosion
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), () -> {
					for (Block block : event.blockList()) {
						pluginData().destroyCustomContainersAt(block.getLocation());
					}
				}
		);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		// Delay to prevent items from being destroyed by the explosion
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), () -> {
					for (Block block : event.blockList()) {
						pluginData().destroyCustomContainersAt(block.getLocation());
					}
				}
		);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			pluginData().onInventoryClose((Player) event.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void destroyPlaceholderItems(InventoryClickEvent event) {
		HumanEntity clicker = event.getWhoClicked();
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), () -> {
					ItemStack cursor = clicker.getItemOnCursor();
					if (GeneralItemNBT.readOnlyInstance(cursor).getOrDefault(
							ContainerInstance.PLACEHOLDER_KEY, 0) == 1) {
						clicker.setItemOnCursor(null);
					}
				}
		);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			ContainerInstance customContainer = pluginData().getCustomContainer(player);
			if (customContainer != null) {
				for (int slotIndex : event.getRawSlots()) {
					if (slotIndex >= 0 && slotIndex < 9 * customContainer.getType().getHeight()) {
						CustomSlot slot = customContainer.getType().getSlot(slotIndex % 9, slotIndex / 9);
						if (!slot.canInsertItems()) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			
			Player player = (Player) event.getWhoClicked();
			
			ContainerInstance customContainer = pluginData().getCustomContainer(player);
			if (customContainer != null) {
				
				int slotIndex = event.getRawSlot();
				CustomContainer containerType = customContainer.getType();
				
				// Check if the player clicked inside the custom container
				if (slotIndex >= 0 && slotIndex < 9 * containerType.getHeight()) {
					CustomSlot slot = customContainer.getType().getSlot(slotIndex % 9, slotIndex / 9);
					
					if (customContainer.getStoredExperience() > 0 && slot instanceof OutputCustomSlot) {
						player.giveExp(customContainer.getStoredExperience());
						customContainer.clearStoredExperience();
					}
					
					// Make sure slots can only be used the way they should be used
					if (isTake(event.getAction())) {
						if (!slot.canTakeItems() || ItemUtils.isEmpty(event.getCurrentItem())) {
							event.setCancelled(true);
						}
					} else if (isInsert(event.getAction())) {
						if (!slot.canInsertItems()) {
							event.setCancelled(true);
						}
					} else if (
							event.getAction() == InventoryAction.SWAP_WITH_CURSOR ||
							
							/*
							 * NOTHING is an interesting case, because it can occur
							 * when players attempt to stack stackable custom items
							 * in a slot.
							 * Because it's hard to predict if anything will happen
							 * regardless, we will be safe and only allow the action
							 * if the slot supports both insert and take actions.
							 */
							event.getAction() == InventoryAction.NOTHING) {
						if (
							// Placeholder items are considered empty
								(!slot.canTakeItems() && !ItemUtils.isEmpty(event.getCurrentItem()))
										|| !slot.canInsertItems()) {
							event.setCancelled(true);
						}
					} else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					    if (slot.canTakeItems()) {

							ItemSet set = CustomItemsPlugin.getInstance().getSet();
							ItemStack toTransfer = event.getCurrentItem();
							CustomItem customTransfer = set.getItem(toTransfer);

							// If it is a stackable custom item, we can improve the action by attempting to merge
							// the item stack with an existing item stack in the destination inventory
							if (customTransfer != null && customTransfer.canStack()) {
								event.setCancelled(true);

								int remainingAmount = toTransfer.getAmount();

                                Inventory bottomInv = event.getView().getBottomInventory();
                                for (int bottomIndex = 0; bottomIndex < bottomInv.getSize(); bottomIndex++) {
                                	ItemStack bottomStack = bottomInv.getItem(bottomIndex);
                                	CustomItem customBottom = set.getItem(bottomStack);
                                	if (customTransfer == customBottom) {
                                		int remainingSpace = customBottom.getMaxStacksize() - bottomStack.getAmount();
                                		if (remainingSpace > 0) {
                                			if (remainingAmount > remainingSpace) {
                                				bottomStack.setAmount(customBottom.getMaxStacksize());
                                				remainingAmount -= remainingSpace;
                                				if (remainingAmount == 0) {
                                					break;
												}
											} else {
                                				int newAmount = bottomStack.getAmount() + remainingAmount;
                                				bottomStack.setAmount(newAmount);
                                				remainingAmount = 0;
                                				break;
											}
										}
									}
								}

                                if (remainingAmount > 0) {
                                	for (int bottomIndex = 0; bottomIndex < bottomInv.getSize(); bottomIndex++) {
                                		ItemStack bottomStack = bottomInv.getItem(bottomIndex);
                                		if (ItemUtils.isEmpty(bottomStack)) {
                                			ItemStack finalStack = toTransfer.clone();
                                			finalStack.setAmount(remainingAmount);
                                			bottomInv.setItem(bottomIndex, finalStack);
                                			remainingAmount = 0;
                                			break;
										}
									}
								}

                                if (remainingAmount != toTransfer.getAmount()) {
									toTransfer.setAmount(remainingAmount);
								}
							}
							// If not, the default move to other inventory behavior is fine
						} else {
					    	event.setCancelled(true);
						}
					} else {
						
						// Some other inventory action occurred
						// I don't know whether or not this should be allowed
						// But better safe than sorry
						event.setCancelled(true);
					}
				} else {
					
					// If the player clicked outside the custom container, we need
					// to make sure he can't transfer the item to container inventory.
					if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						
						// I'm afraid I can't see to which slot the item will be
						// transferred, so better safe than sorry.
						event.setCancelled(true);
						
						ItemSet set = CustomItemsPlugin.getInstance().getSet();
						
						ItemStack toTransfer = event.getCurrentItem();
						CustomItem customTransfer = set.getItem(toTransfer);

						// First try to find a slot that already contains the item
						for (int y = 0; y < customContainer.getType().getHeight(); y++) {
							for (int x = 0; x < 9; x++) {
								CustomSlot slot = customContainer.getType().getSlot(x, y);
								if (slot.canInsertItems()) {
									ItemStack existingItem = event.getInventory().getItem(x + 9 * y);
									if (!ItemUtils.isEmpty(existingItem)) {
										int transferredAmount = 0;
										if (customTransfer != null) {
											CustomItem customExisting = set.getItem(existingItem);
											if (customExisting == customTransfer) {
												transferredAmount = Math.min(
														customTransfer.getMaxStacksize() 
														- existingItem.getAmount(),
														toTransfer.getAmount()
												);
											}
										} else if (toTransfer.isSimilar(existingItem)){
											transferredAmount = Math.min(
													existingItem.getMaxStackSize() 
													- existingItem.getAmount(), 
													toTransfer.getAmount()
											);
										}
										
										if (transferredAmount != 0) {
											existingItem.setAmount(
													existingItem.getAmount() 
													+ transferredAmount
											);
											toTransfer.setAmount(
													toTransfer.getAmount() - transferredAmount
											);
											if (toTransfer.getAmount() == 0) {
												return;
											}
										}
									}
								}
							}
						}
						
						// Place the remainder in a suitable slot that is still empty
						for (int y = 0; y < customContainer.getType().getHeight(); y++) {
							for (int x = 0; x < 9; x++) {
								CustomSlot slot = customContainer.getType().getSlot(x, y);
								if (slot.canInsertItems()) {
									ItemStack existing = event.getInventory().getItem(x + 9 * y);
									if (ItemUtils.isEmpty(existing)) {
										event.getInventory().setItem(x + 9 * y, toTransfer);
										event.setCurrentItem(null);
										return;
									}
								}
							}
						}
						
						// If no slot fits, do nothing
					}
				}
			}
			
			List<CustomContainer> containerSelection = pluginData().getCustomContainerSelection(event.getWhoClicked());
			if (containerSelection != null) {
				// Block any inventory action during container selection
				event.setCancelled(true);
				
				int slotIndex = event.getRawSlot();
				if (slotIndex == 0) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							CustomItemsPlugin.getInstance(), 
							event.getWhoClicked()::closeInventory
					);
					event.getWhoClicked().closeInventory();
				} else if (slotIndex <= containerSelection.size() && slotIndex >= 0) {
					CustomContainer toOpen = containerSelection.get(slotIndex - 1);
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							CustomItemsPlugin.getInstance(), 
							() -> pluginData().selectCustomContainer(
									(Player) event.getWhoClicked(), 
									toOpen
							)
					);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		pluginData().onPlayerQuit(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().isSneaking()) {
			
			String blockName = ItemHelper.getMaterialName(event.getClickedBlock());
			CIMaterial blockType;
			try {
				blockType = CIMaterial.valueOf(blockName);
			} catch (IllegalArgumentException unknownBlockTpe) {
				blockType = null;
			}
			
			VanillaContainerType vanillaType = VanillaContainerType.fromMaterial(blockType);
			Inventory menu = pluginData().getCustomContainerMenu(
					event.getClickedBlock().getLocation(), 
					event.getPlayer(), vanillaType
			);
			
			if (menu != null) {
				event.getPlayer().openInventory(menu);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void openPocketContainer(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			PlayerInventory inv = event.getPlayer().getInventory();
			ItemSet set = CustomItemsPlugin.getInstance().getSet();
			CustomItem customMain = set.getItem(inv.getItemInMainHand());
			CustomItem customOff = set.getItem(inv.getItemInOffHand());

			// Prevent players from opening 2 pocket containers at the same time
			if (
					customMain instanceof CustomPocketContainer
							&& customOff instanceof CustomPocketContainer
							&& event.getHand() != EquipmentSlot.HAND
			) {
				return;
			}

			CustomItem customItem = set.getItem(event.getItem());
			if (customItem instanceof CustomPocketContainer) {
				CustomPocketContainer pocketContainer = (CustomPocketContainer) customItem;
				pluginData().openPocketContainerMenu(event.getPlayer(), pocketContainer);
			}
		}
	}
	
	private boolean isTake(InventoryAction action) {
		return action == InventoryAction.PICKUP_ONE ||
				action == InventoryAction.PICKUP_SOME ||
				action == InventoryAction.PICKUP_HALF ||
				action == InventoryAction.PICKUP_ALL;
	}
	
	private boolean isInsert(InventoryAction action) {
		return action == InventoryAction.PLACE_ONE ||
				action == InventoryAction.PLACE_SOME ||
				action == InventoryAction.PLACE_ALL ||
				action == InventoryAction.COLLECT_TO_CURSOR;
	}
}
