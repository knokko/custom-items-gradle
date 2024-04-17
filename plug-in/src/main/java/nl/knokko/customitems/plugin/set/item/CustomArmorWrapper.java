package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.KciArmor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class CustomArmorWrapper extends CustomToolWrapper {

    public static void colorItemMeta(KciArmor armor, ItemMeta meta) {
        if (armor.getItemType().isLeatherArmor()) {
            if (armor.getFancyPantsTexture() == null) {
                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(armor.getRed(), armor.getGreen(), armor.getBlue()));
            } else {
                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(armor.getFancyPantsTexture().getRgb()));
            }
        }
    }

    private final KciArmor armor;

    CustomArmorWrapper(KciArmor item) {
        super(item);
        this.armor = item;
    }

    @Override
    public ItemMeta createItemMeta(ItemStack item, List<String> lore) {
        ItemMeta meta = super.createItemMeta(item, lore);
        colorItemMeta(this.armor, meta);
        return meta;
    }
}
