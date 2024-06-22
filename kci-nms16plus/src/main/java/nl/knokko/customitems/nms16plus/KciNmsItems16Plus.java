package nl.knokko.customitems.nms16plus;

import nl.knokko.customitems.nms13plus.KciNmsItems13Plus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public abstract class KciNmsItems16Plus extends KciNmsItems13Plus {

    @Override
    public void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(
                new SmithingBlockEventHandler(shouldBeBlocked), plugin
        );
    }

    @Override
    public void setCustomModelData(ItemMeta meta, int data) {
        meta.setCustomModelData(data);
    }

    private static class SmithingBlockEventHandler implements Listener {

        private final Predicate<ItemStack> shouldBeBlocked;

        SmithingBlockEventHandler(Predicate<ItemStack> shouldBeBlocked) {
            this.shouldBeBlocked = shouldBeBlocked;
        }

        @EventHandler
        public void blockSmithingTableUpgrades(PrepareSmithingEvent event) {
            ItemStack toUpgrade = event.getInventory().getItem(0);
            if (shouldBeBlocked.test(toUpgrade)) {
                event.setResult(new ItemStack(Material.AIR));
            }
        }
    }
}
