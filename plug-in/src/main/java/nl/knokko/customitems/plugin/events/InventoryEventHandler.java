package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.multisupport.geyser.GeyserSupport;
import nl.knokko.customitems.plugin.recipe.IngredientEntry;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.*;

import static nl.knokko.customitems.MCVersions.VERSION1_13;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptAmountless;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.enchantments.Enchantment.VANISHING_CURSE;

public class InventoryEventHandler implements Listener {

    private final ItemSetWrapper itemSet;
    private final Map<UUID, List<IngredientEntry>> shouldInterfere = new HashMap<>();

    public InventoryEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryType.SlotType type = event.getSlotType();
        InventoryAction action = event.getAction();

        // The CREATIVE ClickType can't be handled properly because it is unknown whether the player pressed
        // shift, which button was used, and a lot of other stuff. Return early to prevent weird reactions.
        if (event.getClick() == ClickType.CREATIVE) {
            return;
        }

        if (type == InventoryType.SlotType.RESULT) {
            if (event.getInventory().getType().name().equals("GRINDSTONE")) {
                ItemStack[] ingredients = event.getInventory().getStorageContents();
                boolean custom1 = itemSet.getItem(ingredients[0]) != null;
                boolean custom2 = itemSet.getItem(ingredients[1]) != null;

                /*
                 * Without this check, it is possible to use an enchanted custom item with in one slot of a grindstone
                 * and a vanilla item with the same internal item type in the other slot. We clearly don't want to
                 * allow this.
                 */
                if (custom1 && !custom2 && !ItemUtils.isEmpty(ingredients[1])) {
                    event.setCancelled(true);
                }
                if (!custom1 && custom2 && !ItemUtils.isEmpty(ingredients[0])) {
                    event.setCancelled(true);
                }
            }
            if (event.getInventory() instanceof MerchantInventory) {
                MerchantInventory inv = (MerchantInventory) event.getInventory();
                MerchantRecipe recipe = null;
                try {
                    recipe = inv.getSelectedRecipe();
                } catch (NullPointerException npe) {
                    // When the player hasn't inserted enough items, above method will
                    // throw a NullPointerException. If that happens, recipe will stay
                    // null and thus the next if block won't be executed.
                }
                if (recipe != null) {
                    if (event.getAction() != InventoryAction.NOTHING) {
                        ItemStack[] contents = inv.getContents();
                        List<ItemStack> ingredients = recipe.getIngredients();
                        int recipeAmount0 = ingredients.get(0).getAmount();
                        boolean hasSecondIngredient = ingredients.size() > 1 && ingredients.get(1) != null;
                        int recipeAmount1 = hasSecondIngredient ? ingredients.get(1).getAmount() : 0;
                        boolean overrule0 = ItemUtils.isCustom(contents[0]) && contents[0].getAmount() > recipeAmount0;
                        boolean overrule1 = ItemUtils.isCustom(contents[1]) && contents[1].getAmount() > recipeAmount1;
                        if (overrule0 || overrule1) {

                            event.setCancelled(true);
                            if (event.isLeftClick()) {
                                // The default way of trading
                                if (event.getAction() == InventoryAction.PICKUP_ALL) {

                                    // We will have to do this manually...
                                    if (event.getCursor() == null || KciNms.instance.items.getMaterialName(event.getCursor()).equals(CIMaterial.AIR.name())) {
                                        event.setCursor(recipe.getResult());
                                    } else {
                                        event.getCursor().setAmount(
                                                event.getCursor().getAmount() + recipe.getResult().getAmount());
                                    }
                                    if (contents[0] != null && !KciNms.instance.items.getMaterialName(contents[0]).equals(CIMaterial.AIR.name())) {
                                        int newAmount = contents[0].getAmount() - recipeAmount0;
                                        if (newAmount > 0) {
                                            contents[0].setAmount(newAmount);
                                        } else {
                                            contents[0] = null;
                                        }
                                    }
                                    if (contents[1] != null && !KciNms.instance.items.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())
                                            && ingredients.size() > 1 && ingredients.get(1) != null) {
                                        int newAmount = contents[1].getAmount() - recipeAmount1;
                                        if (newAmount > 0) {
                                            contents[1].setAmount(newAmount);
                                        } else {
                                            contents[1] = null;
                                        }
                                    }
                                    inv.setContents(contents);
                                }

                                // Using shift-click for trading
                                else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

                                    int trades = contents[0].getAmount() / recipeAmount0;
                                    if (hasSecondIngredient) {
                                        int trades2 = contents[1].getAmount() / recipeAmount1;
                                        if (trades2 < trades) {
                                            trades = trades2;
                                        }
                                    }

                                    {
                                        int newAmount = contents[0].getAmount() - trades * recipeAmount0;
                                        if (newAmount > 0) {
                                            contents[0].setAmount(newAmount);
                                        } else {
                                            contents[0] = null;
                                        }
                                    }
                                    if (hasSecondIngredient) {
                                        int newAmount = contents[1].getAmount() - trades * recipeAmount1;
                                        if (newAmount > 0) {
                                            contents[1].setAmount(newAmount);
                                        } else {
                                            contents[1] = null;
                                        }
                                    }

                                    ItemStack result = recipe.getResult();
                                    Collection<ItemStack> itemsThatDidntFit = new ArrayList<>(0);
                                    for (int counter = 0; counter < trades; counter++) {
                                        itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(result).values());
                                    }
                                    for (ItemStack didntFit : itemsThatDidntFit) {
                                        event.getWhoClicked().getWorld().dropItem(event.getInventory().getLocation(), didntFit);
                                    }

                                    inv.setContents(contents);
                                }

                                // If I forgot a case, it will go in here. Cancel it to prevent dangerous
                                // glitches
                                else {
                                    event.setCancelled(true);
                                }
                            } else {

                                // I will only allow left click trading
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        } else if (action == InventoryAction.NOTHING || action == InventoryAction.PICKUP_ONE
                || action == InventoryAction.PICKUP_SOME || action == InventoryAction.SWAP_WITH_CURSOR
                || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME
                || action == InventoryAction.PLACE_ALL
        ) {
            ItemStack cursor = event.getCursor();
            ItemStack current = event.getCurrentItem();

            CustomItemValues customCursor = itemSet.getItem(cursor);
            CustomItemValues customCurrent = itemSet.getItem(current);

            // This block makes custom items stackable
            if (customCursor != null && customCursor == customCurrent && wrap(customCursor).needsStackingHelp() &&
                    !GeyserSupport.isBedrock(event.getWhoClicked())
            ) {
                event.setResult(Event.Result.DENY);
                if (event.isLeftClick()) {
                    int amount = current.getAmount() + cursor.getAmount();
                    if (amount <= customCursor.getMaxStacksize()) {
                        current.setAmount(amount);
                        cursor.setAmount(0);
                    } else {
                        current.setAmount(customCursor.getMaxStacksize());
                        cursor.setAmount(amount - customCursor.getMaxStacksize());
                    }
                } else {
                    int newAmount = current.getAmount() + 1;
                    if (newAmount <= customCurrent.getMaxStacksize()) {
                        cursor.setAmount(cursor.getAmount() - 1);
                        current.setAmount(newAmount);
                    }
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                        event.getView().getPlayer().setItemOnCursor(cursor)
                );
            }

            // For some reason, I need to manually enforce this as well...
            if (action == InventoryAction.PLACE_ALL && ItemUtils.isEmpty(current) && customCursor != null && wrap(customCursor).needsStackingHelp()) {
                // event.getView().getInventory() isn't available in MC 1.12
                if (KciNms.mcVersion >= VERSION1_13) {
                    Inventory clickedInventory = event.getView().getInventory(event.getRawSlot());
                    if (clickedInventory != null) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                            ItemStack newCursor = event.getWhoClicked().getItemOnCursor();
                            ItemStack newItem = clickedInventory.getItem(event.getSlot());
                            if (itemSet.getItem(newCursor) == customCursor && itemSet.getItem(newItem) == customCursor) {
                                event.getWhoClicked().setItemOnCursor(null);
                                newItem.setAmount(newItem.getAmount() + newCursor.getAmount());
                                clickedInventory.setItem(event.getSlot(), newItem);
                            }
                        });
                    }
                }
            }
        } else if (action == InventoryAction.COLLECT_TO_CURSOR) {
            CustomItemValues customItem = itemSet.getItem(event.getCursor());
            if (customItem != null && wrap(customItem).needsStackingHelp()) {
                event.setCancelled(true);
                int currentStacksize = event.getCursor().getAmount();
                InventoryView view = event.getView();
                /*
                 * I would rather use Inventory#getSize, but that can include slots like equipment slots that
                 * are hidden in some views. This has lead to stupid exceptions in the past...
                 * (For the same reason, I can't just use view.countSlots()...)
                 */
                int numTopSlots = view.getTopInventory().getStorageContents().length;
                int numBottomSlots = view.getBottomInventory().getStorageContents().length;

                int rawNumBottomSlots = view.getBottomInventory().getSize();
                boolean isInvCrafting = view.getTopInventory() instanceof CraftingInventory && view.getTopInventory().getSize() == 5;

                int numSlots = numTopSlots + (isInvCrafting ? rawNumBottomSlots : numBottomSlots);

                for (int slotIndex = 0; slotIndex < numSlots; slotIndex++) {
                    if (slotIndex != event.getRawSlot()) {
                        ItemStack otherSlot = view.getItem(slotIndex);
                        CustomItemValues otherCustom = itemSet.getItem(otherSlot);
                        if (customItem == otherCustom) {
                            int newStacksize = Math.min(
                                    currentStacksize + otherSlot.getAmount(),
                                    customItem.getMaxStacksize()
                            );
                            if (newStacksize > currentStacksize) {
                                int remainingStacksize = otherSlot.getAmount() - (newStacksize - currentStacksize);
                                if (remainingStacksize == 0) {
                                    view.setItem(slotIndex, null);
                                } else {
                                    otherSlot.setAmount(remainingStacksize);
                                    view.setItem(slotIndex, otherSlot);
                                }

                                currentStacksize = newStacksize;
                                if (newStacksize == customItem.getMaxStacksize()) {
                                    break;
                                }
                            }
                        }
                    }
                }
                if (currentStacksize != event.getCursor().getAmount()) {
                    ItemStack newCursor = event.getCursor().clone();
                    newCursor.setAmount(currentStacksize);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                            event.getWhoClicked().setItemOnCursor(newCursor)
                    );
                }
            }
        } else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            // This block ensures that shift-clicking custom items can stack them
            ItemStack clickedItem = event.getCurrentItem();
            CustomItemValues customClicked = itemSet.getItem(clickedItem);

            if (customClicked != null && wrap(customClicked).needsStackingHelp()) {
                event.setCancelled(true);
                boolean clickedTopInv = event.getRawSlot() == event.getSlot();

                int minDestIndex;
                int boundDestIndex;
                Inventory destInv;

                Inventory topInv = event.getView().getTopInventory();
                if (topInv instanceof CraftingInventory) {
                    if (topInv.getSize() == 5) {
                        // This is for crafting in survival inventory

                        // Top (raw) slots are 9 to 35
                        // The lower top slots are for equipment and crafting
                        // There is also a high top slot for the shield
                        // Hotbar slots are 0 to 8
                        // Hotbar raw slots are 36 to 44

                        if (clickedTopInv) {
                            minDestIndex = 0;
                            if (event.getRawSlot() < 9) {
                                boundDestIndex = 36;
                            } else {
                                boundDestIndex = 9;
                            }
                        } else {
                            minDestIndex = 9;
                            boundDestIndex = 36;
                        }

                        destInv = event.getView().getBottomInventory();
                    } else if (topInv.getSize() == 10) {
                        // This is for crafting table crafting
                        destInv = clickedTopInv ? event.getView().getBottomInventory() : topInv;
                        minDestIndex = clickedTopInv ? 0 : 1;
                        boundDestIndex = destInv.getStorageContents().length;
                    } else {
                        // I don't know what kind of crafting inventory this is, so I don't know how to handle it
                        // Doing nothing is better than doing something wrong
                        return;
                    }
                } else {
                    // This is for other non-customer containers
                    destInv = clickedTopInv ? event.getView().getBottomInventory() : topInv;
                    minDestIndex = 0;
                    boundDestIndex = destInv.getStorageContents().length;
                }

                int originalAmount = clickedItem.getAmount();
                int remainingAmount = originalAmount;
                ItemStack[] destItems = destInv.getContents();

                // Try to put the clicked item in a slot that contains the same custom item, but is not full
                for (int index = minDestIndex; index < boundDestIndex; index++) {
                    ItemStack destItem = destItems[index];
                    CustomItemValues destCandidate = itemSet.getItem(destItem);
                    if (destCandidate == customClicked) {

                        int remainingSpace = destCandidate.getMaxStacksize() - destItem.getAmount();
                        if (remainingSpace >= remainingAmount) {
                            destItem.setAmount(destItem.getAmount() + remainingAmount);
                            remainingAmount = 0;
                            break;
                        } else {
                            remainingAmount -= remainingSpace;
                            destItem.setAmount(destCandidate.getMaxStacksize());
                        }
                    }
                }

                // If the item is not yet 'consumed' entirely, use the remaining part to fill empty slots
                if (remainingAmount > 0) {
                    for (int index = minDestIndex; index < boundDestIndex; index++) {
                        if (ItemUtils.isEmpty(destItems[index])) {
                            destItems[index] = clickedItem.clone();
                            destItems[index].setAmount(remainingAmount);
                            remainingAmount = 0;
                            break;
                        }
                    }
                }

                // Complete the shift-click actions
                if (originalAmount != remainingAmount) {
                    destInv.setContents(destItems);
                    if (remainingAmount > 0) {
                        clickedItem.setAmount(remainingAmount);
                    } else {
                        clickedItem = null;
                    }
                    event.setCurrentItem(clickedItem);
                }
            }
        }
    }

    private final HashMap<UUID, Long> lastInventoryEvents = new HashMap<>();

    /**
     * If this method is called for each inventory event, it will prevent players from triggering more than
     * 1 inventory event per tick. This is necessary to prevent a duplicate/vanish glitch that can occur
     * when this plug-in processes more than 1 inventory event for the same item stack during the same
     * tick.
     */
    private void guardInventoryEvents(Cancellable event, UUID playerId) {
        Long previousInvEvent = lastInventoryEvents.get(playerId);

        long currentTime = CustomItemsPlugin.getInstance().getData().getCurrentTick();

        if (previousInvEvent != null && previousInvEvent == currentTime) {
            event.setCancelled(true);
        } else {
            lastInventoryEvents.put(playerId, currentTime);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void guardInventoryEvents(InventoryClickEvent event) {
        // Don't mess with creative clicks
        if (event.getClick() != ClickType.CREATIVE) {

            CustomItemValues customCurrent = itemSet.getItem(event.getCurrentItem());
            CustomItemValues customCursor = itemSet.getItem(event.getCursor());

            if ((customCurrent != null && wrap(customCurrent).needsStackingHelp()) || (customCursor != null && wrap(customCursor).needsStackingHelp())) {
                guardInventoryEvents(event, event.getWhoClicked().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void guardInventoryEvents(InventoryDragEvent event) {
        guardInventoryEvents(event, event.getWhoClicked().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleCustomItemDragging(InventoryDragEvent event) {
        CustomItemValues customItem = itemSet.getItem(event.getOldCursor());
        if (customItem != null && wrap(customItem).needsStackingHelp()) {
            int numSlots = event.getNewItems().size();

            ItemStack remainingCursor = event.getCursor();
            int remainingSize = event.getOldCursor().getAmount();
            int desiredAmountPerSlot = event.getType() == DragType.EVEN ? remainingSize / numSlots : 1;

            for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
                ItemStack toIncrease = entry.getValue();
                ItemStack oldItem = event.getView().getItem(entry.getKey());
                int oldSize = oldItem != null ? oldItem.getAmount() : 0;
                int newSize = Math.min(oldSize + desiredAmountPerSlot, customItem.getMaxStacksize());
                int amountToAdd = newSize - oldSize;
                if (amountToAdd > 0 && amountToAdd <= remainingSize) {
                    remainingSize -= amountToAdd;
                    ItemStack replacement = toIncrease.clone();
                    replacement.setAmount(newSize);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                            event.getView().setItem(entry.getKey(), replacement)
                    );
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                            event.getView().setItem(entry.getKey(), oldItem)
                    );
                }
            }

            if (remainingCursor != null) {
                if (remainingSize != remainingCursor.getAmount()) {
                    remainingCursor.setAmount(remainingSize);
                    event.setCursor(remainingCursor);
                }
            } else {
                if (remainingSize != 0) {
                    ItemStack newCursor = event.getOldCursor().clone();
                    newCursor.setAmount(remainingSize);
                    event.setCursor(newCursor);
                }
            }
        }
    }

    @EventHandler
    public void upgradeItemsInOtherInventories(InventoryOpenEvent event) {
        CustomItemsPlugin plugin = CustomItemsPlugin.getInstance();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        plugin.getItemUpdater().updateInventory(event.getInventory(), false)
                , 5); // Use some delay to reduce the risk of interference with other plug-ins
    }

    @EventHandler
    public void fixCraftingCloseStacking(InventoryCloseEvent event) {
        if (event.getInventory() instanceof CraftingInventory) {

            ItemStack result = ((CraftingInventory) event.getInventory()).getResult();

            ItemStack[] craftingContents = event.getInventory().getStorageContents();
            ItemStack[] inventoryContents = event.getPlayer().getInventory().getStorageContents();

            for (int craftingIndex = 0; craftingIndex < craftingContents.length; craftingIndex++) {
                CustomItemValues customItem = itemSet.getItem(craftingContents[craftingIndex]);
                if (customItem != null && !craftingContents[craftingIndex].equals(result)) {

                    for (ItemStack currentStack : inventoryContents) {
                        if (itemSet.getItem(currentStack) == customItem) {
                            if (customItem.getMaxStacksize() - currentStack.getAmount() >= craftingContents[craftingIndex].getAmount()) {
                                currentStack.setAmount(currentStack.getAmount() + craftingContents[craftingIndex].getAmount());
                                craftingContents[craftingIndex] = null;
                                break;
                            }
                        }
                    }

                    if (craftingContents[craftingIndex] != null) {
                        for (int invIndex = 0; invIndex < inventoryContents.length; invIndex++) {
                            if (ItemUtils.isEmpty(inventoryContents[invIndex])) {
                                inventoryContents[invIndex] = craftingContents[craftingIndex];
                                craftingContents[craftingIndex] = null;
                            }
                        }
                    }
                }
            }

            event.getInventory().setStorageContents(craftingContents);
            event.getPlayer().getInventory().setStorageContents(inventoryContents);
        }
    }
}
