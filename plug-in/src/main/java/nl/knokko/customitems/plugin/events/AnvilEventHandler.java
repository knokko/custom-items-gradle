package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciTool;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.result.KciResult;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.*;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptAmountless;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
import static org.bukkit.enchantments.Enchantment.*;
import static org.bukkit.enchantments.Enchantment.VANISHING_CURSE;

public class AnvilEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public AnvilEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void prepareAnvil(PrepareAnvilEvent event) {

        ItemStack[] contents = event.getInventory().getStorageContents();
        KciItem custom1 = itemSet.getItem(contents[0]);
        KciItem custom2 = itemSet.getItem(contents[1]);

        if (custom1 != null) {
            if (custom1.allowAnvilActions()) {
                if (custom1 instanceof KciTool) {
                    KciTool tool = (KciTool) custom1;
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
                    } else if (contents[1] != null && !KciNms.instance.items.getMaterialName(contents[1]).equals(VMaterial.AIR.name())) {
                        if (KciNms.instance.items.getMaterialName(contents[1]).equals(VMaterial.ENCHANTED_BOOK.name())) {
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
                                KciIngredient repairItem = tool.getRepairItem();
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
    public void handleAnvilResult(InventoryClickEvent event) {
        InventoryType.SlotType type = event.getSlotType();

        // The CREATIVE ClickType can't be handled properly because it is unknown whether the player pressed
        // shift, which button was used, and a lot of other stuff. Return early to prevent weird reactions.
        if (event.getClick() == ClickType.CREATIVE) {
            return;
        }

        if (type == InventoryType.SlotType.RESULT) {
            if (event.getInventory() instanceof AnvilInventory) {
                // By default, Minecraft does not allow players to pick illegal items from
                // anvil, so...
                ItemStack cursor = event.getCursor();
                ItemStack current = event.getCurrentItem();

                KciItem customCurrent = itemSet.getItem(current);
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
                    KciItem custom = customCurrent;
                    ItemStack first = event.getInventory().getItem(0);
                    KciItem customFirst = itemSet.getItem(first);
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
                            if (custom instanceof KciTool && contents[1] != null
                                    && !KciNms.instance.items.getMaterialName(contents[1]).equals(VMaterial.AIR.name())) {
                                KciTool tool = (KciTool) custom;

                                // Use AcceptAmountless because we need to handle remaining item differently
                                if (shouldIngredientAcceptAmountless(tool.getRepairItem(), contents[1]) && tool.getMaxDurabilityNew() != null) {
                                    long durability = wrap(tool).getDurability(contents[0]);
                                    long maxDurability = tool.getMaxDurabilityNew();
                                    long neededDurability = maxDurability - durability;
                                    int neededAmount = (int) Math.ceil(neededDurability * 4.0 / maxDurability) * tool.getRepairItem().getAmount();

                                    int repairValue = Math.min(neededAmount, contents[1].getAmount()) / tool.getRepairItem().getAmount();
                                    int usedAmount = repairValue * tool.getRepairItem().getAmount();

                                    // If there is a remaining item, we can only proceed if the entire repair item stack is consumed
                                    KciIngredient repairItem = tool.getRepairItem();
                                    if (repairValue > 0 && (repairItem.getRemainingItem() == null || repairValue * repairItem.getAmount() == contents[1].getAmount())) {
                                        if (usedAmount < contents[1].getAmount()) {
                                            contents[1].setAmount(contents[1].getAmount() - usedAmount);
                                        } else {
                                            KciResult remainingResult = tool.getRepairItem().getRemainingItem();
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
        }

        // Force a PrepareAnvilEvent
        if (event.getInventory() instanceof AnvilInventory) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () ->
                    event.getInventory().setItem(0, event.getInventory().getItem(0))
            );
        }
    }
}
