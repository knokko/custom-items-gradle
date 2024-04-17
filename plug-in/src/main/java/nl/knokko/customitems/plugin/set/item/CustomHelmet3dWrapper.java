package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.KciArmor;
import org.bukkit.inventory.ItemStack;

public class CustomHelmet3dWrapper extends CustomArmorWrapper {
    CustomHelmet3dWrapper(KciArmor item) {
        super(item);
    }

    // Don't let custom helmets till dirt because it is a hoe internally
    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }
}
