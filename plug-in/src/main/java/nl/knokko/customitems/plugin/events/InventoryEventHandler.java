package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.itemset.CustomRecipesView;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.multisupport.geyser.GeyserSupport;
import nl.knokko.customitems.plugin.recipe.IngredientEntry;
import nl.knokko.customitems.plugin.recipe.RecipeHelper;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

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
    public void processAnvil(PrepareAnvilEvent event) {

        ItemStack[] contents = event.getInventory().getStorageContents();
        CustomItemValues custom1 = itemSet.getItem(contents[0]);
        CustomItemValues custom2 = itemSet.getItem(contents[1]);

        if (custom1 != null) {
            if (custom1.allowAnvilActions()) {
                if (custom1 instanceof CustomToolValues) {
                    CustomToolValues tool = (CustomToolValues) custom1;
                    String renameText = event.getInventory().getRenameText();
                    String oldName = KciNms.instance.items.getStackName(contents[0]);
                    boolean isRenaming = !renameText.isEmpty() && !renameText.equals(oldName);
                    if (custom1 == custom2) {
                        long durability1 = wrap(tool).getDurability(contents[0]);
                        long durability2 = wrap(tool).getDurability(contents[1]);
                        long resultDurability = -1;
                        if (tool.getMaxDurabilityNew() != null) {
                            resultDurability = Math.min(durability1 + durability2, tool.getMaxDurabilityNew());
                        }
                        Map<Enchantment, Integer> enchantments1 = contents[0].getEnchantments();
                        Map<Enchantment, Integer> enchantments2 = contents[1].getEnchantments();
                        ItemStack result = wrap(tool).create(1, resultDurability);
                        int levelCost = 0;
                        boolean hasChange = false;
                        if (isRenaming) {
                            ItemMeta meta = result.getItemMeta();
                            meta.setDisplayName(event.getInventory().getRenameText());
                            result.setItemMeta(meta);
                            levelCost++;
                            hasChange = true;
                        } else {
                            ItemMeta meta = result.getItemMeta();
                            meta.setDisplayName(oldName);
                            result.setItemMeta(meta);
                        }
                        result.addUnsafeEnchantments(enchantments1);
                        Set<Map.Entry<Enchantment, Integer>> entrySet = enchantments2.entrySet();
                        for (Map.Entry<Enchantment, Integer> entry : entrySet) {
                            if (entry.getKey().canEnchantItem(result)) {
                                try {
                                    result.addEnchantment(entry.getKey(), entry.getValue());
                                    levelCost += entry.getValue() * getItemEnchantFactor(entry.getKey());
                                    hasChange = true;
                                } catch (IllegalArgumentException illegal) {
                                    // The rules from the wiki
                                    levelCost++;
                                } // Only add enchantments that can be added
                            }
                        }
                        int repairCost1 = 0;
                        int repairCost2 = 0;
                        ItemMeta meta1 = contents[0].getItemMeta();
                        if (meta1 instanceof Repairable) {
                            Repairable repairable = (Repairable) meta1;
                            repairCost1 = repairable.getRepairCost();
                            levelCost += repairCost1;
                        }
                        ItemMeta meta2 = contents[1].getItemMeta();
                        if (meta2 instanceof Repairable) {
                            Repairable repairable = (Repairable) meta2;
                            repairCost2 = repairable.getRepairCost();
                            levelCost += repairCost2;
                        }
                        ItemMeta resultMeta = result.getItemMeta();
                        int maxRepairCost = Math.max(repairCost1, repairCost2);
                        int maxRepairCount = (int) Math.round(Math.log(maxRepairCost + 1) / Math.log(2));
                        ((Repairable) resultMeta).setRepairCost((int) Math.round(Math.pow(2, maxRepairCount + 1) - 1));
                        result.setItemMeta(resultMeta);
                        if (tool.getMaxDurabilityNew() != null && wrap(tool).getDurability(contents[0]) < tool.getMaxDurabilityNew()) {
                            levelCost += 2;
                            hasChange = true;
                        }
                        if (hasChange) {
                            event.setResult(result);
                            int finalLevelCost = levelCost;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                                // Apparently, settings the repair cost during the event has no effect
                                event.getInventory().setRepairCost(finalLevelCost);
                            });
                        } else {
                            event.setResult(null);
                        }
                    } else if (contents[1] != null && !KciNms.instance.items.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
                        if (KciNms.instance.items.getMaterialName(contents[1]).equals(CIMaterial.ENCHANTED_BOOK.name())) {
                            // This case is handled by minecraft automagically
                        } else if (shouldIngredientAcceptAmountless(tool.getRepairItem(), contents[1])) {
                            // We use AcceptAmountless because we need to handle remaining items differently

                            long neededDurability = 0;
                            if (tool.getMaxDurabilityNew() != null) {
                                long durability = wrap(tool).getDurability(contents[0]);
                                long maxDurability = tool.getMaxDurabilityNew();
                                neededDurability = maxDurability - durability;
                            }

                            if (neededDurability > 0) {
                                IngredientValues repairItem = tool.getRepairItem();
                                long durability = wrap(tool).getDurability(contents[0]);
                                int neededAmount = (int) Math.ceil(neededDurability * 4.0 / tool.getMaxDurabilityNew()) * repairItem.getAmount();

                                int repairValue = Math.min(neededAmount, contents[1].getAmount()) / repairItem.getAmount();

                                // If there is a remaining item, we can only proceed if the entire repair item stack is consumed
                                if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
                                    long resultDurability = Math.min(durability + tool.getMaxDurabilityNew() * repairValue / 4,
                                            tool.getMaxDurabilityNew());
                                    ItemStack result = wrap(tool).create(1, resultDurability);
                                    result.addUnsafeEnchantments(contents[0].getEnchantments());
                                    int levelCost = repairValue;
                                    if (isRenaming) {
                                        levelCost++;
                                        ItemMeta meta = result.getItemMeta();
                                        meta.setDisplayName(event.getInventory().getRenameText());
                                        result.setItemMeta(meta);
                                    } else {
                                        ItemMeta meta = result.getItemMeta();
                                        meta.setDisplayName(oldName);
                                        result.setItemMeta(meta);
                                    }
                                    int repairCost = 0;
                                    ItemMeta meta1 = contents[0].getItemMeta();
                                    if (meta1 instanceof Repairable) {
                                        Repairable repairable = (Repairable) meta1;
                                        repairCost = repairable.getRepairCost();
                                        levelCost += repairCost;
                                    }
                                    ItemMeta resultMeta = result.getItemMeta();
                                    int repairCount = (int) Math.round(Math.log(repairCost + 1) / Math.log(2));
                                    // We have a minor visual anvil bug here that presumably can't be fixed
                                    ((Repairable) resultMeta)
                                            .setRepairCost((int) Math.round(Math.pow(2, repairCount + 1) - 1));
                                    result.setItemMeta(resultMeta);
                                    int finalLevelCost = levelCost;
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                                        // Update repair cost and result after event to avoid some glitches
                                        event.getInventory().setItem(2, result);
                                        event.getInventory().setRepairCost(finalLevelCost);
                                    });
                                } else {
                                    event.setResult(null);
                                }
                            } else {
                                event.setResult(null);
                            }
                        } else {
                            event.setResult(null);
                        }
                    } else {
                        // This else block is for the case where the first slot is a custom item and the
                        // second slot is empty, so eventually for renaming.
                        // This else block is empty because minecraft itself takes care of it.
                    }
                } else {
                    event.setResult(null);
                }
            } else {
                event.setResult(null);
            }
        } else if (custom2 != null) {
            event.setResult(null);
        }
    }

    private static int getItemEnchantFactor(Enchantment e) {
        if (e.equals(PROTECTION_FIRE) || e.equals(PROTECTION_FALL) || e.equals(PROTECTION_PROJECTILE)
                || e.equals(DAMAGE_UNDEAD) || e.equals(DAMAGE_ARTHROPODS) || e.equals(KNOCKBACK)
                || e.equals(DURABILITY)) {
            return 2;
        }
        if (e.equals(PROTECTION_EXPLOSIONS) || e.equals(OXYGEN) || e.equals(WATER_WORKER) || e.equals(DEPTH_STRIDER)
                || e.equals(FROST_WALKER) || e.equals(FIRE_ASPECT) || e.equals(LOOT_BONUS_MOBS)
                || e.equals(SWEEPING_EDGE) || e.equals(LOOT_BONUS_BLOCKS) || e.equals(ARROW_KNOCKBACK)
                || e.equals(ARROW_FIRE) || e.equals(LUCK) || e.equals(LURE) || e.equals(MENDING)) {
            return 4;
        }
        if (e.equals(THORNS) || e.equals(BINDING_CURSE) || e.equals(SILK_TOUCH) || e.equals(ARROW_INFINITE)
                || e.equals(VANISHING_CURSE)) {
            return 8;
        }
        return 1;
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
            if (event.getInventory() instanceof CraftingInventory) {
                List<IngredientEntry> customCrafting = shouldInterfere.get(event.getWhoClicked().getUniqueId());
                if (customCrafting != null) {
                    if (
                            action == InventoryAction.PICKUP_ALL || action == InventoryAction.DROP_ONE_SLOT
                                    || action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.NOTHING
                    ) {

                        ItemStack[] oldContents = event.getInventory().getContents();
                        ItemStack[] contents = new ItemStack[oldContents.length];
                        for (int index = 0; index < contents.length; index++) {
                            contents[index] = oldContents[index].clone();
                        }

                        int computeAmountsToRemove = 1;

                        // In case of shift-click, we need to count how many transfers we can do
                        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {

                            event.setResult(Event.Result.DENY);

                            amountTestLoop:
                            while (computeAmountsToRemove < 64) {
                                for (IngredientEntry entry : customCrafting) {
                                    if (contents[entry.itemIndex + 1].getAmount() >= entry.ingredient.getAmount() * (computeAmountsToRemove + 1)) {
                                        continue;
                                    }
                                    break amountTestLoop;
                                }
                                computeAmountsToRemove++;
                            }
                        }

                        // In case of 'nothing', we need to check if we really can't do anything
                        // This is needed for handling stackable custom items
                        if (action == InventoryAction.NOTHING) {

                            computeAmountsToRemove = 0;

                            ItemStack cursor = event.getCursor();
                            ItemStack current = event.getCurrentItem();

                            CustomItemValues customCursor = itemSet.getItem(cursor);
                            CustomItemValues customCurrent = itemSet.getItem(current);

                            if (customCursor != null && customCursor == customCurrent) {
                                if (customCursor.canStack() && cursor.getAmount() + current.getAmount() <= customCursor.getMaxStacksize()) {
                                    computeAmountsToRemove = 1;
                                }
                            }
                        }

                        int baseAmountsToRemove = computeAmountsToRemove;
                        ItemStack cursor = event.getCursor();
                        ItemStack currentItem = event.getCurrentItem();

                        if (computeAmountsToRemove > 0) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {

                                // Decrease the stack sizes of all consumed ingredients
                                for (IngredientEntry entry : customCrafting) {
                                    if (entry.ingredient.getRemainingItem() == null) {
                                        ItemStack slotItem = contents[entry.itemIndex + 1];
                                        slotItem.setAmount(slotItem.getAmount() - entry.ingredient.getAmount() * baseAmountsToRemove);
                                    } else {
                                        contents[entry.itemIndex + 1] = convertResultToItemStack(entry.ingredient.getRemainingItem());
                                    }
                                }

                                if (action == InventoryAction.NOTHING) {
                                    cursor.setAmount(cursor.getAmount() + currentItem.getAmount());
                                    event.getView().getPlayer().setItemOnCursor(cursor);
                                }

                                if (action == InventoryAction.DROP_ONE_SLOT) {
                                    event.getWhoClicked().getWorld().dropItem(
                                            event.getWhoClicked().getLocation(),
                                            currentItem
                                    );
                                }

                                if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                                    ItemStack result = currentItem.clone();
                                    event.getInventory().setItem(0, KciNms.instance.items.createStack(CIMaterial.AIR.name(), 1));
                                    CustomItemValues customResult = itemSet.getItem(result);
                                    int amountToGive = baseAmountsToRemove * result.getAmount();

                                    Collection<ItemStack> itemsThatDidntFit = new ArrayList<>(0);
                                    if (customResult != null && !customResult.canStack()) {
                                        for (int counter = 0; counter < amountToGive; counter++) {
                                            itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(result.clone()).values());
                                        }
                                    } else {
                                        int maxStacksize = customResult == null ? 64 : customResult.getMaxStacksize();
                                        for (int counter = 0; counter < amountToGive; counter += maxStacksize) {
                                            int left = amountToGive - counter;
                                            ItemStack clonedResult = result.clone();
                                            if (left > maxStacksize) {
                                                clonedResult.setAmount(maxStacksize);
                                                itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(clonedResult).values());
                                            } else {
                                                clonedResult.setAmount(left);
                                                itemsThatDidntFit.addAll(event.getWhoClicked().getInventory().addItem(clonedResult).values());
                                                break;
                                            }
                                        }
                                    }

                                    for (ItemStack didntFit : itemsThatDidntFit) {
                                        event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), didntFit);
                                    }
                                }

                                event.getInventory().setContents(contents);

                                if (action == InventoryAction.NOTHING) {
                                    beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer());
                                }
                            });
                        }
                    } else {
                        // Maybe, there is some edge case I don't know about, so cancel it just to be
                        // sure
                        event.setResult(Event.Result.DENY);
                    }
                }
            } else if (event.getInventory() instanceof AnvilInventory) {
                // By default, Minecraft does not allow players to pick illegal items from
                // anvil, so...
                ItemStack cursor = event.getCursor();
                ItemStack current = event.getCurrentItem();

                CustomItemValues customCurrent = itemSet.getItem(current);
                if (ItemUtils.isEmpty(current)) {
                    event.setCancelled(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                        if (event.getWhoClicked() instanceof Player) {
                            Player player = (Player) event.getWhoClicked();
                            player.setExp(player.getExp());
                            player.closeInventory();
                        }
                    });
                } else if (ItemUtils.isEmpty(cursor) && customCurrent != null) {
                    AnvilInventory ai = (AnvilInventory) event.getInventory();
                    CustomItemValues custom = customCurrent;
                    ItemStack first = event.getInventory().getItem(0);
                    CustomItemValues customFirst = itemSet.getItem(first);
                    if (customFirst != null && !customFirst.allowAnvilActions()) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                            if (event.getWhoClicked() instanceof Player) {
                                Player player = (Player) event.getWhoClicked();
                                player.setExp(player.getExp());
                            }
                        });
                    } else if (event.getView().getPlayer() instanceof Player) {
                        Player player = (Player) event.getView().getPlayer();
                        int repairCost = ai.getRepairCost();
                        if (player.getLevel() >= repairCost) {
                            player.setItemOnCursor(current);
                            player.setLevel(player.getLevel() - repairCost);
                            ItemStack[] contents = ai.getContents();
                            if (custom instanceof CustomToolValues && contents[1] != null
                                    && !KciNms.instance.items.getMaterialName(contents[1]).equals(CIMaterial.AIR.name())) {
                                CustomToolValues tool = (CustomToolValues) custom;

                                // Use AcceptAmountless because we need to handle remaining item differently
                                if (shouldIngredientAcceptAmountless(tool.getRepairItem(), contents[1]) && tool.getMaxDurabilityNew() != null) {
                                    long durability = wrap(tool).getDurability(contents[0]);
                                    long maxDurability = tool.getMaxDurabilityNew();
                                    long neededDurability = maxDurability - durability;
                                    int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability) * tool.getRepairItem().getAmount();

                                    int repairValue = Math.min(neededAmount, contents[1].getAmount()) / tool.getRepairItem().getAmount();
                                    int usedAmount = repairValue * tool.getRepairItem().getAmount();

                                    // If there is a remaining item, we can only proceed if the entire repair item stack is consumed
                                    IngredientValues repairItem = tool.getRepairItem();
                                    if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
                                        if (usedAmount < contents[1].getAmount()) {
                                            contents[1].setAmount(contents[1].getAmount() - usedAmount);
                                        } else {
                                            ResultValues remainingResult = tool.getRepairItem().getRemainingItem();
                                            contents[1] = convertResultToItemStack(remainingResult);
                                            if (tool.getRepairItem().getRemainingItem() != null) {
                                                contents[1].setAmount(contents[1].getAmount() * repairValue);
                                            }
                                        }
                                    } else {
                                        contents[1] = null;
                                    }
                                } else {
                                    contents[1] = null;
                                }
                            } else {
                                contents[1] = null;
                            }
                            contents[0] = null;
                            // apparently, the length of contents is 2
                            ai.setContents(contents);
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
        // Force a PrepareAnvilEvent
        if (event.getInventory() instanceof AnvilInventory) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                    event.getInventory().setItem(0, event.getInventory().getItem(0))
            );
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
            int naturalStacksize = event.getOldCursor().getMaxStackSize();

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
    public void triggerCraftingHandler(InventoryClickEvent event) {
        if (event.getInventory() instanceof CraftingInventory) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                    beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer())
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void triggerCraftingHandler(InventoryDragEvent event) {
        if (event.getInventory() instanceof CraftingInventory) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                    beforeCraft((CraftingInventory) event.getInventory(), event.getView().getPlayer())
            );
        }
    }

    private void beforeCraft(CraftingInventory inventory, HumanEntity owner) {
        ItemStack result = inventory.getResult();

        // Block vanilla recipes that attempt to use custom items
        if (result != null && !KciNms.instance.items.getMaterialName(result).equals(CIMaterial.AIR.name())) {
            // When the result is a custom item, the recipe can't be an accident, so we can proceed safely
            // This improves cooperation with other crafting plug-ins
            if (!ItemUtils.isCustom(result)) {
                ItemStack[] ingredients = inventory.getStorageContents();
                for (ItemStack ingredient : ingredients) {
                    if (ItemUtils.isCustom(ingredient)) {
                        inventory.setResult(KciNms.instance.items.createStack(CIMaterial.AIR.name(), 1));
                        break;
                    }
                }
            }
        }

        // Check if there are any custom recipes matching the ingredients
        CustomRecipesView recipes = itemSet.get().getCraftingRecipes();
        if (recipes.size() > 0 && CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(owner.getLocation())) {
            // Determine ingredients
            ItemStack[] ingredients = inventory.getStorageContents();
            ingredients = Arrays.copyOfRange(ingredients, 1, ingredients.length);

            // Shaped recipes first because they have priority
            for (CraftingRecipeValues recipe : recipes) {

                String permission = recipe.getRequiredPermission();
                boolean hasPermission = permission == null || owner.hasPermission(permission) || owner.hasPermission("customitems.craftall");

                if (hasPermission && recipe instanceof ShapedRecipeValues) {
                    List<IngredientEntry> ingredientMapping = RecipeHelper.wrap(recipe).shouldAccept(ingredients);
                    if (ingredientMapping != null) {
                        inventory.setResult(RecipeHelper.wrap(recipe).getResult(ingredientMapping, ingredients));
                        inventory.getViewers().forEach(viewer -> {
                            if (viewer instanceof Player) {
                                ((Player) viewer).updateInventory();
                            }
                        });
                        shouldInterfere.put(owner.getUniqueId(), ingredientMapping);
                        return;
                    }
                }
            }

            // No shaped recipe fits, so try the shapeless recipes
            for (CraftingRecipeValues recipe : recipes) {

                String permission = recipe.getRequiredPermission();
                boolean hasPermission = permission == null || owner.hasPermission(permission) || owner.hasPermission("customitems.craftall");

                if (hasPermission && recipe instanceof ShapelessRecipeValues) {
                    List<IngredientEntry> ingredientMapping = RecipeHelper.wrap(recipe).shouldAccept(ingredients);
                    if (ingredientMapping != null) {
                        inventory.setResult(RecipeHelper.wrap(recipe).getResult(ingredientMapping, ingredients));
                        inventory.getViewers().forEach(viewer -> {
                            if (viewer instanceof Player) {
                                ((Player) viewer).updateInventory();
                            }
                        });
                        shouldInterfere.put(owner.getUniqueId(), ingredientMapping);
                        return;
                    }
                }
            }
        }
        if (shouldInterfere.remove(owner.getUniqueId()) != null) {
            inventory.setResult(null);
            inventory.getViewers().forEach(viewer -> {
                if (viewer instanceof Player) {
                    ((Player) viewer).updateInventory();
                }
            });
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
