package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.plugin.util.SoundPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomShearsWrapper extends CustomToolWrapper {

    CustomShearsWrapper(CustomToolValues item) {
        super(item);
    }

    @Override
    public void onBlockBreak(
            Player player, ItemStack tool, boolean wasSolid, boolean wasFakeMainHand, int numBrokenBlocks
    ) {

        // Only lose durability when breaking non-solid blocks because we shear it
        if (!wasSolid) {
            int durabilityFactor = this.tool.getMultiBlockBreak().shouldStackDurabilityCost() ? numBrokenBlocks : 1;
            boolean broke = decreaseDurability(tool, this.tool.getBlockBreakDurabilityLoss() * durabilityFactor);
            if (broke) {
                SoundPlayer.playBreakSound(player);
                tool = null;
            }
            if (wasFakeMainHand) {
                player.getInventory().setItemInOffHand(tool);
            } else {
                player.getInventory().setItemInMainHand(tool);
            }
        }
    }
}
