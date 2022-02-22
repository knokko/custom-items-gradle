package nl.knokko.customitems.plugin.multisupport.mimic;

import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.Collection;

class MimicItemRegistry implements BukkitItemsRegistry {

    static boolean isMimicItem(ItemStack candidate, String id) {
        try {
            return isModernMimicItem(candidate, id);
        } catch (NoSuchMethodError tryLegacy) {
            return isLegacyMimicItem(candidate, id);
        }
    }

    static ItemStack fetchMimicItem(String id, int amount) {
        try {
            return fetchModernMimicItem(id, amount);
        } catch (NoSuchMethodError tryLegacy) {
            return fetchLegacyMimicItem(id, amount);
        }
    }

    private static boolean isModernMimicItem(ItemStack candidate, String id) {
        return id.equals(Mimic.getInstance().getItemsRegistry().getItemId(candidate));
    }

    private static boolean isLegacyMimicItem(ItemStack candidate, String id) {
        return id.equals(Bukkit.getServicesManager().getRegistration(
                BukkitItemsRegistry.class
        ).getProvider().getItemId(candidate));
    }

    private static ItemStack fetchModernMimicItem(String id, int amount) {
        return Mimic.getInstance().getItemsRegistry().getItem(id, amount);
    }

    private static ItemStack fetchLegacyMimicItem(String id, int amount) {
        return Bukkit.getServicesManager().getRegistration(
                BukkitItemsRegistry.class
        ).getProvider().getItem(id, null, amount);
    }

    static void load(CustomItemsPlugin plugin) {
        try {
            loadModern(plugin);
            Bukkit.getLogger().info("Enabling modern Mimic integration");
        } catch (NoSuchMethodError useLegacy) {
            loadLegacy(plugin);
            Bukkit.getLogger().info("Enabling legacy Mimic integration");
        }
    }

    private static void loadLegacy(CustomItemsPlugin plugin) {
        plugin.getServer().getServicesManager().register(
                BukkitItemsRegistry.class, new MimicItemRegistry(), plugin, ServicePriority.Normal
        );
    }

    private static void loadModern(CustomItemsPlugin plugin) {
        Mimic.getInstance().registerItemsRegistry(new MimicItemRegistry(), MimicApiLevel.VERSION_0_7, plugin);
    }

    @Override
    public String getId() {
        return "knokkoscustomitems";
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
