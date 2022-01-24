package nl.knokko.customitems.plugin.multisupport.itembridge;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;

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
}
