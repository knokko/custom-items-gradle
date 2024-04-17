package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.KciItem;
import org.bukkit.inventory.ItemStack;

public class CustomMusicDiscWrapper extends CustomItemWrapper {

    CustomMusicDiscWrapper(KciItem item) {
        super(item);
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return false;
    }
}
