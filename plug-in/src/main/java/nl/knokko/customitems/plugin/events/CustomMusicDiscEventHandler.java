package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomMusicDiscValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.SoundPlayer;
import nl.knokko.customitems.plugin.multisupport.actionbarapi.ActionBarAPISupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class CustomMusicDiscEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public CustomMusicDiscEventHandler(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleCustomMusicDiscs(PlayerInteractEvent event) {
        // Jukebox doesn't have a getRecord() method in MC 1.12.2
        if (!KciNms.instance.useNewCommands()) return;

        Block block = event.getClickedBlock();
        if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        BlockState blockState = block.getState();
        if (!(blockState instanceof Jukebox)) return;
        Jukebox jukebox = (Jukebox) blockState;

        ItemStack oldRecord = jukebox.getRecord();
        CustomItemValues oldCustomRecord = itemSet.getItem(oldRecord);
        if (oldCustomRecord instanceof CustomMusicDiscValues) {
            SoundPlayer.stopSound(block.getLocation(), ((CustomMusicDiscValues) oldCustomRecord).getMusic(), false);
            return;
        }

        // Let the old record go out normally
        if (!ItemUtils.isEmpty(oldRecord)) return;

        CustomItemValues customItem = itemSet.getItem(event.getItem());
        if (customItem instanceof CustomMusicDiscValues) {
            SoundValues music = ((CustomMusicDiscValues) customItem).getMusic();
            SoundPlayer.playSound(block.getLocation(), music);

            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                VanillaSoundType undesiredSound = VanillaSoundType.valueOf(customItem.getOtherMaterial().name());
                SoundPlayer.stopSound(block.getLocation(), SoundValues.createQuick(undesiredSound, 4f, 1f), true);
            }, 5);

            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                String soundName = music.getVanillaSound() != null ? music.getVanillaSound().name().toLowerCase(Locale.ROOT) : music.getCustomSound().getName();
                soundName = soundName.replace('_', ' ');
                ActionBarAPISupport.sendActionBar(event.getPlayer(), ChatColor.GREEN + "Now playing: " + soundName);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void stopCustomMusicDisc(BlockBreakEvent event) {
        // Jukebox doesn't have a getRecord() method in MC 1.12.2
        if (!KciNms.instance.useNewCommands()) return;

        BlockState blockState = event.getBlock().getState();
        if (!(blockState instanceof Jukebox)) return;

        Jukebox jukebox = (Jukebox) blockState;
        CustomItemValues customRecord = itemSet.getItem(jukebox.getRecord());
        if (customRecord instanceof CustomMusicDiscValues) {
            SoundPlayer.stopSound(event.getBlock().getLocation(), ((CustomMusicDiscValues) customRecord).getMusic(), false);
        }
    }
}
