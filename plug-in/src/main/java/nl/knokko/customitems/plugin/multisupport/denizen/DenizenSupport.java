package nl.knokko.customitems.plugin.multisupport.denizen;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class DenizenSupport {

    public static void onEnable() {
        String testClass = "com.denizenscript.denizen.objects.ItemTag";
        try {
            Class.forName(testClass);
            try {
                Class.forName(
                        "nl.knokko.customitems.plugin.multisupport.denizen.CIDenizenAddon"
                ).getMethod("enable").invoke(null);
            } catch (Exception unexpectedError) {
                Bukkit.getLogger().log(
                        Level.SEVERE,
                        "An unexpected error occurred while enabling Denizen support",
                        unexpectedError
                );
            }
            Bukkit.getLogger().info("Enabled Denizen integration");
        } catch (ClassNotFoundException missingDenizen) {
            Bukkit.getLogger().info("Disabled OPTIONAL Denizen integration: can't find " + testClass);
        }
    }
}
