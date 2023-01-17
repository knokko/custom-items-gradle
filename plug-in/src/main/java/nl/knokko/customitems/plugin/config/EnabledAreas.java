package nl.knokko.customitems.plugin.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class EnabledAreas {

    private List<String> worldWhitelist;
    private List<String> worldBlacklist;

    public void update(FileConfiguration config) {
        this.worldWhitelist = config.getStringList("World whitelist");
        this.worldBlacklist = config.getStringList("World blacklist");
    }

    public boolean isEnabled(Location location) {
        // Currently, there are only worldName requirements, but I might add more in the future.
        return isEnabled(location.getWorld());
    }

    /**
     * Returns false if the entire world is disabled, and true if at least 1 place in the world is not disabled
     */
    public boolean isEnabled(World world) {
        String worldName = world != null ? world.getName() : null;
        if (!worldWhitelist.isEmpty() && !worldWhitelist.contains(worldName)) {
            return false;
        }

        return !worldBlacklist.contains(worldName);
    }
}
