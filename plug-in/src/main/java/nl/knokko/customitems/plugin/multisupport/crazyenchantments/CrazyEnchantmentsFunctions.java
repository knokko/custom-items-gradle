package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import org.bukkit.inventory.ItemStack;

public interface CrazyEnchantmentsFunctions {

    int getLevel(ItemStack itemStack, String enchantmentName);

    void add(ItemStack itemStack, String enchantmentName, int level);

    void remove(ItemStack itemStack, String enchantmentName);
}
