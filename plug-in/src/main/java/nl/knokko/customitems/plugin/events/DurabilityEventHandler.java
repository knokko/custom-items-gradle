package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

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

                ItemStack oldHelmet = e.getHelmet();
                ItemStack newHelmet = decreaseCustomArmorDurability(oldHelmet, helmetDamage);
                if (oldHelmet != newHelmet) {
                    if (newHelmet == null) {
                        CustomItemValues helmet = itemSet.getItem(oldHelmet);
                        if (helmet instanceof CustomArmorValues) {
                            String newItemName = checkBrokenCondition(helmet.getReplacementConditions());
                            if (newItemName != null) {
                                ItemUtils.giveCustomItem(itemSet, player, itemSet.getItem(newItemName));
                            }
                        }
                        playBreakSound(player);
                    }
                    e.setHelmet(newHelmet);
                }

                ItemStack oldChestplate = e.getChestplate();
                ItemStack newChestplate = decreaseCustomArmorDurability(oldChestplate, armorDamage);
                if (oldChestplate != newChestplate) {
                    if (newChestplate == null) {
                        CustomItemValues plate = itemSet.getItem(oldChestplate);
                        if (plate instanceof CustomArmorValues) {
                            String newItemName = checkBrokenCondition(plate.getReplacementConditions());
                            if (newItemName != null) {
                                ItemUtils.giveCustomItem(itemSet, player, itemSet.getItem(newItemName));
                            }
                        }
                        playBreakSound(player);
                    }
                    e.setChestplate(newChestplate);
                }

                ItemStack oldLeggings = e.getLeggings();
                ItemStack newLeggings = decreaseCustomArmorDurability(oldLeggings, armorDamage);
                if (oldLeggings != newLeggings) {
                    if (newLeggings == null) {
                        CustomItemValues leggings = itemSet.getItem(oldLeggings);
                        if (leggings instanceof CustomArmorValues) {
                            String newItemName = checkBrokenCondition(leggings.getReplacementConditions());
                            if (newItemName != null) {
                                ItemUtils.giveCustomItem(itemSet, player, itemSet.getItem(newItemName));
                            }
                        }
                        playBreakSound(player);
                    }
                    e.setLeggings(newLeggings);
                }

                ItemStack oldBoots = e.getBoots();
                ItemStack newBoots = decreaseCustomArmorDurability(oldBoots, armorDamage);
                if (oldBoots != newBoots) {
                    if (newBoots == null) {
                        CustomItemValues boots = itemSet.getItem(oldBoots);
                        if (boots instanceof CustomArmorValues) {
                            String newItemName = checkBrokenCondition(boots.getReplacementConditions());
                            if (newItemName != null) {
                                ItemUtils.giveCustomItem(itemSet, player, itemSet.getItem(newItemName));
                            }
                        }
                        playBreakSound(player);
                    }
                    e.setBoots(newBoots);
                }
            }

            // There is no nice shield blocking event, so this dirty check will have to do
            if (player.isBlocking() && event.getFinalDamage() == 0.0) {

                UsedShield usedShield = determineUsedShield(itemSet, player);

                if (usedShield.customShield != null && event.getDamage() >= usedShield.customShield.getThresholdDamage()) {
                    int lostDurability = (int) (event.getDamage()) + 1;
                    if (usedShield.inOffhand) {
                        ItemStack oldOffHand = player.getInventory().getItemInOffHand();
                        ItemStack newOffHand = wrap(usedShield.customShield).decreaseDurability(oldOffHand, lostDurability);
                        if (oldOffHand != newOffHand) {
                            player.getInventory().setItemInOffHand(newOffHand);
                            if (newOffHand == null) {
                                String newItemName = checkBrokenCondition(usedShield.customShield.getReplacementConditions());
                                if (newItemName != null) {
                                    player.getInventory().setItemInOffHand(CustomItemWrapper.wrap(itemSet.getItem(newItemName)).create(1));
                                }
                                playBreakSound(player);
                            }
                        }
                    } else {
                        ItemStack oldMainHand = player.getInventory().getItemInMainHand();
                        ItemStack newMainHand = wrap(usedShield.customShield).decreaseDurability(oldMainHand, lostDurability);
                        if (oldMainHand != newMainHand) {
                            player.getInventory().setItemInMainHand(newMainHand);
                            if (newMainHand == null) {
                                String newItemName = checkBrokenCondition(usedShield.customShield.getReplacementConditions());
                                if (newItemName != null) {
                                    player.getInventory().setItemInMainHand(CustomItemWrapper.wrap(itemSet.getItem(newItemName)).create(1));
                                }
                                playBreakSound(player);
                            }
                        }
                    }
                }
            }
        }
    }

    static UsedShield determineUsedShield(ItemSetWrapper itemSet, Player player) {
        CustomShieldValues shield = null;
        boolean offhand = true;

        ItemStack offStack = player.getInventory().getItemInOffHand();
        ItemStack mainStack = player.getInventory().getItemInMainHand();

        CustomItemValues customOff = itemSet.getItem(offStack);
        if (customOff instanceof CustomShieldValues) {
            shield = (CustomShieldValues) customOff;
        }

        CustomItemValues customMain = itemSet.getItem(mainStack);
        if (customMain instanceof CustomShieldValues) {
            shield = (CustomShieldValues) customMain;
            offhand = false;
        } else if (KciNms.instance.items.getMaterialName(mainStack).equals(CIMaterial.SHIELD.name())) {
            shield = null;
            offhand = false;
        }

        return new UsedShield(offhand, offhand ? offStack : mainStack, shield);
    }

    static class UsedShield {

        final boolean inOffhand;
        final ItemStack itemStack;
        final CustomShieldValues customShield;

        UsedShield(boolean inOffHand, ItemStack itemStack, CustomShieldValues customShield) {
            this.inOffhand = inOffHand;
            this.itemStack = itemStack;
            this.customShield = customShield;
        }
    }

    private ItemStack decreaseCustomArmorDurability(ItemStack piece, int damage) {
        CustomItemValues custom = itemSet.getItem(piece);
        if (custom instanceof CustomArmorValues) {
            return wrap((CustomArmorValues) custom).decreaseDurability(piece, damage);
        }
        return piece;
    }

    private boolean isReducedByArmor(EntityDamageEvent.DamageCause c) {
        return c == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || c == EntityDamageEvent.DamageCause.CONTACT || c == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || c == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || c == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                || c == EntityDamageEvent.DamageCause.FALLING_BLOCK || c == EntityDamageEvent.DamageCause.FLY_INTO_WALL || c == EntityDamageEvent.DamageCause.HOT_FLOOR
                || c == EntityDamageEvent.DamageCause.LAVA || c == EntityDamageEvent.DamageCause.PROJECTILE;
    }

    @EventHandler
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
        ItemStack[] oldEquipment = Arrays.copyOf(allEquipment, allEquipment.length);
        int durAmount = event.getAmount() * 2;

        for (int index = 0; index < allEquipment.length; index++) {
            ItemStack item = allEquipment[index];
            CustomItemValues custom = itemSet.getItem(item);
            if (custom != null) {
                if (item.containsEnchantment(Enchantment.MENDING) && custom instanceof CustomToolValues) {
                    CustomToolValues tool = (CustomToolValues) custom;

                    CustomToolWrapper.IncreaseDurabilityResult increaseResult = wrap(tool).increaseDurability(item, durAmount);
                    durAmount -= increaseResult.increasedAmount;
                    allEquipment[index] = increaseResult.stack;

                    if (durAmount == 0) {
                        break;
                    }
                }
            }
        }

        if (oldEquipment[0] != allEquipment[0]) {
            eq.setItemInMainHand(allEquipment[0]);
        }
        if (oldEquipment[1] != allEquipment[1]) {
            eq.setItemInOffHand(allEquipment[1]);
        }
        if (oldEquipment[2] != allEquipment[2]) {
            eq.setHelmet(allEquipment[2]);
        }
        if (oldEquipment[3] != allEquipment[3]) {
            eq.setChestplate(allEquipment[3]);
        }
        if (oldEquipment[4] != allEquipment[4]) {
            eq.setLeggings(allEquipment[4]);
        }
        if (oldEquipment[5] != allEquipment[5]) {
            eq.setBoots(allEquipment[5]);
        }

        int newXP = durAmount / 2;
        event.setAmount(newXP);
    }
}
