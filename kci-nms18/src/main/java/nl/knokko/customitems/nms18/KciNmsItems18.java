package nl.knokko.customitems.nms18;

import nl.knokko.customitems.nms.CustomItemNBT;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms16plus.KciNmsItems16Plus;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

class KciNmsItems18 extends KciNmsItems16Plus {

    @Override
    public GeneralItemNBT generalReadOnlyNbt(ItemStack bukkitStack) {
        return new GeneralItemNBT18(CraftItemStack.asNMSCopy(bukkitStack), false);
    }

    @Override
    public GeneralItemNBT generalReadWriteNbt(ItemStack bukkitStack) {
        return new GeneralItemNBT18(CraftItemStack.asNMSCopy(bukkitStack), true);
    }

    @Override
    public void customReadOnlyNbt(ItemStack bukkitStack, Consumer<CustomItemNBT> useNBT) {
        useNBT.accept(new CustomItemNBT18(bukkitStack, false));
    }

    @Override
    public void customReadWriteNbt(ItemStack original, Consumer<CustomItemNBT> useNBT, Consumer<ItemStack> getNewStack) {
        CustomItemNBT18 nbt = new CustomItemNBT18(original, true);
        useNBT.accept(nbt);
        getNewStack.accept(nbt.getBukkitStack());
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.w().getString();
    }
}
