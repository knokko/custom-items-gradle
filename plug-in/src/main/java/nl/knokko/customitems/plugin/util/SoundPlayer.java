package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.sound.KciSound;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Objects;

public class SoundPlayer {

    public static String determineSoundName(KciSound sound) {
        if (sound.getVanillaSound() != null) return sound.getVanillaSound().key.toLowerCase(Locale.ROOT);
        else return "kci_" + sound.getCustomSound().getName();
    }

    private static Sound getVanillaSound(KciSound sound) {
        return KciNms.instance.getVanillaSound(sound.getVanillaSound().key);
    }

    private static SoundCategory determineSoundCategory(KciSound sound) {
        if (sound.getCustomSound() != null) {
            return SoundCategory.valueOf(sound.getCustomSound().getSoundCategory().name());
        }

        // Vanilla sounds will respect the sound category by default, so I can use just the playSound methods
        // without a SoundCategory parameter
        else return null;
    }

    public static void playSound(Location location, KciSound sound) {
        Objects.requireNonNull(location.getWorld());
        if (sound.getVanillaSound() != null) {
            location.getWorld().playSound(location, getVanillaSound(sound), sound.getVolume(), sound.getPitch());
        } else {
            location.getWorld().playSound(
                    location, "kci_" + sound.getCustomSound().getName(),
                    SoundCategory.valueOf(sound.getCustomSound().getSoundCategory().name()),
                    sound.getVolume(), sound.getPitch()
            );
        }
    }

    public static void playSound(Player player, KciSound sound) {
        if (sound.getVanillaSound() != null) {
            player.playSound(player.getLocation(), getVanillaSound(sound), sound.getVolume(), sound.getPitch());
        } else {
            player.playSound(
                    player.getLocation(), "kci_" + sound.getCustomSound().getName(),
                    SoundCategory.valueOf(sound.getCustomSound().getSoundCategory().name()),
                    sound.getVolume(), sound.getPitch()
            );
        }
    }

    private static final int JUKEBOX_RANGE = 16;

    public static void stopSound(Location location, KciSound sound, boolean forceRecordCategory) {
        SoundCategory category = determineSoundCategory(sound);

        for (Player player : Objects.requireNonNull(location.getWorld()).getPlayers()) {
            if (location.distance(player.getLocation()) <= JUKEBOX_RANGE * sound.getVolume()) {
                if (forceRecordCategory) player.stopSound(getVanillaSound(sound), SoundCategory.RECORDS);
                else if (category != null) player.stopSound(determineSoundName(sound), category);
                else player.stopSound(determineSoundName(sound));
            }
        }
    }

    public static void playBreakSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
    }
}
