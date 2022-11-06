package nl.knokko.customitems.plugin.multisupport.dualwield;

import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms.KciNms;
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

    private static final String[] DUAL_WIELD_KEY = {"dualWieldItem"};

    public static ItemStack purge(ItemStack original) {
        GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(original);
        int dualWieldValue = nbt.getOrDefault(DUAL_WIELD_KEY, 0);
        if (dualWieldValue == 1) {
            nbt.remove(DUAL_WIELD_KEY);
            return nbt.backToBukkit();
        }
        return original;
    }

    public static boolean isCorrupted(GeneralItemNBT nbt) {
        return nbt.getOrDefault(DUAL_WIELD_KEY, 0) != 0;
    }
}
