package nl.knokko.customitems.plugin;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class EnabledAreas {

    private List<String> worldWhitelist;
    private List<String> worldBlacklist;

    void update(FileConfiguration config) {
        this.worldWhitelist = config.getStringList("World whitelist");
        this.worldBlacklist = config.getStringList("World blacklist");
    }

    public boolean isEnabled(Location location) {
        String worldName = location.getWorld() != null ? location.getWorld().getName() : null;
        if (!worldWhitelist.isEmpty() && !worldWhitelist.contains(worldName)) {
            return false;
        }

        if (worldBlacklist.contains(worldName)) {
            return false;
        }

        // Currently, there are only worldName requirements, but I might add more in the future.

        return true;
    }
}
