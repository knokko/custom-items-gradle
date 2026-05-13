package nl.knokko.customitems.nms26;

import nl.knokko.customitems.nms21plus.KciNmsItems21Plus;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class KciNmsItems26 extends KciNmsItems21Plus {
    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getHoverName().toString();
    }
}
