package nl.knokko.customitems.nms21;

import nl.knokko.customitems.nms20plus.KciNmsItems20Plus;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

public class KciNmsItems21 extends KciNmsItems20Plus {
    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.y().getString();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void setEquippableAssetID(ItemMeta meta, EquipmentSlot slot, String id) {
        EquippableComponent component = meta.getEquippable();
        component.setModel(new NamespacedKey("minecraft", id));
        component.setSlot(slot);
        meta.setEquippable(component);
    }
}
