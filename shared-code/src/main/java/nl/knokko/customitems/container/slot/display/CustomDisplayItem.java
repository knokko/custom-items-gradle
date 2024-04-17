package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class CustomDisplayItem extends SlotDisplayItem {

    static CustomDisplayItem load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        CustomDisplayItem result = new CustomDisplayItem(false);

        if (encoding == Encodings.CUSTOM1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomDisplayItem", encoding);
        }

        return result;
    }

    public static CustomDisplayItem createQuick(ItemReference customItem) {
        CustomDisplayItem result = new CustomDisplayItem(true);
        result.setItem(customItem);
        return result;
    }

    private ItemReference customItem;

    public CustomDisplayItem(boolean mutable) {
        super(mutable);
        this.customItem = null;
    }

    public CustomDisplayItem(CustomDisplayItem toCopy, boolean mutable) {
        super(mutable);
        this.customItem = toCopy.getItemReference();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        this.customItem = itemSet.items.getReference(input.readString());
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.CUSTOM1);
        output.addString(customItem.get().getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomDisplayItem) {
            return this.customItem.equals(((CustomDisplayItem) other).customItem);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.customItem.get().getName();
    }

    @Override
    public CustomDisplayItem copy(boolean mutable) {
        return new CustomDisplayItem(this, mutable);
    }

    public ItemReference getItemReference() {
        return customItem;
    }

    public KciItem getItem() {
        return customItem == null ? null : customItem.get();
    }

    public void setItem(ItemReference newItem) {
        assertMutable();
        Checks.notNull(newItem);
        this.customItem = newItem;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (customItem == null) throw new ValidationException("You need to choose an item");
        if (!itemSet.items.isValid(customItem)) throw new ProgrammingValidationException("Item is no longer valid");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // Custom display items don't rely on the MC version
    }
}
