package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.plugin.CustomItemsEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomShearsWrapper extends CustomToolWrapper {

    CustomShearsWrapper(CustomToolValues item) {
        super(item);
    }

    @Override
    public void onBlockBreak(Player player, ItemStack tool, boolean wasSolid, boolean wasFakeMainHand) {
        // Only lose durability when breaking non-solid blocks because we shear it
        if (!wasSolid && this.tool.getBlockBreakDurabilityLoss() != 0) {
            ItemStack newTool = decreaseDurability(tool, this.tool.getBlockBreakDurabilityLoss());
            if (tool != newTool) {
                if (newTool == null) {
                    CustomItemsEventHandler.playBreakSound(player);
                }
                if (wasFakeMainHand) {
                    player.getInventory().setItemInOffHand(newTool);
                } else {
                    player.getInventory().setItemInMainHand(newTool);
                }
            }
        }
    }
}