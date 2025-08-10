package nl.knokko.customitems.nms21;

import nl.knokko.customitems.nms21plus.KciNmsItems21Plus;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class KciNmsItems21 extends KciNmsItems21Plus {
    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.y().getString();
    }
}
