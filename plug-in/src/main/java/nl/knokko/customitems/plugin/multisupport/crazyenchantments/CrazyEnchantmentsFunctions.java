package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import org.bukkit.inventory.ItemStack;

public interface CrazyEnchantmentsFunctions {

    int getLevel(ItemStack itemStack, String enchantmentName);

    ItemStack add(ItemStack itemStack, String enchantmentName, int level);

    ItemStack remove(ItemStack itemStack, String enchantmentName);
}
