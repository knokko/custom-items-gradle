package nl.knokko.customitems.plugin;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class CustomItemsApi {

    public static Collection<String> getAllItemNames() {
        SItemSet itemSet = CustomItemsPlugin.getInstance().getSet().get();

        Collection<String> itemNames = new ArrayList<>(itemSet.getItems().size());
        for (CustomItemValues item : itemSet.getItems()) {
            itemNames.add(item.getName());
        }

        return itemNames;
    }

    public static ItemStack createItemStack(String itemName, int amount) {
        ItemSetWrapper wrapper = CustomItemsPlugin.getInstance().getSet();

        CustomItemValues item = wrapper.getItem(itemName);
        if (item != null) return wrap(item).create(amount);
        else return null;
    }

    public static String getItemName(ItemStack itemStack) {
        CustomItemValues item = CustomItemsPlugin.getInstance().getSet().getItem(itemStack);

        if (item != null) return item.getName();
        else return null;
    }

    public static boolean hasItem(String itemName) {
        return CustomItemsPlugin.getInstance().getSet().getItem(itemName) != null;
    }
}
