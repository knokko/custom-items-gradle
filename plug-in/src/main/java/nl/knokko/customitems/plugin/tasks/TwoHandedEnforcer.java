package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TwoHandedEnforcer implements Listener {

    public static void start(JavaPlugin plugin, ItemSetWrapper itemSet) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {

                ItemStack offItem = player.getInventory().getItemInOffHand();
                CustomItemValues customOffItem = itemSet.getItem(offItem);
                boolean dropOffItem = false;
                if (customOffItem != null && customOffItem.isTwoHanded()) {
                    // It is forbidden to equip a two-handed item in the offhand
                    dropOffItem = true;
                } else {
                    ItemStack mainItem = player.getInventory().getItemInMainHand();
                    CustomItemValues customMainItem = itemSet.getItem(mainItem);
                    if (customMainItem != null && customMainItem.isTwoHanded() && !ItemUtils.isEmpty(offItem)) {
                        // It is forbidden to carry any item in the offhand while holding a two-handed weapon in the main hand
                        dropOffItem = true;
                    }
                }

                if (dropOffItem) {
                    player.getWorld().dropItem(player.getLocation(), offItem);
                    player.getInventory().setItemInOffHand(null);
                }
            }
        }, 100, 100);
    }

    private final ItemSetWrapper itemSet;

    public TwoHandedEnforcer(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void enforceTwoHandedRestrictionsWithInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40) {
            CustomItemValues customItem = itemSet.getItem(event.getCursor());
            if (customItem != null && customItem.isTwoHanded()) {
                event.setCancelled(true);
            } else {
                CustomItemValues customMain = itemSet.getItem(event.getWhoClicked().getInventory().getItemInMainHand());
                if (customMain != null && customMain.isTwoHanded()) {
                    event.setCancelled(true);
                }
            }
        }

        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == event.getWhoClicked().getInventory().getHeldItemSlot()) {
            CustomItemValues customCursor = itemSet.getItem(event.getCursor());
            if (customCursor != null && customCursor.isTwoHanded() && !ItemUtils.isEmpty(event.getWhoClicked().getInventory().getItemInOffHand())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void enforceTwoHandedRestrictionsWithSwap(PlayerSwapHandItemsEvent event) {
        CustomItemValues customMain = itemSet.getItem(event.getOffHandItem());
        if (customMain != null && customMain.isTwoHanded()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void enforceTwoHandedRestrictionsWithHotBar(PlayerItemHeldEvent event) {
        CustomItemValues newMainItem = itemSet.getItem(event.getPlayer().getInventory().getItem(event.getNewSlot()));
        if (newMainItem != null && newMainItem.isTwoHanded() && !ItemUtils.isEmpty(event.getPlayer().getInventory().getItemInOffHand())) {
            event.setCancelled(true);
        }
    }
}
