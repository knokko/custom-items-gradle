package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpdater;
import nl.knokko.customitems.recipe.result.ResultValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;

public class DropEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public DropEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    static boolean collectDrops(
            Collection<ItemStack> stacksToDrop, DropValues drop, Location location, Random random,
            ItemSetWrapper itemSet, ItemStack mainItem
    ) {

        // Make sure the required held items of drops are really required
        boolean shouldDrop = BlockEventHandler.shouldRequiredItemsAccept(drop.getRequiredHeldItems(), mainItem, itemSet);

        if (!drop.getAllowedBiomes().isAllowed(CIBiome.valueOf(location.getBlock().getBiome().name()))) {
            shouldDrop = false;
        }

        if (!shouldDrop) {
            return false;
        }

        ResultValues resultToDrop = drop.getOutputTable().pickResult(random);
        ItemStack stackToDrop = convertResultToItemStack(resultToDrop);
        boolean cancelDefaultDrops = false;

        if (stackToDrop != null) {

            // Cloning prevents very nasty errors
            stackToDrop = stackToDrop.clone();

            if (drop.shouldCancelNormalDrops()) {
                cancelDefaultDrops = true;
            }

            CustomItemValues itemToDrop = itemSet.getItem(stackToDrop);
            for (ItemStack potentialMerge : stacksToDrop) {
                if (stackToDrop.isSimilar(potentialMerge)) {

                    int remainingAmount;
                    if (itemToDrop == null) {
                        remainingAmount = potentialMerge.getMaxStackSize() - potentialMerge.getAmount();
                    } else {
                        remainingAmount = itemToDrop.getMaxStacksize() - potentialMerge.getAmount();
                    }

                    if (remainingAmount > 0) {
                        int consumedAmount = Math.min(remainingAmount, stackToDrop.getAmount());
                        stackToDrop.setAmount(stackToDrop.getAmount() - consumedAmount);
                        potentialMerge.setAmount(potentialMerge.getAmount() + consumedAmount);
                        if (stackToDrop.getAmount() <= 0) {
                            break;
                        }
                    }
                }
            }

            if (stackToDrop.getAmount() > 0) {
                stacksToDrop.add(stackToDrop);
            }
        }

        return cancelDefaultDrops;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleCustomMobDrops(EntityDeathEvent event) {
        ItemUpdater itemUpdater = CustomItemsPlugin.getInstance().getItemUpdater();

        // Remove corrupted or deleted custom items
        event.getDrops().removeIf(itemStack -> itemUpdater.maybeUpdate(itemStack) == null);

        // Upgrade/initialize potential custom items
        for (int index = 0; index < event.getDrops().size(); index++) {

            ItemStack original = event.getDrops().get(index);
            ItemStack replacement = itemUpdater.maybeUpdate(original);
            if (replacement != original) {
                event.getDrops().set(index, replacement);
            }
        }

        // The following work-around is needed to support mob drops when Libs Disguises is active
        // I don't know how or why, but the code above doesn't work if Libs Disguises is active
        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
            for (Entity nearbyEntity : event.getEntity().getNearbyEntities(2.0, 2.0, 2.0)) {
                if (nearbyEntity instanceof Item) {
                    Item nearbyItem = (Item) nearbyEntity;
                    ItemStack oldStack = nearbyItem.getItemStack();
                    ItemStack newStack = itemUpdater.maybeUpdate(oldStack);
                    if (oldStack != newStack) {
                        nearbyItem.setItemStack(newStack);
                    }
                }
            }
        });
    }

    private boolean isProjectileSource(ItemStack stack) {
        if (stack == null) return false;
        CIMaterial material = CIMaterial.valueOf(stack.getType().name());

        CustomItemValues customItem = itemSet.getItem(stack);
        return material == CIMaterial.BOW || material == CIMaterial.CROSSBOW || material == CIMaterial.TRIDENT ||
                customItem instanceof CustomWandValues || customItem instanceof CustomGunValues ||
                customItem instanceof CustomThrowableValues;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {

        if (!CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(event.getEntity().getLocation())) {
            return;
        }

        Collection<MobDropValues> drops = itemSet.getMobDrops(event.getEntity());
        Random random = new Random();

        ItemStack usedItem = null;
        EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();
        Player killer = event.getEntity().getKiller();

        if (lastDamageEvent != null && killer != null) {
            ItemStack mainItem = killer.getInventory().getItemInMainHand();
            EntityDamageEvent.DamageCause cause = lastDamageEvent.getCause();

            if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                usedItem = mainItem;
            } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                ItemStack offItem = killer.getInventory().getItemInOffHand();
                if (isProjectileSource(mainItem)) {
                    usedItem = killer.getInventory().getItemInMainHand();
                } else if (isProjectileSource(offItem)) {
                    usedItem = offItem;
                }
            }
        }

        boolean cancelDefaultDrops = false;
        Collection<ItemStack> stacksToDrop = new ArrayList<>();
        for (MobDropValues mobDrop : drops) {
            if (collectDrops(stacksToDrop, mobDrop.getDrop(), event.getEntity().getLocation(), random, itemSet, usedItem)) {
                cancelDefaultDrops = true;
            }
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Collection<ItemStack> stacksToKeep = new ArrayList<>();
            event.getDrops().removeIf(droppedItem -> {
                CustomItemValues droppedCustomItem = itemSet.getItem(droppedItem);
                if (droppedCustomItem != null && droppedCustomItem.shouldKeepOnDeath()) {
                    stacksToKeep.add(droppedItem);
                    return true;
                } else {
                    return false;
                }
            });
            if (!stacksToKeep.isEmpty()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                    for (ItemStack stackToKeep : stacksToKeep) {
                        player.getInventory().addItem(stackToKeep);
                    }
                });
            }
        }

        if (cancelDefaultDrops) {
            event.getDrops().clear();
        }

        event.getDrops().addAll(stacksToDrop);
    }
}
