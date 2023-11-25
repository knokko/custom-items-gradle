package nl.knokko.customitems.plugin.multisupport.itembridge;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBridgeSupport {

    private static final String TEST_CLASS = "com.jojodmo.itembridge.ItemBridgeListener";

    public static void onEnable(CustomItemsPlugin plugin) {
        try {
            Class.forName(TEST_CLASS);
            KciItemBridgeListener.setup(plugin);
            Bukkit.getLogger().info("Enabled ItemBridge integration");
        } catch (ClassNotFoundException noItemBridge) {
            Bukkit.getLogger().info("Disabled OPTIONAL ItemBridge integration: can't find " + TEST_CLASS);
        }
    }

    public static ItemStack fetchItem(String id, int amount) {
        try {
            Class.forName(TEST_CLASS);
            return KciItemBridgeListener.fetchItem(id, amount);
        } catch (ClassNotFoundException noItemBridge) {
            Bukkit.getLogger().severe("Can't fetch ItemBridge item " + id + " because ItemBridge is not installed");
            return new ItemStack(Material.AIR);
        }
    }

    public static boolean isItem(ItemStack candidate, String fullId) {
        try {
            Class.forName(TEST_CLASS);
            return KciItemBridgeListener.isBridgeItem(candidate, fullId);
        } catch (ClassNotFoundException noItemBridge) {
            Bukkit.getLogger().severe("Can't check ItemBridge item " + fullId + " because ItemBridge is not installed");
            return false;
        }
    }
}
