package nl.knokko.customitems.nms;

import org.bukkit.inventory.ItemStack;

public interface GeneralItemNBT {

    ItemStack backToBukkit();

    String getOrDefault(String[] key, String defaultValue);

    int getOrDefault(String[] key, int defaultValue);

    void set(String[] key, String value);

    void set(String[] key, int value);

    void remove(String[] key);
}
