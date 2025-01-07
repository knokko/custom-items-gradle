package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.KciArmor;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.texture.ArmorTexture;
import org.bukkit.Color;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

import static nl.knokko.customitems.MCVersions.VERSION1_21;

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

    public static void setItemMetaArmor(KciArmor armor, ItemMeta meta) {
        ArmorTexture armorTexture = armor.getArmorTexture();
        if (armorTexture != null && KciNms.mcVersion >= VERSION1_21) {
            EquipmentSlot slot = EquipmentSlot.HEAD;
            KciItemType.Category category = armor.getItemType().getMainCategory();
            if (category == KciItemType.Category.CHESTPLATE || category == KciItemType.Category.ELYTRA) slot = EquipmentSlot.CHEST;
            if (category == KciItemType.Category.LEGGINGS) slot = EquipmentSlot.LEGS;
            if (category == KciItemType.Category.BOOTS) slot = EquipmentSlot.FEET;
            KciNms.instance.items.setEquippableAssetID(meta, slot, "kci_" + armorTexture.getName());
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
        setItemMetaArmor(this.armor, meta);
        return meta;
    }
}
