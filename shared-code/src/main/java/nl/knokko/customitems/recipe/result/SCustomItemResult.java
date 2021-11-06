package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SCustomItemResult extends ResultValues {

    static SCustomItemResult load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        SCustomItemResult result = new SCustomItemResult(false);

        if (encoding == RecipeEncoding.Result.CUSTOM) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomItemResult", encoding);
        }

        return result;
    }

    private byte amount;
    private ItemReference item;

    public SCustomItemResult(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.item = null;
    }

    public SCustomItemResult(SCustomItemResult toCopy, boolean mutable) {
        super(mutable);

        this.amount = toCopy.getAmount();
        this.item = toCopy.getItemReference();
    }

    @Override
    public SCustomItemResult copy(boolean mutable) {
        return new SCustomItemResult(this, mutable);
    }

    private void load1(BitInput input, SItemSet itemSet) {
        this.amount = loadAmount(input);
        this.item = itemSet.getItemReference(input.readJavaString());
    }

    @Override
    public String toString() {
        String amountString = amount == 1 ? "" : (" x " + amount);
        return item.get().getName() + amountString;
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.CUSTOM);
        save1(output);
    }

    private void save1(BitOutput output) {
        saveAmount(output, this.amount);
        output.addJavaString(this.item.get().getName());
    }

    public byte getAmount() {
        return amount;
    }

    public CustomItemValues getItem() {
        return item.get();
    }

    public ItemReference getItemReference() {
        return item;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setItem(ItemReference newItem) {
        assertMutable();
        Checks.notNull(newItem);
        this.item = newItem;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (item == null) throw new ValidationException("You must choose an item");
        byte maxStacksize = item.get().getMaxStacksize();

        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > maxStacksize) throw new ValidationException("Amount can be at most " + maxStacksize);
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (!itemSet.isReferenceValid(item)) throw new ProgrammingValidationException("The item is not or no longer valid");
    }
}
