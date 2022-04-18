package nl.knokko.customitems.plugin.multisupport.worldguard;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardSupport {

    public static boolean canInteract(Block target, Player player) {
        try {
            return WorldGuardQuery.canInteract(target, player);
        } catch (NoClassDefFoundError | NoSuchMethodError nope) {
            return true;
        }
    }
}
