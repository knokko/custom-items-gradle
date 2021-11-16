package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomDisplayItemValues extends SlotDisplayItemValues {

    static CustomDisplayItemValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        CustomDisplayItemValues result = new CustomDisplayItemValues(false);

        if (encoding == Encodings.CUSTOM1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomDisplayItem", encoding);
        }

        return result;
    }

    public static CustomDisplayItemValues createQuick(ItemReference customItem) {
        CustomDisplayItemValues result = new CustomDisplayItemValues(true);
        result.setItem(customItem);
        return result;
    }

    private ItemReference customItem;

    public CustomDisplayItemValues(boolean mutable) {
        super(mutable);
        this.customItem = null;
    }

    public CustomDisplayItemValues(CustomDisplayItemValues toCopy, boolean mutable) {
        super(mutable);
        this.customItem = toCopy.getItemReference();
    }

    private void load1(BitInput input, SItemSet itemSet) {
        this.customItem = itemSet.getItemReference(input.readString());
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.CUSTOM1);
        output.addString(customItem.get().getName());
    }

    @Override
    public CustomDisplayItemValues copy(boolean mutable) {
        return new CustomDisplayItemValues(this, mutable);
    }

    public ItemReference getItemReference() {
        return customItem;
    }

    public CustomItemValues getItem() {
        return customItem == null ? null : customItem.get();
    }

    public void setItem(ItemReference newItem) {
        assertMutable();
        Checks.notNull(newItem);
        this.customItem = newItem;
    }

    @Override
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (customItem == null) throw new ValidationException("You need to choose an item");
        if (!itemSet.isReferenceValid(customItem)) throw new ProgrammingValidationException("Item is no longer valid");
    }
}
