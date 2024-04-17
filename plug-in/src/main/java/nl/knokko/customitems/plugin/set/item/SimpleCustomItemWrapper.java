package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciItem;
import org.bukkit.inventory.ItemStack;

public class SimpleCustomItemWrapper extends CustomItemWrapper {
    SimpleCustomItemWrapper(KciItem item) {
        super(item);
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return this.item.getItemType() != KciItemType.OTHER;
    }
}
