package nl.knokko.customitems.plugin.multisupport.denizen;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class DenizenSupport {

    public static void onEnable(JavaPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                Class.forName("com.denizenscript.denizen.objects.ItemTag");
                CIDenizenAddon.enable();
            } catch (ClassNotFoundException missingDenizen) {
                Bukkit.getLogger().info("Can't load class 'com.denizenscript.denizen.objects.ItemTag', so I assume Denizen is not installed.");
            } catch (Exception unexpectedError) {
                Bukkit.getLogger().log(Level.SEVERE, "An unexpected error occurred while enabling Denizen support", unexpectedError);
            }
        });
    }
}
