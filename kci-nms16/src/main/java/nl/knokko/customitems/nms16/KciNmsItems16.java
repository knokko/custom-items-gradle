package nl.knokko.customitems.nms16;

import nl.knokko.customitems.nms.CustomItemNBT;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms16plus.KciNmsItems16Plus;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class KciNmsItems16 extends KciNmsItems16Plus {

    @Override
    public GeneralItemNBT generalReadOnlyNbt(ItemStack bukkitStack) {
        return new GeneralItemNBT16(CraftItemStack.asNMSCopy(bukkitStack), false);
    }

    @Override
    public GeneralItemNBT generalReadWriteNbt(ItemStack bukkitStack) {
        return new GeneralItemNBT16(CraftItemStack.asNMSCopy(bukkitStack), true);
    }

    @Override
    public void customReadOnlyNbt(ItemStack bukkitStack, Consumer<CustomItemNBT> useNBT) {
        useNBT.accept(new CustomItemNBT16(bukkitStack, false));
    }

    @Override
    public void customReadWriteNbt(ItemStack original, Consumer<CustomItemNBT> useNBT, Consumer<ItemStack> getNewStack) {
        CustomItemNBT16 nbt = new CustomItemNBT16(original, true);
        useNBT.accept(nbt);
        getNewStack.accept(nbt.getBukkitStack());
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getName().getString();
    }

    @Override
    public String getTagAsString(ItemStack stack) {
        net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        if (nms.hasTag()) {
            assert nms.getTag() != null;
            return nms.getTag().toString();
        } else {
            return null;
        }
    }
}
