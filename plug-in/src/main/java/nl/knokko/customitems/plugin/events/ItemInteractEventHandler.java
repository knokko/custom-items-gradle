package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

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
            KciItem custom = itemSet.getItem(item);

            if (custom != null) {

                VMaterial type = VMaterial.getOrNull(KciNms.instance.items.getMaterialName(event.getClickedBlock()));

                // Don't let custom items be used as their internal item
                boolean canBeTilled = type == VMaterial.DIRT || type == VMaterial.GRASS
                        || type == VMaterial.GRASS_BLOCK || type == VMaterial.GRASS_PATH
                        || type == VMaterial.COARSE_DIRT || type == VMaterial.DIRT_PATH
                        || type == VMaterial.ROOTED_DIRT;
                boolean canBeSheared = type == VMaterial.PUMPKIN || type == VMaterial.BEE_NEST
                        || type == VMaterial.BEEHIVE;

                if (wrap(custom).forbidDefaultUse(item)) {

                    // But don't cancel unnecessary events (so don't prevent opening containers)
                    if (custom.getItemType().canServe(KciItemType.Category.HOE)) {
                        if (canBeTilled) {
                            event.setCancelled(true);
                        }
                    } else if (custom.getItemType().canServe(KciItemType.Category.SHEAR)) {
                        if (canBeSheared) {
                            event.setCancelled(true);
                        }
                    } else {
                        // Shouldn't happen, but better safe than sorry
                        event.setCancelled(true);
                    }
                } else if (custom instanceof KciTool) {
                    KciTool tool = (KciTool) custom;
                    boolean broke = false;

                    if (tool instanceof KciHoe) {
                        KciHoe customHoe = (KciHoe) tool;
                        if (canBeTilled) {
                            broke = CustomToolWrapper.wrap(tool).decreaseDurability(item, customHoe.getTillDurabilityLoss());
                        }
                    }

                    if (tool instanceof KciShears) {
                        KciShears customShears = (KciShears) tool;
                        if (canBeSheared) {
                            broke = CustomToolWrapper.wrap(tool).decreaseDurability(item, customShears.getShearDurabilityLoss());
                        }
                    }

                    if (broke) {
                        String newItemName = checkBrokenCondition(tool.getReplacementConditions());
                        if (newItemName != null) {
                            item = wrap(itemSet.getItem(newItemName)).create(1);
                        } else item = null;
                        playBreakSound(event.getPlayer());
                    }

                    if (event.getHand() == EquipmentSlot.HAND)
                        event.getPlayer().getInventory().setItemInMainHand(item);
                    else
                        event.getPlayer().getInventory().setItemInOffHand(item);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {

        ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack off = event.getPlayer().getInventory().getItemInOffHand();

        KciItem customMain = VMaterial.getOrNull(KciNms.instance.items.getMaterialName(main)) == VMaterial.SHEARS
                ? itemSet.getItem(main) : null;
        KciItem customOff = VMaterial.getOrNull(KciNms.instance.items.getMaterialName(off)) == VMaterial.SHEARS
                ? itemSet.getItem(off) : null;

        if (customMain != null) {
            if (wrap(customMain).forbidDefaultUse(main)) event.setCancelled(true);
            else if (customMain instanceof KciShears) {
                KciShears tool = (KciShears) customMain;
                boolean broke = CustomToolWrapper.wrap(tool).decreaseDurability(main, tool.getShearDurabilityLoss());
                if (broke) {
                    String newItemName = checkBrokenCondition(tool.getReplacementConditions());
                    if (newItemName != null) {
                        main = wrap(itemSet.getItem(newItemName)).create(1);
                    } else main = null;
                    playBreakSound(event.getPlayer());
                }
                event.getPlayer().getInventory().setItemInMainHand(main);
            }
        } else if (customOff != null) {
            if (wrap(customOff).forbidDefaultUse(off)) event.setCancelled(true);
            else if (customOff instanceof KciShears) {
                KciShears tool = (KciShears) customOff;
                boolean broke = CustomToolWrapper.wrap(tool).decreaseDurability(off, tool.getShearDurabilityLoss());
                if (broke) {
                    String newItemName = checkBrokenCondition(tool.getReplacementConditions());
                    if (newItemName != null) {
                        off = wrap(itemSet.getItem(newItemName)).create(1);
                    } else off = null;
                    playBreakSound(event.getPlayer());
                }
                event.getPlayer().getInventory().setItemInOffHand(off);
            }
        }
    }

    @EventHandler
    public void updateGunsAndWands(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            KciItem usedItem = itemSet.getItem(event.getItem());
            PluginData data = CustomItemsPlugin.getInstance().getData();

            if ((usedItem instanceof KciWand || usedItem instanceof KciGun ||
                    usedItem instanceof KciThrowable)
            ) {
                if (data.hasPermissionToShoot(event.getPlayer(), usedItem)) {
                    data.setShooting(event.getPlayer());
                } else {
                    event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not allowed to shoot with this item");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void finishEating(PlayerItemConsumeEvent event) {
        ItemStack eatenStack = event.getItem();
        KciItem eatenItem = itemSet.getItem(eatenStack);
        if (eatenItem instanceof KciFood) {
            event.setCancelled(true);

            PlayerInventory inv = event.getPlayer().getInventory();
            boolean isMainHand = eatenStack.equals(inv.getItemInMainHand());
            if (isMainHand || eatenStack.equals(inv.getItemInOffHand())) {
                PluginData.consumeCustomFood(
                        event.getPlayer(), eatenStack, (KciFood) eatenItem,
                        isMainHand ? inv::setItemInMainHand : inv::setItemInOffHand
                );
            }
        }
    }

    @EventHandler
    public void startEating(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack usedStack = event.getItem();
            KciItem usedItem = itemSet.getItem(usedStack);
            if (usedItem instanceof KciFood && !Objects.requireNonNull(usedStack).getType().isEdible()) {
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
        KciItem custom = itemSet.getItem(item);
        if (custom != null && wrap(custom).forbidDefaultUse(item)) {
            // Don't let custom items be used as their internal item
            event.setCancelled(true);
        }
    }
}
