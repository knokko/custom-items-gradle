package nl.knokko.customitems.plugin.multisupport.itembridge;

import com.jojodmo.itembridge.ItemBridge;
import com.jojodmo.itembridge.ItemBridgeListener;
import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KciItemBridgeListener implements ItemBridgeListener {

    static boolean isBridgeItem(ItemStack candidate, String fullId) {
        int indexColon = fullId.indexOf(':');
        if (indexColon == -1) throw new IllegalArgumentException("fullId (" + fullId + ") should contain a colon");
        return ItemBridge.isItemStack(candidate, fullId.substring(0, indexColon), fullId.substring(indexColon + 1));
    }

    static ItemStack fetchItem(String id, int amount) {
        ItemStack item = ItemBridge.getItemStack(id);
        if (item == null) {
            Bukkit.getLogger().severe("Can't find ItemBridge item " + id);
            return new ItemStack(Material.AIR);
        }
        item.setAmount(amount);
        return item;
    }

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
