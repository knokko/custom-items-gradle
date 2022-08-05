package nl.knokko.customitems.item.equipment;

import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.util.Checks;

public class EquipmentEntry {

    public final AttributeModifierValues.Slot slot;
    public final ItemReference item;

    public EquipmentEntry(AttributeModifierValues.Slot slot, ItemReference item) {
        Checks.nonNull(slot, item);
        this.slot = slot;
        this.item = item;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EquipmentEntry) {
            EquipmentEntry otherEntry = (EquipmentEntry) other;
            return this.slot == otherEntry.slot && this.item.equals(otherEntry.item);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return slot.hashCode() + 31 * item.hashCode();
    }
}
