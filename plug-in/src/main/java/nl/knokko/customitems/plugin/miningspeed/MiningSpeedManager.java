package nl.knokko.customitems.plugin.miningspeed;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningSpeedManager {

    static final int UPDATE_PERIOD = 20;
    static final int EFFECT_DURATION = 2 * UPDATE_PERIOD;

    private final Map<UUID, MiningSpeedEntry> entries = new HashMap<>();

    public void startBreakingCustomBlock(
            Player player, Block block, CustomBlockValues customBlock,
            CIMaterial vanillaItem, CustomItemValues customItem
    ) {
        stopBreakingCustomBlock(player);

        int speed = customBlock.getMiningSpeed().getSpeedFor(vanillaItem, customItem);
        if (speed != 0) {
            this.entries.put(player.getUniqueId(), new MiningSpeedEntry(player, block, speed));
        }
    }

    public void stopBreakingCustomBlock(Player player) {
        MiningSpeedEntry oldEntry = this.entries.remove(player.getUniqueId());
        if (oldEntry != null) oldEntry.stopEffect();
    }

    public void start(JavaPlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::update, 0, UPDATE_PERIOD);
    }

    private void update() {
        entries.entrySet().removeIf(entry -> entry.getValue().update());
    }
}
