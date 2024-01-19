package nl.knokko.customitems.plugin.multisupport.dualwield;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DualWieldSupport {

    public static boolean isFakeMainHand(BlockBreakEvent event) {
        Plugin dualWieldPlugin = Bukkit.getServer().getPluginManager().getPlugin("DualWield");
        if (dualWieldPlugin != null && dualWieldPlugin.isEnabled()) {
            try {
                Method dualWieldChecker = dualWieldPlugin.getClass().getMethod("isDualWielding", Player.class);
                return (Boolean) dualWieldChecker.invoke(null, event.getPlayer());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException("Failed to get/use DualWield API", ex);
            }
        } else {
            return false;
        }
    }

    private static final String DUAL_WIELD_KEY = "dualWieldItem";

    public static ItemStack purge(ItemStack original) {
        NBT.modify(original, nbt -> {
            int dualWieldValue = nbt.getOrDefault(DUAL_WIELD_KEY, 0);
            if (dualWieldValue == 1) {
                nbt.removeKey(DUAL_WIELD_KEY);
            }
        });
        return original;
    }

    public static boolean isCorrupted(ReadableNBT nbt) {
        return nbt.getOrDefault(DUAL_WIELD_KEY, 0) != 0;
    }
}
