package nl.knokko.customitems.nms21;

import nl.knokko.customitems.nms20plus.KciNmsItems20Plus;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class KciNmsItems21 extends KciNmsItems20Plus {
    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.y().getString();
    }
}
