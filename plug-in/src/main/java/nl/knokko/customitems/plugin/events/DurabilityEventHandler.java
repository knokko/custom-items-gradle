package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static nl.knokko.customitems.plugin.events.ReplacementEventHandler.checkBrokenCondition;
import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
import static nl.knokko.customitems.plugin.util.SoundPlayer.playBreakSound;

public class DurabilityEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public DurabilityEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double original = event.getDamage();

            // Only act if armor reduced the damage
            if (isReducedByArmor(event.getCause()) && player.getEquipment() != null) {

                int armorDamage = Math.max(1, (int) (original / 4));
                EntityEquipment e = player.getEquipment();

                int helmetDamage = armorDamage;
                if (event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
                    // The regular helmet durability loss upon falling anvils seems somewhat randomized,
                    // but this amount should be reasonable
                    helmetDamage *= 25;
                }

                e.setHelmet(decreateCustomArmorDurability(e.getHelmet(), helmetDamage, player));
                e.setChestplate(decreateCustomArmorDurability(e.getChestplate(), armorDamage, player));
                e.setLeggings(decreateCustomArmorDurability(e.getLeggings(), armorDamage, player));
                e.setBoots(decreateCustomArmorDurability(e.getBoots(), armorDamage, player));
            }

            // There is no nice shield blocking event, so this dirty check will have to do
            if (player.isBlocking() && event.getFinalDamage() == 0.0) {

                UsedShield usedShield = determineUsedShield(itemSet, player);

                if (usedShield.customShield != null && event.getDamage() >= usedShield.customShield.getThresholdDamage()) {
                    int lostDurability = (int) (event.getDamage()) + 1;
                    if (usedShield.inOffhand) {
                        ItemStack offHand = player.getInventory().getItemInOffHand();
                        boolean broke = wrap(usedShield.customShield).decreaseDurability(offHand, lostDurability);
                        if (broke) {
                            String newItemName = checkBrokenCondition(usedShield.customShield.getReplacementConditions());
                            if (newItemName != null) {
                                player.getInventory().setItemInOffHand(CustomItemWrapper.wrap(itemSet.getItem(newItemName)).create(1));
                            } else player.getInventory().setItemInOffHand(null);
                            playBreakSound(player);
                        } else player.getInventory().setItemInOffHand(offHand);
                    } else {
                        ItemStack mainHand = player.getInventory().getItemInMainHand();
                        boolean broke = wrap(usedShield.customShield).decreaseDurability(mainHand, lostDurability);
                        if (broke) {
                            String newItemName = checkBrokenCondition(usedShield.customShield.getReplacementConditions());
                            if (newItemName != null) {
                                player.getInventory().setItemInMainHand(CustomItemWrapper.wrap(itemSet.getItem(newItemName)).create(1));
                            } else player.getInventory().setItemInMainHand(null);
                            playBreakSound(player);
                        } else player.getInventory().setItemInMainHand(mainHand);
                    }
                }
            }
        }
    }

    static UsedShield determineUsedShield(ItemSetWrapper itemSet, Player player) {
        KciShield shield = null;
        boolean offhand = true;

        ItemStack offStack = player.getInventory().getItemInOffHand();
        ItemStack mainStack = player.getInventory().getItemInMainHand();

        KciItem customOff = itemSet.getItem(offStack);
        if (customOff instanceof KciShield) {
            shield = (KciShield) customOff;
        }

        KciItem customMain = itemSet.getItem(mainStack);
        if (customMain instanceof KciShield) {
            shield = (KciShield) customMain;
            offhand = false;
        } else if (KciNms.instance.items.getMaterialName(mainStack).equals(VMaterial.SHIELD.name())) {
            shield = null;
            offhand = false;
        }

        return new UsedShield(offhand, offhand ? offStack : mainStack, shield);
    }

    static class UsedShield {

        final boolean inOffhand;
        final ItemStack itemStack;
        final KciShield customShield;

        UsedShield(boolean inOffHand, ItemStack itemStack, KciShield customShield) {
            this.inOffhand = inOffHand;
            this.itemStack = itemStack;
            this.customShield = customShield;
        }
    }

    private ItemStack decreateCustomArmorDurability(ItemStack piece, int damage, Player player) {
        boolean broke = decreaseCustomArmorDurability(piece, damage);
        if (broke) {
            String newItemName = checkBrokenCondition(itemSet.getItem(piece).getReplacementConditions());
            if (newItemName != null) {
                ItemUtils.giveCustomItem(itemSet, player, itemSet.getItem(newItemName));
            }
            playBreakSound(player);
            return null;
        } else return piece;
    }

    private boolean decreaseCustomArmorDurability(ItemStack piece, int damage) {
        KciItem custom = itemSet.getItem(piece);
        if (custom instanceof KciArmor) {
            return wrap((KciArmor) custom).decreaseDurability(piece, damage);
        }
        return false;
    }

    private boolean isReducedByArmor(EntityDamageEvent.DamageCause c) {
        return c == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || c == EntityDamageEvent.DamageCause.CONTACT || c == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || c == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || c == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                || c == EntityDamageEvent.DamageCause.FALLING_BLOCK || c == EntityDamageEvent.DamageCause.FLY_INTO_WALL || c == EntityDamageEvent.DamageCause.HOT_FLOOR
                || c == EntityDamageEvent.DamageCause.LAVA || c == EntityDamageEvent.DamageCause.PROJECTILE;
    }

    @EventHandler
    public void handleMending(PlayerItemMendEvent event) {
        // Handle it during PlayerExpChangeEvent instead
        if (itemSet.getItem(event.getItem()) != null) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void handleMendingXP(PlayerExpChangeEvent event) {
        EntityEquipment eq = event.getPlayer().getEquipment();
        if (eq == null) return;

        ItemStack mainHand = eq.getItemInMainHand();
        ItemStack offHand = eq.getItemInOffHand();

        ItemStack helmet = eq.getHelmet();
        ItemStack chest = eq.getChestplate();
        ItemStack leggs = eq.getLeggings();
        ItemStack boots = eq.getBoots();

        ItemStack[] allEquipment = {mainHand, offHand, helmet, chest, leggs, boots};
        long durAmount = event.getAmount() * 2L;

        for (ItemStack item : allEquipment) {
            KciItem custom = itemSet.getItem(item);
            if (custom instanceof KciTool && item.containsEnchantment(Enchantment.MENDING)) {
                KciTool tool = (KciTool) custom;

                durAmount -= wrap(tool).increaseDurability(item, durAmount);

                if (durAmount == 0) {
                    break;
                }
            }
        }

        eq.setItemInMainHand(allEquipment[0]);
        eq.setItemInOffHand(allEquipment[1]);
        eq.setHelmet(allEquipment[2]);
        eq.setChestplate(allEquipment[3]);
        eq.setLeggings(allEquipment[4]);
        eq.setBoots(allEquipment[5]);

        long newXP = durAmount / 2L;
        if (newXP > 0) event.setAmount((int) newXP);
        else event.setAmount(0);
    }
}
