package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraintsValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.CUSTOM_NEW;

public class CustomItemIngredientValues extends IngredientValues {

    static CustomItemIngredientValues load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        CustomItemIngredientValues ingredient = new CustomItemIngredientValues(false);

        if (encoding == RecipeEncoding.Ingredient.CUSTOM) {
            ingredient.load1(input, itemSet);
        } else if (encoding == RecipeEncoding.Ingredient.CUSTOM_2) {
            ingredient.load2(input, itemSet);
        } else if (encoding == CUSTOM_NEW) {
            ingredient.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomItemIngredient", encoding);
        }

        return ingredient;
    }

    public static CustomItemIngredientValues createQuick(ItemReference item, int amount) {
        return createQuick(item, amount, null, new IngredientConstraintsValues(true));
    }

    public static CustomItemIngredientValues createQuick(
            ItemReference item, int amount, ResultValues remainingItem, IngredientConstraintsValues constraints
    ) {
        CustomItemIngredientValues result = new CustomItemIngredientValues(true);
        result.setItem(item);
        result.setAmount((byte) amount);
        result.setRemainingItem(remainingItem);
        result.setConstraints(constraints);
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

    private void load1(BitInput input, ItemSet itemSet) {
        this.amount = 1;
        this.remainingItem = null;
        this.item = itemSet.items.getReference(input.readJavaString());
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.amount = input.readByte();
        loadRemainingItem(input, itemSet);
        this.item = itemSet.items.getReference(input.readJavaString());
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("InternalCustomIngredient", encoding);

        this.amount = input.readByte();
        this.item = itemSet.items.getReference(input.readString());
        loadRemainingItem(input, itemSet);
        this.constraints = IngredientConstraintsValues.load(input);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.CUSTOM_NEW);
        output.addByte((byte) 1);

        output.addByte(amount);
        output.addString(item.get().getName());
        saveRemainingItem(output);
        constraints.save(output);
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        if (other instanceof CustomItemIngredientValues) {
            return this.item.equals(((CustomItemIngredientValues) other).item);
        } else {
            return false;
        }
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
                    && Objects.equals(this.remainingItem, otherIngredient.remainingItem)
                    && this.constraints.equals(otherIngredient.constraints);
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

    @Override
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
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet);

        if (!itemSet.items.isValid(item)) throw new ValidationException("Item is not or no longer valid");
    }
}
