package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.CustomArmorValues;
import nl.knokko.customitems.item.CustomToolValues;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class CustomArmorWrapper extends CustomToolWrapper {

    private final CustomArmorValues armor;

    CustomArmorWrapper(CustomArmorValues item) {
        super(item);
        this.armor = item;
    }

    @Override
    public ItemMeta createItemMeta(ItemStack item, List<String> lore) {
        ItemMeta meta = super.createItemMeta(item, lore);
        if (this.armor.getItemType().isLeatherArmor()) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(this.armor.getRed(), this.armor.getGreen(), this.armor.getBlue()));
        }
        return meta;
    }
}
