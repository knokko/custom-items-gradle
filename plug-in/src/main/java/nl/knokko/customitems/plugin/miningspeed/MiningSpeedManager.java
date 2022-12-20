package nl.knokko.customitems.plugin.miningspeed;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.miningspeed.CustomMiningSpeedEntry;
import nl.knokko.customitems.block.miningspeed.VanillaMiningSpeedEntry;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
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
        stopBreakingCustomBlockEffect(player);

        int speed = customBlock.getMiningSpeed().getSpeedFor(vanillaItem, customItem);

        if (speed != 0) {
            this.entries.put(player.getUniqueId(), new MiningSpeedEntry(player, block, speed));
        }
    }

    public void maybeCancelCustomBlockBreak(BlockBreakEvent event, ItemSetWrapper itemSet) {
        CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(event.getBlock());

        if (customBlock != null) {
            ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
            CustomItemValues customTool = itemSet.getItem(tool);

            if (customBlock.getMiningSpeed().getDefaultValue() < 0) {

                // Because axes can break mushroom blocks faster, we should block them unless the user explicitly allows them
                if (tool.getType().name().endsWith("_AXE")) {
                    for (VanillaMiningSpeedEntry entry : customBlock.getMiningSpeed().getVanillaEntries()) {
                        if (tool.getType().name().equals(entry.getMaterial().name())) return;
                    }

                    if (customTool != null) {
                        for (CustomMiningSpeedEntry entry : customBlock.getMiningSpeed().getCustomEntries()) {
                            if (customTool.getName().equals(entry.getItem().getName())) return;
                        }
                    }

                    event.setCancelled(true);
                }
            }

            // If the mining speed is below -4, the block should be unbreakable
            if (customBlock.getMiningSpeed().getSpeedFor(CIMaterial.valueOf(tool.getType().name()), customTool) < -4) {
                event.setCancelled(true);
            }
        }
    }

    public void stopBreakingCustomBlockEffect(Player player) {
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
