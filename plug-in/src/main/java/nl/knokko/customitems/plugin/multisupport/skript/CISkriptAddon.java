package nl.knokko.customitems.plugin.multisupport.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;

class CISkriptAddon {

    static void enable(CustomItemsPlugin plugin) {
        if (!Skript.isAcceptRegistrations()) {
            Bukkit.getLogger().warning("Failed to enable Skript support because Skript no longer accepts registrations");
            return;
        }

        SkriptAddon addon = Skript.registerAddon(plugin);
        try {
            addon.loadClasses("nl.knokko.customitems.plugin.multisupport.skript", "elements");
        } catch (IOException ioTrouble) {
            Bukkit.getLogger().severe("Failed to load Custom Items Skript addon classes: " + ioTrouble.getMessage());
        }
    }
}
