package nl.knokko.customitems.item.equipment;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EquipmentSetValues extends ModelValues {

    public static EquipmentSetValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EquipmentSet", encoding);
        EquipmentSetValues result = new EquipmentSetValues(false);

        int numEntries = input.readInt();
        for (int counter = 0; counter < numEntries; counter++) {
            result.loadEntry(input, itemSet);
        }

        int numBonuses = input.readInt();
        result.bonuses = new ArrayList<>(numBonuses);
        for (int counter = 0; counter < numBonuses; counter++) {
            result.bonuses.add(EquipmentBonusValues.load(input, itemSet));
        }

        return result;
    }

    private final Map<EquipmentEntry, Integer> entries;
    private Collection<EquipmentBonusValues> bonuses;

    public EquipmentSetValues(boolean mutable) {
        super(mutable);
        this.entries = new HashMap<>();
        this.bonuses = new ArrayList<>();
    }

    public EquipmentSetValues(EquipmentSetValues toCopy, boolean mutable) {
        super(mutable);
        this.entries = toCopy.getEntries();
        this.bonuses = toCopy.getBonuses();
    }

    public Map<EquipmentEntry, Integer> getEntries() {
        return new HashMap<>(entries);
    }

    public Integer getEntryValue(EquipmentEntry entry) {
        return entries.get(entry);
    }

    public Collection<EquipmentBonusValues> getBonuses() {
        return new ArrayList<>(bonuses);
    }

    public void setEntryValue(EquipmentEntry entry, int value) {
        assertMutable();
        entries.put(entry, value);
    }

    public void removeEntry(EquipmentEntry entry) {
        assertMutable();
        entries.remove(entry);
    }

    public void setBonuses(Collection<EquipmentBonusValues> bonuses) {
        assertMutable();
        Checks.nonNull(bonuses);
        this.bonuses = Mutability.createDeepCopy(bonuses, false);
    }

    @Override
    public EquipmentSetValues copy(boolean mutable) {
        return new EquipmentSetValues(this, mutable);
    }

    @Override
    public String toString() {
        if (entries.isEmpty()) return "No items";
        return entries.keySet().iterator().next().item.get().getName() + " + " + (entries.size() - 1) + " more";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EquipmentSetValues) {
            EquipmentSetValues otherSet = (EquipmentSetValues) other;
            return this.entries.equals(otherSet.entries) && this.bonuses.equals(otherSet.bonuses);
        } else {
            return false;
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(entries.size());
        for (Map.Entry<EquipmentEntry, Integer> entry : entries.entrySet()) {
            saveEntry(output, entry.getKey(), entry.getValue());
        }

        output.addInt(bonuses.size());
        for (EquipmentBonusValues bonus : bonuses) {
            bonus.save(output);
        }
    }

    private void saveEntry(BitOutput output, EquipmentEntry key, int value) {
        output.addByte((byte) 1);

        output.addString(key.slot.name());
        output.addString(key.item.get().getName());
        output.addInt(value);
    }

    private void loadEntry(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EquipmentSet.Entry", encoding);

        this.entries.put(new EquipmentEntry(
                AttributeModifierValues.Slot.valueOf(input.readString()),
                itemSet.items.getReference(input.readString())
        ), input.readInt());
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (entries == null) throw new ProgrammingValidationException("No entries");
        for (Map.Entry<EquipmentEntry, Integer> entry : entries.entrySet()) {
            if (entry.getKey() == null) throw new ProgrammingValidationException("Missing an equipment entry");
            if (entry.getValue() == null) throw new ProgrammingValidationException("Missing an equipment entry value");
            if (!itemSet.items.isValid(entry.getKey().item)) {
                throw new ProgrammingValidationException("Item is no longer valid");
            }
        }

        if (bonuses == null) throw new ProgrammingValidationException("No bonuses");
        for (EquipmentBonusValues bonus : bonuses) {
            if (bonus == null) throw new ProgrammingValidationException("Missing a bonus");
            Validation.scope("Bonus", bonus::validate);
        }
    }

    public void validateExportVersion(int mcVersion) {
        // Currently, nothing here is tried to any specific minecraft version
    }
}
