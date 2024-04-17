package nl.knokko.customitems.block.miningspeed;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

import static nl.knokko.customitems.block.miningspeed.MiningSpeed.validateValue;

public class CustomMiningSpeedEntry extends ModelValues {

    static CustomMiningSpeedEntry load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomMiningSpeedEntry", encoding);

        CustomMiningSpeedEntry result = new CustomMiningSpeedEntry(false);
        result.value = input.readInt();
        result.item = itemSet.items.getReference(input.readString());
        return result;
    }

    private int value;
    private ItemReference item;

    public CustomMiningSpeedEntry(boolean mutable) {
        super(mutable);

        this.value = -1;
        this.item = null;
    }

    public CustomMiningSpeedEntry(CustomMiningSpeedEntry toCopy, boolean mutable) {
        super(mutable);

        this.value = toCopy.getValue();
        this.item = toCopy.getItemReference();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(value);
        output.addString(item.get().getName());
    }

    @Override
    public CustomMiningSpeedEntry copy(boolean mutable) {
        return new CustomMiningSpeedEntry(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomMiningSpeedEntry) {
            CustomMiningSpeedEntry otherEntry = (CustomMiningSpeedEntry) other;
            return this.value == otherEntry.value && Objects.equals(this.item, otherEntry.item);
        } else {
            return false;
        }
    }

    public int getValue() {
        return value;
    }

    public ItemReference getItemReference() {
        return item;
    }

    public KciItem getItem() {
        return item != null ? item.get() : null;
    }

    public void setValue(int value) {
        assertMutable();
        this.value = value;
    }

    public void setItemReference(ItemReference item) {
        assertMutable();
        Checks.notNull(item);
        this.item = item;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateValue(value);
        if (item == null) throw new ValidationException("You must choose a custom item");
        if (!itemSet.items.isValid(item)) throw new ProgrammingValidationException("Item is no longer valid");
    }
}
