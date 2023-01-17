package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static nl.knokko.customitems.plugin.events.ReplacementEventHandler.checkBrokenCondition;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;
import static nl.knokko.customitems.plugin.util.SoundPlayer.playBreakSound;

public class ItemInteractEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public ItemInteractEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            ItemStack item = event.getItem();
            CustomItemValues custom = itemSet.getItem(item);

            if (custom != null) {

                CIMaterial type = CIMaterial.getOrNull(KciNms.instance.items.getMaterialName(event.getClickedBlock()));

                // Don't let custom items be used as their internal item
                boolean canBeTilled = type == CIMaterial.DIRT || type == CIMaterial.GRASS
                        || type == CIMaterial.GRASS_BLOCK || type == CIMaterial.GRASS_PATH
                        || type == CIMaterial.COARSE_DIRT || type == CIMaterial.DIRT_PATH
                        || type == CIMaterial.ROOTED_DIRT;
                boolean canBeSheared = type == CIMaterial.PUMPKIN || type == CIMaterial.BEE_NEST
                        || type == CIMaterial.BEEHIVE;

                ItemStack newStack = item;

                if (wrap(custom).forbidDefaultUse(item)) {

                    // But don't cancel unnecessary events (so don't prevent opening containers)
                    if (custom.getItemType().canServe(CustomItemType.Category.HOE)) {
                        if (canBeTilled) {
                            event.setCancelled(true);
                        }
                    } else if (custom.getItemType().canServe(CustomItemType.Category.SHEAR)) {
                        if (canBeSheared) {
                            event.setCancelled(true);
                        }
                    } else {
                        // Shouldn't happen, but better safe than sorry
                        event.setCancelled(true);
                    }


                } else if (custom instanceof CustomToolValues) {
                    CustomToolValues tool = (CustomToolValues) custom;
                    if (tool instanceof CustomHoeValues) {
                        CustomHoeValues customHoe = (CustomHoeValues) tool;
                        if (canBeTilled) {
                            newStack = CustomToolWrapper.wrap(tool).decreaseDurability(item, customHoe.getTillDurabilityLoss());
                        }
                    }

                    if (tool instanceof CustomShearsValues) {
                        CustomShearsValues customShears = (CustomShearsValues) tool;
                        if (canBeSheared) {
                            newStack = CustomToolWrapper.wrap(tool).decreaseDurability(item, customShears.getShearDurabilityLoss());
                        }
                    }

                    if (newStack != item) {
                        if (newStack == null) {
                            String newItemName = checkBrokenCondition(tool.getReplacementConditions());
                            if (newItemName != null) {
                                newStack = wrap(itemSet.getItem(newItemName)).create(1);
                            }
                            playBreakSound(event.getPlayer());
                        }

                        if (newStack != null) {
                            if (event.getHand() == EquipmentSlot.HAND)
                                event.getPlayer().getInventory().setItemInMainHand(newStack);
                            else
                                event.getPlayer().getInventory().setItemInOffHand(newStack);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {

        ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack off = event.getPlayer().getInventory().getItemInOffHand();

        CustomItemValues customMain = CIMaterial.getOrNull(KciNms.instance.items.getMaterialName(main)) == CIMaterial.SHEARS
                ? itemSet.getItem(main) : null;
        CustomItemValues customOff = CIMaterial.getOrNull(KciNms.instance.items.getMaterialName(off)) == CIMaterial.SHEARS
                ? itemSet.getItem(off) : null;

        if (customMain != null) {
            if (wrap(customMain).forbidDefaultUse(main))
                event.setCancelled(true);
            else if (customMain instanceof CustomShearsValues) {
                CustomShearsValues tool = (CustomShearsValues) customMain;
                ItemStack newMain = CustomToolWrapper.wrap(tool).decreaseDurability(main, tool.getShearDurabilityLoss());
                if (newMain != main) {
                    if (newMain == null) {
                        String newItemName = checkBrokenCondition(tool.getReplacementConditions());
                        if (newItemName != null) {
                            newMain = wrap(itemSet.getItem(newItemName)).create(1);
                        }
                        playBreakSound(event.getPlayer());
                    }
                    event.getPlayer().getInventory().setItemInMainHand(newMain);
                }
            }
        } else if (customOff != null) {
            if (wrap(customOff).forbidDefaultUse(off))
                event.setCancelled(true);
            else if (customOff instanceof CustomShearsValues) {
                CustomShearsValues tool = (CustomShearsValues) customOff;
                ItemStack newOff = CustomToolWrapper.wrap(tool).decreaseDurability(off, tool.getShearDurabilityLoss());
                if (newOff != off) {
                    if (newOff == null) {
                        String newItemName = checkBrokenCondition(tool.getReplacementConditions());
                        if (newItemName != null) {
                            newOff = wrap(itemSet.getItem(newItemName)).create(1);
                        }
                        playBreakSound(event.getPlayer());
                    }
                    event.getPlayer().getInventory().setItemInOffHand(newOff);
                }
            }
        }
    }

    @EventHandler
    public void updateGunsAndWands(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            CustomItemValues usedItem = itemSet.getItem(event.getItem());
            PluginData data = CustomItemsPlugin.getInstance().getData();

            if ((usedItem instanceof CustomWandValues || usedItem instanceof CustomGunValues)) {
                if (data.hasPermissionToShoot(event.getPlayer(), usedItem)) {
                    data.setShooting(event.getPlayer());
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not allowed to shoot with this item");
                }
            }
        }
    }

    @EventHandler
    public void startEating(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            CustomItemValues usedItem = itemSet.getItem(event.getItem());
            if (usedItem instanceof CustomFoodValues) {
                CustomItemsPlugin.getInstance().getData().setEating(event.getPlayer());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemInteract(PlayerInteractAtEntityEvent event) {
        ItemStack item;
        if (event.getHand() == EquipmentSlot.HAND)
            item = event.getPlayer().getInventory().getItemInMainHand();
        else
            item = event.getPlayer().getInventory().getItemInOffHand();
        CustomItemValues custom = itemSet.getItem(item);
        if (custom != null && wrap(custom).forbidDefaultUse(item)) {
            // Don't let custom items be used as their internal item
            event.setCancelled(true);
        }
    }
}
