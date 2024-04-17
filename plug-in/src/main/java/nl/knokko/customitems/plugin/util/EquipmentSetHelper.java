package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.equipment.EquipmentSetBonus;
import nl.knokko.customitems.item.equipment.EquipmentSetEntry;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.inventory.EntityEquipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EquipmentSetHelper {

    private static Collection<EquipmentSetEntry> getEquipmentEntries(EntityEquipment equipment, ItemSetWrapper itemSet) {
        ItemReference customHelmet = itemSet.getItemReference(equipment.getHelmet());
        ItemReference customChestplate = itemSet.getItemReference(equipment.getChestplate());
        ItemReference customLeggings = itemSet.getItemReference(equipment.getLeggings());
        ItemReference customBoots = itemSet.getItemReference(equipment.getBoots());

        ItemReference customMainItem = itemSet.getItemReference(equipment.getItemInMainHand());
        ItemReference customOffItem = itemSet.getItemReference(equipment.getItemInOffHand());

        Collection<EquipmentSetEntry> entries = new ArrayList<>(6);
        if (customHelmet != null) entries.add(new EquipmentSetEntry(KciAttributeModifier.Slot.HEAD, customHelmet));
        if (customChestplate != null) entries.add(new EquipmentSetEntry(KciAttributeModifier.Slot.CHEST, customChestplate));
        if (customLeggings != null) entries.add(new EquipmentSetEntry(KciAttributeModifier.Slot.LEGS, customLeggings));
        if (customBoots != null) entries.add(new EquipmentSetEntry(KciAttributeModifier.Slot.FEET, customBoots));

        if (customMainItem != null) entries.add(new EquipmentSetEntry(KciAttributeModifier.Slot.MAINHAND, customMainItem));
        if (customOffItem != null) entries.add(new EquipmentSetEntry(KciAttributeModifier.Slot.OFFHAND, customOffItem));

        return entries;
    }

    public static Collection<EquipmentSetBonus> getEquipmentBonuses(EntityEquipment equipment, ItemSetWrapper itemSet) {
        Collection<EquipmentSetEntry> equippedItems = getEquipmentEntries(equipment, itemSet);

        // Performance improvement: return early if not a single custom items is equipped
        if (equippedItems.isEmpty()) return Collections.emptyList();

        Collection<EquipmentSetBonus> bonuses = new ArrayList<>();
        for (EquipmentSet equipmentSet : itemSet.get().equipmentSets) {
            int value = 0;
            for (EquipmentSetEntry equippedItem : equippedItems) {
                Integer itemValue = equipmentSet.getEntryValue(equippedItem);
                if (itemValue != null) value += itemValue;
            }

            // Performance improvement: skip iterator bonuses if the value is 0
            // This will happen if not a single piece is equipped, which is a very common case
            if (value == 0) continue;

            for (EquipmentSetBonus bonus : equipmentSet.getBonuses()) {
                if (value >= bonus.getMinValue() && value <= bonus.getMaxValue()) {
                    bonuses.add(bonus);
                }
            }
        }

        return bonuses;
    }
}
