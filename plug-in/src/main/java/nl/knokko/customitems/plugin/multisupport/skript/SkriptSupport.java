package nl.knokko.customitems.plugin.multisupport.skript;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;

public class SkriptSupport {

    public static void onEnable(CustomItemsPlugin plugin) {
        try {
            Class.forName("ch.njol.skript.Skript");
            CISkriptAddon.enable(plugin);
        } catch (ClassNotFoundException missingSkript) {
            Bukkit.getLogger().info("Can't load class ch.njol.skript.Skript, so I assume Skript is not installed.");
        }
    }
}
