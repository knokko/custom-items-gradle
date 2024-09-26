package nl.knokko.customitems.plugin.container;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.ContainerHost;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciPocketContainer;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.events.CustomContainerActionEvent;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import nl.knokko.customitems.plugin.util.NbtHelper;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.container.VContainerType;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;

import static nl.knokko.customitems.plugin.container.ContainerRecipeWrapper.wrap;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class ContainerEventHandler implements Listener {
	
	private static PluginData pluginData() {
		return CustomItemsPlugin.getInstance().getData();
	}

	private final ItemSetWrapper itemSet;

	public ContainerEventHandler(ItemSetWrapper itemSet) {
		this.itemSet = itemSet;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		// Delay it to prevent the items to be dropped while the block is still there
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), 
				() -> pluginData().containerManager.destroyCustomContainersAt(
						event.getBlock().getLocation()
				)
		);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		pluginData().containerManager.handleEntityDeath(event.getEntity());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkUnload(ChunkUnloadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {
			pluginData().containerManager.handleEntityUnload(entity);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockExplode(BlockExplodeEvent event) {
		
		// Delay to prevent items from being destroyed by the explosion
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), () -> {
					for (Block block : event.blockList()) {
						pluginData().containerManager.destroyCustomContainersAt(block.getLocation());
					}
				}
		);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		// Delay dropping the items to prevent the items from being destroyed by the explosion
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), () -> {
					for (Block block : event.blockList()) {
						pluginData().containerManager.destroyCustomContainersAt(block.getLocation());
					}
				}
		);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			pluginData().containerSelections.close((Player) event.getPlayer());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void destroyPlaceholderItems(InventoryClickEvent event) {
		HumanEntity clicker = event.getWhoClicked();
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				CustomItemsPlugin.getInstance(), () -> {
					ItemStack cursor = clicker.getItemOnCursor();
					if (cursor.getAmount() > 0 && cursor.getType() != Material.AIR &&
							NBT.get(cursor, nbt -> NbtHelper.getNested(nbt, ContainerInstance.PLACEHOLDER_KEY, 0) == 1)) {
						clicker.setItemOnCursor(null);
					}
				}
		);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			ContainerInstance customContainer = pluginData().containerManager.getOpened(player);
			if (customContainer != null) {
				if (event.getNewItems().values().stream().anyMatch(item -> !canStore(item, customContainer))) {
					event.setCancelled(true);
					return;
				}

				for (int slotIndex : event.getRawSlots()) {
					if (slotIndex >= 0 && slotIndex < 9 * customContainer.getType().getHeight()) {
						ContainerSlot slot = customContainer.getType().getSlot(slotIndex % 9, slotIndex / 9);
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
			
			ContainerInstance customContainer = pluginData().containerManager.getOpened(player);
			if (customContainer != null) {
				
				int slotIndex = event.getRawSlot();
				KciContainer containerType = customContainer.getType();
				
				// Check if the player clicked inside the custom container
				if (slotIndex >= 0 && slotIndex < 9 * containerType.getHeight()) {
					ContainerSlot slot = customContainer.getType().getSlot(slotIndex % 9, slotIndex / 9);
					
					if (customContainer.getStoredExperience() > 0 && slot instanceof OutputSlot) {
						player.giveExp(customContainer.getStoredExperience());
						customContainer.clearStoredExperience();
					}

					ContainerRecipe currentManualRecipe = null;
					boolean stackManualResultOnCursor = false;

					if (slot instanceof ManualOutputSlot) {
						currentManualRecipe = customContainer.getCurrentRecipe();
						if (currentManualRecipe != null && currentManualRecipe.getManualOutput() == null) {
							currentManualRecipe = null;
						}

						// This extra check is needed because the customContainer.getCurrentRecipe() can be outdated
						if (currentManualRecipe != null && customContainer.determineCurrentRecipe(currentManualRecipe) != currentManualRecipe) {
							currentManualRecipe = null;
						}

						if (currentManualRecipe != null && !ItemUtils.isEmpty(event.getCursor())) {
							KciItem customCursor = itemSet.getItem(event.getCursor());
							if (customCursor == null) {
								ItemStack manualOutputStack = wrap(
										currentManualRecipe, customContainer.getCurrentIngredients()
								).getManualOutput();
								if (!event.getCursor().isSimilar(manualOutputStack) || event.getCursor().getAmount() + manualOutputStack.getAmount() > event.getCursor().getMaxStackSize()) {
									currentManualRecipe = null;
								}
							} else {
								if (currentManualRecipe.getManualOutput() instanceof CustomItemResult) {

									CustomItemResult customResult = (CustomItemResult) currentManualRecipe.getManualOutput();
									if (customResult.getItem() != customCursor || customResult.getAmount() + event.getCursor().getAmount() > customCursor.getMaxStacksize()) {
										currentManualRecipe = null;
									}
								} else {
									currentManualRecipe = null;
								}
							}

							if (currentManualRecipe != null) {
								stackManualResultOnCursor = true;
							}
						}
					} else if (slot instanceof LinkSlot) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							pluginData().containerManager.attemptToSwitchToLinkedContainer(
									player, ((LinkSlot) slot).getLinkedContainer()
							);
						});
					} else if (slot instanceof ActionSlot) {
						Bukkit.getPluginManager().callEvent(new CustomContainerActionEvent(
								((ActionSlot) slot).getActionID(), customContainer, event, itemSet
						));
					}

					boolean consumeManualRecipeOnce = false;
					
					// Make sure slots can only be used the way they should be used
					if (isTake(event.getAction())) {
						if (!slot.canTakeItems() || ItemUtils.isEmpty(event.getCurrentItem())) {
							if (currentManualRecipe != null) {
								consumeManualRecipeOnce = true;
							}
							event.setCancelled(true);
						}
					} else if (isInsert(event.getAction())) {
						if (!slot.canInsertItems() || !canStore(event.getCursor(), customContainer)) {
							if (currentManualRecipe != null) {
								consumeManualRecipeOnce = true;
							}
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
										|| !slot.canInsertItems() || !canStore(event.getCursor(), customContainer)) {
							event.setCancelled(true);
						}
						if (currentManualRecipe != null) {
							consumeManualRecipeOnce = true;
						}
					} else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					    if (slot.canTakeItems()) {

							ItemStack toTransfer = event.getCurrentItem();
							KciItem customTransfer = itemSet.getItem(toTransfer);

							// If it is a stackable custom item, we can improve the action by attempting to merge
							// the item stack with an existing item stack in the destination inventory
							if (customTransfer != null && wrap(customTransfer).needsStackingHelp()) {
								event.setCancelled(true);

								int remainingAmount = toTransfer.getAmount();

                                Inventory bottomInv = event.getView().getBottomInventory();
                                for (int bottomIndex = 0; bottomIndex < bottomInv.getSize(); bottomIndex++) {
                                	ItemStack bottomStack = bottomInv.getItem(bottomIndex);
                                	KciItem customBottom = itemSet.getItem(bottomStack);
                                	if (customTransfer == customBottom) {
                                		int remainingSpace = customBottom.getMaxStacksize() - bottomStack.getAmount();
                                		if (remainingSpace > 0) {
                                			if (remainingAmount > remainingSpace) {
                                				bottomStack.setAmount(customBottom.getMaxStacksize());
                                				remainingAmount -= remainingSpace;
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
							if (currentManualRecipe != null) {

								int numRecipeExecutions = 0;
								Map<String, ItemStack> originalIngredients = customContainer.getCurrentIngredients();
								while (customContainer.determineCurrentRecipe(currentManualRecipe) == currentManualRecipe) {
									customContainer.consumeIngredientsAndEnergyOfCurrentRecipe();
									numRecipeExecutions += 1;
								}

								if (currentManualRecipe.getExperience() > 0) {
									player.giveExp(numRecipeExecutions * currentManualRecipe.getExperience());
								}

								ItemStack baseResultStack = wrap(
										currentManualRecipe, originalIngredients
								).getManualOutput();
								KciItem customResult = currentManualRecipe.getManualOutput() instanceof CustomItemResult ?
										((CustomItemResult) currentManualRecipe.getManualOutput()).getItem() : null;
								int maxStackSize = customResult != null ? customResult.getMaxStacksize() : baseResultStack.getMaxStackSize();

								int finalNumRecipeExecutions = numRecipeExecutions;
								Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

									int remainingAmount = finalNumRecipeExecutions * baseResultStack.getAmount();

									// First merge with existing item stacks in the player inventory
									ItemStack[] contents = player.getInventory().getStorageContents();
									for (ItemStack content : contents) {
										if (remainingAmount <= 0) {
											break;
										}

										KciItem customContent = itemSet.getItem(content);
										boolean canMerge;
										if (customResult == null) {
											canMerge = customContent == null && baseResultStack.isSimilar(content);
										} else {
											canMerge = customContent == customResult;
										}
										if (canMerge) {
											int amountToPut = Math.min(remainingAmount, maxStackSize - content.getAmount());
											if (amountToPut > 0) {
												content.setAmount(content.getAmount() + amountToPut);
												remainingAmount -= amountToPut;
											}
										}
									}

									// Then place the remaining items in empty slots in the inventory
									for (int index = 0; index < contents.length; index++) {
										if (remainingAmount <= 0) {
											break;
										}
										if (ItemUtils.isEmpty(contents[index])) {
											int amountToPut = Math.min(remainingAmount, maxStackSize);
											contents[index] = baseResultStack.clone();
											contents[index].setAmount(amountToPut);
											remainingAmount -= amountToPut;
										}
									}

									player.getInventory().setStorageContents(contents);

									// If not everything fits in the inventory, the leftovers will be dropped on the floor
									while (remainingAmount > 0) {
										int amountToDrop = Math.min(remainingAmount, maxStackSize);
										ItemStack stackToDrop = baseResultStack.clone();
										stackToDrop.setAmount(amountToDrop);
										player.getWorld().dropItem(player.getLocation(), stackToDrop);
										remainingAmount -= amountToDrop;
									}
								});
							}
					    	event.setCancelled(true);
						}
					} else {
						
						// Some other inventory action occurred
						// I don't know whether this should be allowed
						// But better safe than sorry
						event.setCancelled(true);
					}

					if (consumeManualRecipeOnce) {
						Map<String, ItemStack> originalIngredients = customContainer.getCurrentIngredients();
						customContainer.consumeIngredientsAndEnergyOfCurrentRecipe();
						if (currentManualRecipe.getExperience() > 0) {
							player.giveExp(currentManualRecipe.getExperience());
						}
						ContainerRecipe finalManualRecipe = currentManualRecipe;
						boolean finalStackResultOnCursor = stackManualResultOnCursor;
						Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
							ItemStack cursor = wrap(finalManualRecipe, originalIngredients).getManualOutput();
							if (finalStackResultOnCursor) {
								cursor.setAmount(cursor.getAmount() + event.getCursor().getAmount());
							}
							player.setItemOnCursor(cursor);
						});
					}
				} else {

					// Prevent players from moving their opened pocket container
					if (event.getSlot() == player.getInventory().getHeldItemSlot()) {
						if (itemSet.getItem(event.getCurrentItem()) instanceof KciPocketContainer) {
							event.setCancelled(true);
							return;
						}
					}

					// If the player clicked outside the custom container, we need
					// to make sure he can't transfer the item to container inventory.
					if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						
						// I'm afraid I can't see to which slot the item will be
						// transferred, so better safe than sorry.
						event.setCancelled(true);

						ItemStack toTransfer = event.getCurrentItem();
						if (!canStore(toTransfer, customContainer)) return;
						KciItem customTransfer = itemSet.getItem(toTransfer);

						// First try to find a slot that already contains the item
						for (int y = 0; y < customContainer.getType().getHeight(); y++) {
							for (int x = 0; x < 9; x++) {
								ContainerSlot slot = customContainer.getType().getSlot(x, y);
								if (slot.canInsertItems()) {
									ItemStack existingItem = event.getInventory().getItem(x + 9 * y);
									if (!ItemUtils.isEmpty(existingItem)) {
										int transferredAmount = 0;
										if (customTransfer != null) {
											KciItem customExisting = itemSet.getItem(existingItem);
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
								ContainerSlot slot = customContainer.getType().getSlot(x, y);
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
			
			List<KciContainer> containerSelection = pluginData().containerSelections.getShown(event.getWhoClicked());
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
					KciContainer toOpen = containerSelection.get(slotIndex - 1);
					Bukkit.getScheduler().scheduleSyncDelayedTask(
							CustomItemsPlugin.getInstance(), 
							() -> pluginData().containerManager.selectCustomContainer(
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

		if (event.getClickedBlock() != null && !CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(event.getClickedBlock().getLocation())) {
			return;
		}

		// Ensure the code below is only executed once rather than for both offhand and mainhand
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().isSneaking()) {
			
			String blockName = KciNms.instance.items.getMaterialName(event.getClickedBlock());
			VMaterial blockType;
			try {
				blockType = VMaterial.valueOf(blockName);
			} catch (IllegalArgumentException unknownBlockTpe) {
				blockType = null;
			}

			VContainerType vanillaType = VContainerType.fromMaterial(blockType);
			if (vanillaType != null) {
				Inventory menu = pluginData().containerSelections.getBlockContainerMenu(
						event.getClickedBlock().getLocation(),
						event.getPlayer(), new ContainerHost(vanillaType)
				);

				if (menu != null) {
					event.getPlayer().openInventory(menu);
				}
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isSneaking()) {
			try {
				String blockName = KciNms.instance.items.getMaterialName(event.getClickedBlock());
				VMaterial blockType = VMaterial.valueOf(blockName);
				Inventory maybeMenu = pluginData().containerSelections.getBlockContainerMenu(
						event.getClickedBlock().getLocation(), event.getPlayer(), new ContainerHost(blockType)
				);

				if (maybeMenu != null) {
					event.getPlayer().openInventory(maybeMenu);
				}
			} catch (IllegalArgumentException unknownBlockTpe) {
				// There is no need to do anything in this case
			}

			KciBlock maybeCustomBlock = MushroomBlockHelper.getMushroomBlock(event.getClickedBlock());
			if (maybeCustomBlock != null) {
				BlockReference customBlockReference = itemSet.get().blocks.getReference(maybeCustomBlock.getInternalID());
				Inventory maybeMenu = pluginData().containerSelections.getBlockContainerMenu(
						event.getClickedBlock().getLocation(), event.getPlayer(), new ContainerHost(customBlockReference)
				);
				if (maybeMenu != null) {
					event.getPlayer().openInventory(maybeMenu);
				}
			}
		}
	}

	@EventHandler
	public void openEntityContainer(PlayerInteractAtEntityEvent event) {
		if (event.getHand() == EquipmentSlot.HAND) {
			if (CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(event.getRightClicked().getLocation())) {
				pluginData().containerSelections.openEntityContainerMenu(event.getPlayer(), event.getRightClicked());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void openPocketContainer(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			PlayerInventory inv = event.getPlayer().getInventory();
			KciItem customMain = itemSet.getItem(inv.getItemInMainHand());
			KciItem customOff = itemSet.getItem(inv.getItemInOffHand());

			// Prevent players from opening 2 pocket containers at the same time
			if (
					customMain instanceof KciPocketContainer
							&& customOff instanceof KciPocketContainer
							&& event.getHand() != EquipmentSlot.HAND
			) {
				return;
			}

			KciItem customItem = itemSet.getItem(event.getItem());
			if (customItem instanceof KciPocketContainer) {
				KciPocketContainer pocketContainer = (KciPocketContainer) customItem;
				Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(),
						() ->pluginData().containerSelections.openPocketContainerMenu(event.getPlayer(), pocketContainer)
				);

				for (String armorType : new String[] { "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"}) {
					if (event.getMaterial().name().contains(armorType)) event.setCancelled(true);
				}
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

	private boolean canStore(ItemStack item, ContainerInstance destination) {
		KciItem customItem = itemSet.getItem(item);
		if (destination.getStorageKey().isPocketContainer() && customItem instanceof KciPocketContainer) {
			for (KciContainer pocketContainer : ((KciPocketContainer) customItem).getContainers()) {
				if (9 * pocketContainer.getHeight() >= destination.getInventory().getSize()) return false;
			}
		}

		return true;
	}
}
