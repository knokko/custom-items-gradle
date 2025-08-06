package nl.knokko.customitems.plugin.multisupport.skript;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SkriptSupport {

    public static void onEnable(CustomItemsPlugin plugin) {
        String testClass = "ch.njol.skript.Skript";
        try {
            Class.forName(testClass);
            try {
                Class.forName(
                        "nl.knokko.customitems.plugin.multisupport.skript.CISkriptAddon"
                ).getMethod("enable", CustomItemsPlugin.class).invoke(null, plugin);
            } catch (Exception failed) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to enable Skript integration", failed);
            }
            Bukkit.getLogger().info("Enabled Skript integration");
        } catch (ClassNotFoundException missingSkript) {
            Bukkit.getLogger().info("Disabled OPTIONAL Skript integration: can't find " + testClass);
        }
    }
}
