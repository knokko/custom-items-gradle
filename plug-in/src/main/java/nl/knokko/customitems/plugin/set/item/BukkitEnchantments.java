package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.enchantment.CustomEnchantmentProvider;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.multisupport.crazyenchantments.CrazyEnchantmentsSupport;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BukkitEnchantments {

    private static Enchantment vanillaEnchantment(VEnchantmentType enchantment) {
        // Work around MULTSHOT typo
        String enchantmentName = enchantment == VEnchantmentType.MULTSHOT ? "MULTISHOT" : enchantment.name();
        // Warning: do NOT use Enchantment.getByKey because that is not supported in minecraft 1.12
        return Objects.requireNonNull(Enchantment.getByName(enchantmentName));
    }

    private static boolean isForThisMcVersion(VEnchantmentType enchantment) {
        return enchantment.provider != null || enchantment.version <= KciNms.mcVersion;
    }

    /**
     * @return The enchantment level, or 0 if the item stack doesn't have the enchantment
     */
    public static int getLevel(ItemStack itemStack, VEnchantmentType enchantment) {
        if (!isForThisMcVersion(enchantment)) return 0;
        if (enchantment.provider == null) {
            return itemStack.getEnchantmentLevel(vanillaEnchantment(enchantment));
        } else if (enchantment.provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) {
            if (CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions(false) != null) {
                return CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions(true).getLevel(itemStack, enchantment.getKey());
            } else {
                return 0;
            }
        } else {
            throw new Error("Unknown enchantment provider: " + enchantment.provider);
        }
    }

    public static ItemStack add(ItemStack itemStack, VEnchantmentType enchantment, int level) {
        if (!isForThisMcVersion(enchantment)) {
            throw new IllegalArgumentException("Enchantment " + enchantment + " is not supported by this MC version: " + KciNms.mcVersion);
        }
        if (enchantment.provider == null) {
            itemStack.addUnsafeEnchantment(vanillaEnchantment(enchantment), level);
        } else if (enchantment.provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) {
            if (CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions(true) != null) {
                return CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions(true).add(itemStack, enchantment.getKey(), level);
            }
        } else {
            throw new Error("Unknown enchantment provider: " + enchantment.provider);
        }
        return itemStack;
    }

    public static ItemStack remove(ItemStack itemStack, VEnchantmentType enchantment) {
        if (!isForThisMcVersion(enchantment)) {
            throw new IllegalArgumentException("Enchantment " + enchantment + " is not supported by this MC version: " + KciNms.mcVersion);
        }
        if (enchantment.provider == null) {
            itemStack.removeEnchantment(vanillaEnchantment(enchantment));
        } else if (enchantment.provider == CustomEnchantmentProvider.CRAZY_ENCHANTMENTS) {
            if (CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions(true) != null) {
                return CrazyEnchantmentsSupport.getCrazyEnchantmentsFunctions(true).remove(itemStack, enchantment.getKey());
            }
        } else {
            throw new Error("Unknown enchantment provider: " + enchantment.provider);
        }
        return itemStack;
    }
}
