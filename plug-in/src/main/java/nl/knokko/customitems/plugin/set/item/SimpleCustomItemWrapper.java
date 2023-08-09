package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemValues;
import org.bukkit.inventory.ItemStack;

public class SimpleCustomItemWrapper extends CustomItemWrapper {
    SimpleCustomItemWrapper(CustomItemValues item) {
        super(item);
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return this.item.getItemType() != CustomItemType.OTHER;
    }
}
