package com.badbones69.crazyenchantments.api;

import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class CrazyManager {

    public static CrazyManager getInstance() {
        return new CrazyManager();
    }

    public int getLevel(ItemStack itemStack, CEnchantment enchantment) {
        return 1;
    }

    public ItemStack addEnchantment(ItemStack itemStack, CEnchantment enchantment, int level) {
        return itemStack;
    }

    public ItemStack removeEnchantment(ItemStack itemStack, CEnchantment enchantment) {
        return itemStack;
    }
}
