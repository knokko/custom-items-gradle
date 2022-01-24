package nl.knokko.customitems.plugin.multisupport.itembridge;

import com.jojodmo.itembridge.ItemBridge;
import com.jojodmo.itembridge.ItemBridgeListener;
import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.inventory.ItemStack;

public class KciItemBridgeListener implements ItemBridgeListener {

    static void setup(CustomItemsPlugin plugin) {
        new ItemBridge(plugin, "KnokkosCustomItems", "kci").registerListener(new KciItemBridgeListener());
    }

    @Override
    public ItemStack fetchItemStack(String item) {
        return CustomItemsApi.createItemStack(item, 1);
    }

    @Override
    public String getItemName(ItemStack itemStack) {
        return CustomItemsApi.getItemName(itemStack);
    }
}
