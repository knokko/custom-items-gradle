package nl.knokko.customitems.plugin.multisupport.itembridge;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBridgeSupport {

    public static void onEnable(CustomItemsPlugin plugin) {
        try {
            Class.forName("com.jojodmo.itembridge.ItemBridgeListener");
            KciItemBridgeListener.setup(plugin);
            Bukkit.getLogger().info("Enabling ItemBridge plug-in support");
        } catch (ClassNotFoundException noItemBridge) {
            Bukkit.getLogger().info("The ItemBridge plug-in doesn't seem to be installed, so support for it won't be enabled.");
        }
    }

    public static ItemStack fetchItem(String id, int amount) {
        try {
            Class.forName("com.jojodmo.itembridge.ItemBridgeListener");
            return KciItemBridgeListener.fetchItem(id, amount);
        } catch (ClassNotFoundException noItemBridge) {
            Bukkit.getLogger().severe("Can't fetch ItemBridge item " + id + " because ItemBridge is not installed");
            return new ItemStack(Material.AIR);
        }
    }

    public static boolean isItem(ItemStack candidate, String fullId) {
        try {
            Class.forName("com.jojodmo.itembridge.ItemBridgeListener");
            return KciItemBridgeListener.isBridgeItem(candidate, fullId);
        } catch (ClassNotFoundException noItemBridge) {
            Bukkit.getLogger().severe("Can't check ItemBridge item " + fullId + " because ItemBridge is not installed");
            return false;
        }
    }
}
