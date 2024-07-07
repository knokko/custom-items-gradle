package nl.knokko.customitems.nms20;

import nl.knokko.customitems.nms20plus.KciNmsItems20Plus;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class KciNmsItems20 extends KciNmsItems20Plus {

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.x().getString();
    }
}
