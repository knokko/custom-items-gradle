package nl.knokko.customitems.plugin.multisupport.denizen;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class DenizenSupport {

    public static void onEnable() {
        String testClass = "com.denizenscript.denizen.objects.ItemTag";
        try {
            Class.forName(testClass);
            CIDenizenAddon.enable();
            Bukkit.getLogger().info("Enabled Denizen integration");
        } catch (ClassNotFoundException missingDenizen) {
            Bukkit.getLogger().info("Disabled OPTIONAL Denizen integration: can't find " + testClass);
        } catch (Exception unexpectedError) {
            Bukkit.getLogger().log(Level.SEVERE, "An unexpected error occurred while enabling Denizen support", unexpectedError);
        }
    }
}
