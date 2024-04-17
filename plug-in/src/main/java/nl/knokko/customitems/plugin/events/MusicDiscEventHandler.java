package nl.knokko.customitems.plugin.events;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciMusicDisc;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.util.SoundPlayer;
import nl.knokko.customitems.plugin.multisupport.actionbarapi.ActionBarAPISupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
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

public class MusicDiscEventHandler implements Listener {

    private final ItemSetWrapper itemSet;

    public MusicDiscEventHandler(ItemSetWrapper itemSet) {
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
        KciItem oldCustomRecord = itemSet.getItem(oldRecord);
        if (oldCustomRecord instanceof KciMusicDisc) {
            SoundPlayer.stopSound(block.getLocation(), ((KciMusicDisc) oldCustomRecord).getMusic(), false);
            return;
        }

        // Let the old record go out normally
        if (!ItemUtils.isEmpty(oldRecord)) return;

        KciItem customItem = itemSet.getItem(event.getItem());
        if (customItem instanceof KciMusicDisc) {
            KciSound music = ((KciMusicDisc) customItem).getMusic();
            SoundPlayer.playSound(block.getLocation(), music);

            Bukkit.getScheduler().scheduleSyncDelayedTask(CustomItemsPlugin.getInstance(), () -> {
                VSoundType undesiredSound = VSoundType.valueOf(customItem.getOtherMaterial().name());
                SoundPlayer.stopSound(block.getLocation(), KciSound.createQuick(undesiredSound, 4f, 1f), true);
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
        KciItem customRecord = itemSet.getItem(jukebox.getRecord());
        if (customRecord instanceof KciMusicDisc) {
            SoundPlayer.stopSound(event.getBlock().getLocation(), ((KciMusicDisc) customRecord).getMusic(), false);
        }
    }
}
