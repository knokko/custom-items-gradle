package nl.knokko.customitems.plugin.multisupport.skript;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;

public class SkriptSupport {

    public static void onEnable(CustomItemsPlugin plugin) {
        String testClass = "ch.njol.skript.Skript";
        try {
            Class.forName(testClass);
            CISkriptAddon.enable(plugin);
            Bukkit.getLogger().info("Enabled Skript integration");
        } catch (ClassNotFoundException missingSkript) {
            Bukkit.getLogger().info("Disabled OPTIONAL Skript integration: can't find " + testClass);
        }
    }
}
