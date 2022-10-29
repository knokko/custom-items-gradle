package com.badbones69.crazyenchantments.api.objects;

public class CEnchantment {

    @SuppressWarnings("unused")
    final String name;

    private CEnchantment(String name) {
        this.name = name;
    }

    public static CEnchantment getCEnchantmentFromName(String enchantment) {
        return new CEnchantment(enchantment);
    }
}
