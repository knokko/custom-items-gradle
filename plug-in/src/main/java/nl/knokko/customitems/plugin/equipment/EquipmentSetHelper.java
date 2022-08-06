package nl.knokko.customitems.plugin.equipment;

import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.equipment.EquipmentBonusValues;
import nl.knokko.customitems.item.equipment.EquipmentEntry;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EquipmentSetHelper {

    private static Collection<EquipmentEntry> getEquipmentEntries(EntityEquipment equipment, ItemSetWrapper itemSet) {
        ItemReference customHelmet = itemSet.getItemReference(equipment.getHelmet());
        ItemReference customChestplate = itemSet.getItemReference(equipment.getChestplate());
        ItemReference customLeggings = itemSet.getItemReference(equipment.getLeggings());
        ItemReference customBoots = itemSet.getItemReference(equipment.getBoots());

        ItemReference customMainItem = itemSet.getItemReference(equipment.getItemInMainHand());
        ItemReference customOffItem = itemSet.getItemReference(equipment.getItemInOffHand());

        Collection<EquipmentEntry> entries = new ArrayList<>(6);
        if (customHelmet != null) entries.add(new EquipmentEntry(AttributeModifierValues.Slot.HEAD, customHelmet));
        if (customChestplate != null) entries.add(new EquipmentEntry(AttributeModifierValues.Slot.CHEST, customChestplate));
        if (customLeggings != null) entries.add(new EquipmentEntry(AttributeModifierValues.Slot.LEGS, customLeggings));
        if (customBoots != null) entries.add(new EquipmentEntry(AttributeModifierValues.Slot.FEET, customBoots));

        if (customMainItem != null) entries.add(new EquipmentEntry(AttributeModifierValues.Slot.MAINHAND, customMainItem));
        if (customOffItem != null) entries.add(new EquipmentEntry(AttributeModifierValues.Slot.OFFHAND, customOffItem));

        return entries;
    }

    public static Collection<EquipmentBonusValues> getEquipmentBonuses(EntityEquipment equipment, ItemSetWrapper itemSet) {
        Collection<EquipmentEntry> equippedItems = getEquipmentEntries(equipment, itemSet);

        // Performance improvement: return early if not a single custom items is equipped
        if (equippedItems.isEmpty()) return Collections.emptyList();

        Collection<EquipmentBonusValues> bonuses = new ArrayList<>();
        for (EquipmentSetValues equipmentSet : itemSet.get().getEquipmentSets()) {
            int value = 0;
            for (EquipmentEntry equippedItem : equippedItems) {
                Integer itemValue = equipmentSet.getEntryValue(equippedItem);
                if (itemValue != null) value += itemValue;
            }

            // Performance improvement: skip iterator bonuses if the value is 0
            // This will happen if not a single piece is equipped, which is a very common case
            if (value == 0) continue;

            for (EquipmentBonusValues bonus : equipmentSet.getBonuses()) {
                if (value >= bonus.getMinValue() && value <= bonus.getMaxValue()) {
                    bonuses.add(bonus);
                }
            }
        }

        return bonuses;
    }
}
