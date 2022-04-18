package nl.knokko.customitems.plugin.multisupport.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class WorldGuardQuery {

    static boolean canInteract(Block block, Player player) {
        return WorldGuardPlugin.inst().createProtectionQuery().testBlockInteract(
                player, block
        );
    }
}
