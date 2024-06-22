package nl.knokko.customitems.nms12;

import nl.knokko.customitems.nms.*;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

class KciNmsItems12 implements KciNmsItems {

    @Override
    public ItemStack createWithAttributes(String materialName, int amount, RawAttribute... attributes) {
        return ItemAttributes.createWithAttributes(materialName, amount, attributes);
    }

    @Override
    public ItemStack replaceAttributes(ItemStack original, RawAttribute... attributes) {
        return ItemAttributes.replaceAttributes(original, attributes);
    }

    @Override
    public RawAttribute[] getAttributes(ItemStack stack) {
        return ItemAttributes.getAttributes(stack);
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getName();
    }

    @Override
    public void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin) {
        // There is no need to do anything because there are no smithing tables in minecraft 1.12
    }
}
