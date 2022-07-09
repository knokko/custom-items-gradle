package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.enchantment.EnchantmentType;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BukkitEnchantments {

    private static Enchantment vanillaEnchantment(String key) {
        return Objects.requireNonNull(
                Enchantment.getByKey(NamespacedKey.minecraft(key)),
                "Can't find enchantment with key '" + key + "'"
        );
    }

    /**
     * @return The enchantment level, or 0 if the item stack doesn't have the enchantment
     */
    public static int getLevel(ItemStack itemStack, EnchantmentType enchantment) {
        return itemStack.getEnchantmentLevel(vanillaEnchantment(enchantment.getKey()));
    }

    public static void add(ItemStack itemStack, EnchantmentType enchantment, int level) {
        itemStack.addUnsafeEnchantment(vanillaEnchantment(enchantment.getKey()), level);
    }

    public static void remove(ItemStack itemStack, EnchantmentType enchantment) {
        itemStack.removeEnchantment(vanillaEnchantment(enchantment.getKey()));
    }
}
