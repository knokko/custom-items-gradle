package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.enchantment.CustomEnchantmentProvider;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
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
        if (enchantment.provider == null) {
            return itemStack.getEnchantmentLevel(vanillaEnchantment(enchantment.getKey()));
        } else if (enchantment.provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) {
            if (CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions() != null) {
                return CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions().getLevel(itemStack, enchantment.getKey());
            } else {
                return 0;
            }
        } else {
            throw new Error("Unknown enchantment provider: " + enchantment.provider);
        }
    }

    public static void add(ItemStack itemStack, EnchantmentType enchantment, int level) {
        if (enchantment.provider == null) {
            itemStack.addUnsafeEnchantment(vanillaEnchantment(enchantment.getKey()), level);
        } else if (enchantment.provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) {
            if (CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions() != null) {
                CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions().add(itemStack, enchantment.getKey(), level);
            }
        } else {
            throw new Error("Unknown enchantment provider: " + enchantment.provider);
        }
    }

    public static void remove(ItemStack itemStack, EnchantmentType enchantment) {
        if (enchantment.provider == null) {
            itemStack.removeEnchantment(vanillaEnchantment(enchantment.getKey()));
        } else if (enchantment.provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) {
            if (CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions() != null) {
                CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions().remove(itemStack, enchantment.getKey());
            }
        } else {
            throw new Error("Unknown enchantment provider: " + enchantment.provider);
        }
    }
}
