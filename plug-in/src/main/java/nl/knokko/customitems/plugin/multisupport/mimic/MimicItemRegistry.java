package nl.knokko.customitems.plugin.multisupport.mimic;

import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.Collection;

public class MimicItemRegistry implements BukkitItemsRegistry {

    static void load(CustomItemsPlugin plugin) {
        plugin.getServer().getServicesManager().register(
                BukkitItemsRegistry.class, new MimicItemRegistry(), plugin, ServicePriority.Normal
        );
    }

    @Override
    public String getId() {
        return "KnokkosCustomItems";
    }

    @Override
    public boolean isEnabled() {
        return CustomItemsPlugin.getInstance() != null;
    }

    @Override
    public Collection<String> getKnownIds() {
        return CustomItemsApi.getAllItemNames();
    }

    @Override
    public ItemStack getItem(String itemId, Object payload, int amount) {
        return CustomItemsApi.createItemStack(itemId, amount);
    }

    @Override
    public String getItemId(ItemStack item) {
        return CustomItemsApi.getItemName(item);
    }

    @Override
    public boolean isItemExists(String itemId) {
        return CustomItemsApi.hasItem(itemId);
    }
}
