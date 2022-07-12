package nl.knokko.customitems.plugin.multisupport.denizen;

import org.bukkit.Bukkit;

public class DenizenSupport {

    public static void onEnable() {
        try {
            Class.forName("com.denizenscript.denizen.objects.ItemTag");
            CIDenizenAddon.enable();
        } catch (ClassNotFoundException missingDenizen) {
            Bukkit.getLogger().info("Can't load class 'com.denizenscript.denizen.objects.ItemTag', so I assume Denizen is not installed.");
        }
    }
}
