package nl.knokko.customitems.nms13;

import nl.knokko.customitems.nms.CustomItemNBT;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms13plus.KciNmsItems13Plus;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Predicate;

class KciNmsItems13 extends KciNmsItems13Plus {

    @Override
    public GeneralItemNBT generalReadOnlyNbt(ItemStack bukkitStack) {
        return new GeneralItemNBT13(CraftItemStack.asNMSCopy(bukkitStack), false);
    }

    @Override
    public GeneralItemNBT generalReadWriteNbt(ItemStack bukkitStack) {
        return new GeneralItemNBT13(CraftItemStack.asNMSCopy(bukkitStack), true);
    }

    @Override
    public void customReadOnlyNbt(ItemStack bukkitStack, Consumer<CustomItemNBT> useNBT) {
        useNBT.accept(new CustomItemNBT13(bukkitStack, false));
    }

    @Override
    public void customReadWriteNbt(ItemStack original, Consumer<CustomItemNBT> useNBT, Consumer<ItemStack> getNewStack) {
        CustomItemNBT13 nbt = new CustomItemNBT13(original, true);
        useNBT.accept(nbt);
        getNewStack.accept(nbt.getBukkitStack());
    }

    @Override
    public String getStackName(ItemStack stack) {
        net.minecraft.server.v1_13_R2.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        return nms.getName().getString();
    }

    @Override
    public String getTagAsString(ItemStack stack) {
        net.minecraft.server.v1_13_R2.ItemStack nms = CraftItemStack.asNMSCopy(stack);
        if (nms.hasTag()) {
            assert nms.getTag() != null;
            return nms.getTag().toString();
        } else {
            return null;
        }
    }

    @Override
    public void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin) {
        // There are no smithing tables in minecraft 1.13
    }
}
