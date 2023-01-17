package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CustomHelmet3dValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class Helmet3dEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public Helmet3dEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void equip3dHelmets(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {

            InventoryAction action = event.getAction();

            // For some reason, the result is ALLOW, even when nothing will happen
            if (event.getResult() == Event.Result.ALLOW &&
                    (action == InventoryAction.PLACE_ALL || action == InventoryAction.SWAP_WITH_CURSOR)) {
                int slot = event.getSlot();

                // 39 is the helmet slot
                if (slot == 39) {

                    ItemStack newCursor = event.getCurrentItem().clone();

                    ItemStack newArmor = event.getCursor().clone();
                    CustomItemValues newCustomArmor = itemSet.getItem(newArmor);

                    if (newCustomArmor instanceof CustomHelmet3dValues) {
                        HumanEntity player = event.getWhoClicked();

                        Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                            ItemStack actualNewArmor = player.getInventory().getHelmet();
                            if (!Objects.equals(actualNewArmor, newArmor)) {
                                player.getInventory().setHelmet(newArmor);
                                player.setItemOnCursor(newCursor);
                            }
                        });
                    }
                }
            }
        }
    }

    @EventHandler
    public void equip3dHelmets(PlayerInteractEvent event) {
        ItemStack eventItem = event.getItem();
        CustomItemValues customEventItem = itemSet.getItem(eventItem);

        // Equip 3d custom helmets upon right click
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                && customEventItem instanceof CustomHelmet3dValues) {
            PlayerInventory inv = event.getPlayer().getInventory();

            EquipmentSlot hand = event.getHand();

            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                ItemStack oldHelmet = inv.getHelmet();
                if (hand == EquipmentSlot.HAND) {
                    ItemStack oldItem = inv.getItemInMainHand();
                    if (itemSet.getItem(oldItem) instanceof CustomHelmet3dValues) {
                        inv.setItemInMainHand(oldHelmet);
                        inv.setHelmet(oldItem);
                    }
                } else if (hand == EquipmentSlot.OFF_HAND) {
                    ItemStack oldItem = inv.getItemInOffHand();
                    if (itemSet.getItem(oldItem) instanceof CustomHelmet3dValues) {
                        inv.setItemInOffHand(oldHelmet);
                        inv.setHelmet(oldItem);
                    }
                }
            });
        }
    }
}
