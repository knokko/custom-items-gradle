package nl.knokko.customitems.nms20;

import nl.knokko.customitems.nms18plus.KciNmsItems18Plus;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class KciNmsItems20 extends KciNmsItems18Plus {

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.y().getString();
    }
}
