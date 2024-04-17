package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class CustomItemResult extends KciResult {

    static CustomItemResult load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        CustomItemResult result = new CustomItemResult(false);

        if (encoding == RecipeEncoding.Result.CUSTOM) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomItemResult", encoding);
        }

        return result;
    }

    public static CustomItemResult createQuick(ItemReference item, int amount) {
        CustomItemResult result = new CustomItemResult(true);
        result.setItem(item);
        result.setAmount((byte) amount);
        return result;
    }

    private byte amount;
    private ItemReference item;

    public CustomItemResult(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.item = null;
    }

    public CustomItemResult(CustomItemResult toCopy, boolean mutable) {
        super(mutable);

        this.amount = toCopy.getAmount();
        this.item = toCopy.getItemReference();
    }

    @Override
    public CustomItemResult copy(boolean mutable) {
        return new CustomItemResult(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == CustomItemResult.class) {
            CustomItemResult otherResult = (CustomItemResult) other;
            return this.amount == otherResult.amount && this.item.equals(otherResult.item);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return amount + 237 * item.get().getName().hashCode();
    }

    private void load1(BitInput input, ItemSet itemSet) {
        this.amount = loadAmount(input);
        this.item = itemSet.items.getReference(input.readJavaString());
    }

    @Override
    public String toString() {
        String amountString = amount == 1 ? "" : (" x " + amount);
        return item.get().getName() + amountString;
    }

    @Override
    public List<String> getInfo() {
        List<String> result = new ArrayList<>(2);
        result.add("Custom item:");
        result.add(item.get().getName() + " x " + amount);
        return result;
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

    public KciItem getItem() {
        return item.get();
    }

    public ItemReference getItemReference() {
        return item;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setAmount(int newAmount) {
        this.setAmount((byte) newAmount);
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
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (!itemSet.items.isValid(item)) throw new ProgrammingValidationException("The item is not or no longer valid");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        // Custom items results are allowed in any MC version
    }
}
