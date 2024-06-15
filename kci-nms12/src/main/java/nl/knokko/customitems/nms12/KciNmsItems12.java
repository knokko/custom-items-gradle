package nl.knokko.customitems.nms12;

import nl.knokko.customitems.nms.*;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
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
    public void customReadOnlyNbt(ItemStack bukkitStack, Consumer<CustomItemNBT> useNBT) {
        useNBT.accept(new CustomItemNBT12(bukkitStack, false));
    }

    @Override
    public void customReadWriteNbt(ItemStack original, Consumer<CustomItemNBT> useNBT, Consumer<ItemStack> getNewStack) {
        CustomItemNBT12 nbt = new CustomItemNBT12(original, true);
        useNBT.accept(nbt);
        getNewStack.accept(nbt.getBukkitStack());
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getName();
    }

    @Override
    public String getTagAsString(ItemStack stack) {
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        if (nms.hasTag()) {
            assert nms.getTag() != null;
            return nms.getTag().toString();
        } else {
            return null;
        }
    }

    @Override
    public void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin) {
        // There is no need to do anything because there are no smithing tables in minecraft 1.12
    }
}
