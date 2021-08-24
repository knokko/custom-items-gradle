package nl.knokko.customitems.item;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class CustomItemValues extends ModelValues {

    protected CustomItemType itemType;
    protected short itemDamage;

    public CustomItemValues(boolean mutable) {
        super(mutable);
    }

    public CustomItemValues(CustomItemValues toCopy, boolean mutable) {
        super(mutable);

        copyProperties(toCopy);
    }

    protected void copyProperties(CustomItemValues source) {
        this.itemType = source.getItemType();
        this.itemDamage = source.getItemDamage();
    }

    protected void load1(BitInput input) {
        this.itemType = CustomItemType.valueOf(input.readJavaString());
        this.itemDamage = input.readShort();
    }

    protected void save1(BitOutput output) {
        output.addJavaString(itemType.name());
        output.addShort(itemDamage);
    }

    public CustomItemType getItemType() {
        return itemType;
    }

    public short getItemDamage() {
        return itemDamage;
    }

    public void setItemType(CustomItemType newItemType) {
        assertMutable();
        this.itemType = newItemType;
    }

    public void setItemDamage(short newItemDamage) {
        assertMutable();
        this.itemDamage = newItemDamage;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (itemDamage < 0) throw new ValidationException("Internal item damage is negative");
    }

    public void validateComplete() throws ValidationException, ProgrammingValidationException {
        validateIndependent();
    }
}
