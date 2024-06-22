package nl.knokko.customitems.nms18;

import nl.knokko.customitems.nms18plus.KciNmsItems18Plus;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class KciNmsItems18 extends KciNmsItems18Plus {

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.w().getString();
    }
}
