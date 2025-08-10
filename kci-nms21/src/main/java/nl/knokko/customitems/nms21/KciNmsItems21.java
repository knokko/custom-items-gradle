package nl.knokko.customitems.nms21;

import net.kyori.adventure.text.Component;
import nl.knokko.customitems.nms20plus.KciNmsItems20Plus;
//import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
//import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

public class KciNmsItems21 extends KciNmsItems20Plus {
    @Override
    public String getStackName(ItemStack stack) {
//        stack.getItemMeta().getItemName();
//        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
//        return nms.y().getString();
        return "test1234";
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void setEquippableAssetID(ItemMeta meta, EquipmentSlot slot, String id) {
        EquippableComponent component = meta.getEquippable();
        component.setModel(new NamespacedKey("minecraft", id));
        component.setSlot(slot);
        meta.setEquippable(component);
    }

    @Override
    public ItemStack translate(ItemStack item, String itemName, boolean translateDisplayName, int loreSize) {
        if (!item.editMeta(meta -> {
            meta.customName(item.effectiveName().append(Component.translatable("kci." + itemName + ".name")));
        })) {
            throw new RuntimeException("Failed to edit ItemMeta of " + item + " for " + itemName);
        }
        return item;
    }
}
