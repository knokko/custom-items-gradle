package nl.knokko.customitems.nms;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface KciNmsItems {

    ItemStack createWithAttributes(String materialName, int amount, RawAttribute...attributes);

    ItemStack replaceAttributes(ItemStack original, RawAttribute...attributes);

    RawAttribute[] getAttributes(ItemStack stack);

    GeneralItemNBT generalReadOnlyNbt(ItemStack bukkitStack);

    GeneralItemNBT generalReadWriteNbt(ItemStack bukkitStack);

    /**
     * This method grants the opportunity to read the custom item nbt of the given
     * Bukkit ItemStack.
     *
     * @param bukkitStack The item stack whose custom item nbt is to be read
     * @param useNBT A lambda expression taking the custom item nbt of the given item
     * stack as parameter. It will be called before this method returns.
     */
    void customReadOnlyNbt(ItemStack bukkitStack, Consumer<CustomItemNBT> useNBT);

    /**
     * This method grants the opportunity to both read from and write to the custom
     * item nbt of the given Bukkit ItemStack.
     *
     * This method requires that the original item stack is replaced with a new item
     * stack because the original item stack will NOT be modified. The third
     * parameter is to remind users of that.
     *
     * Both lambda expression parameters will be called before this method returns.
     *
     * @param original The original Bukkit ItemStack 'to modify'
     * @param useNBT A lambda expression taking the CustomItemNBT as parameter.
     * Reading from and writing to the custom item nbt should be done in this
     * lambda expression.
     * @param getNewStack A lambda expression taking the new item stack as parameter.
     * This lambda should be used to replace the old item stack with the new item
     * modified item stack.
     */
    void customReadWriteNbt(ItemStack original, Consumer<CustomItemNBT> useNBT, Consumer<ItemStack> getNewStack);

    String getStackName(ItemStack stack);

    // TODO Stop using these methods since we no longer need them (they were needed in the past due to api-version)
    default String getMaterialName(Block block) {
        return block.getType().name();
    }

    default void setMaterial(ItemStack stack, String newMaterialName) {
        stack.setType(Material.getMaterial(newMaterialName));
    }

    default boolean isMaterialSolid(Block block) {
        return block.getType().isSolid();
    }

    default String getMaterialName(ItemStack stack) {
        return stack.getType().name();
    }

    default ItemStack createStack(String materialName, int amount) throws UnknownMaterialException {
        Material material = Material.getMaterial(materialName);
        if (material == null)
            throw new UnknownMaterialException(materialName);
        return new ItemStack(material, amount);
    }

    void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked, Plugin plugin);
}
