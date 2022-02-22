package nl.knokko.customitems.plugin.multisupport.mimic;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;

public class MimicSupport {

    public static void onLoad(CustomItemsPlugin plugin) {
        try {
            Class.forName("ru.endlesscode.mimic.items.BukkitItemsRegistry");
            MimicItemRegistry.load(plugin);
        } catch (ClassNotFoundException noMimic) {
            Bukkit.getLogger().info("It looks like the Mimic plug-in is not installed, so support for it won't be enabled.");
        }
    }
}
