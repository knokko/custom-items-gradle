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

    String getStackName(ItemStack stack);

    String getTagAsString(ItemStack stack);

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
