package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Objects;

public class CustomItemIngredientValues extends IngredientValues {

    static CustomItemIngredientValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        CustomItemIngredientValues ingredient = new CustomItemIngredientValues(false);

        if (encoding == RecipeEncoding.Ingredient.CUSTOM) {
            ingredient.load1(input, itemSet);
        } else if (encoding == RecipeEncoding.Ingredient.CUSTOM_2) {
            ingredient.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomItemIngredient", encoding);
        }

        return ingredient;
    }

    public static CustomItemIngredientValues createQuick(ItemReference item, int amount, ResultValues remainingItem) {
        CustomItemIngredientValues result = new CustomItemIngredientValues(true);
        result.setItem(item);
        result.setAmount((byte) amount);
        result.setRemainingItem(remainingItem);
        return result;
    }

    private byte amount;
    private ItemReference item;

    public CustomItemIngredientValues(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.item = null;
    }

    @Override
    public String toString(String emptyString) {
        return item.get().getName() + amountToString(amount) + remainingToString();
    }

    public CustomItemIngredientValues(CustomItemIngredientValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.amount = toCopy.getAmount();
        this.item = toCopy.getItemReference();
    }

    private void load1(BitInput input, SItemSet itemSet) {
        this.amount = 1;
        this.remainingItem = null;
        this.item = itemSet.getItemReference(input.readJavaString());
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.amount = input.readByte();
        loadRemainingItem(input, itemSet);
        this.item = itemSet.getItemReference(input.readJavaString());
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.CUSTOM_2);
        save2(output);
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        if (other instanceof CustomItemIngredientValues) {
            return this.item.equals(((CustomItemIngredientValues) other).item);
        } else {
            return false;
        }
    }

    private void save2(BitOutput output) {
        output.addByte(amount);
        saveRemainingItem(output);
        output.addJavaString(item.get().getName());
    }

    @Override
    public String toString() {
        return toString(null);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomItemIngredientValues) {
            CustomItemIngredientValues otherIngredient = (CustomItemIngredientValues) other;
            return this.item.equals(otherIngredient.item) && this.amount == otherIngredient.amount
                    && Objects.equals(this.remainingItem, otherIngredient.remainingItem);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return amount + 71 * item.get().getName().hashCode() + 975 * Objects.hashCode(remainingItem);
    }

    @Override
    public CustomItemIngredientValues copy(boolean mutable) {
        return new CustomItemIngredientValues(this, mutable);
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
        super.validateIndependent();

        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        if (item == null) throw new ValidationException("No item has been selected");
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet);

        if (!itemSet.isReferenceValid(item)) throw new ValidationException("Item is not or no longer valid");
    }
}
