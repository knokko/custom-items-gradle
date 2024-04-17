package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.CUSTOM_NEW;

public class CustomItemIngredient extends KciIngredient {

    static CustomItemIngredient load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        CustomItemIngredient ingredient = new CustomItemIngredient(false);

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

    public static CustomItemIngredient createQuick(ItemReference item, int amount) {
        return createQuick(item, amount, null, new IngredientConstraints(true));
    }

    public static CustomItemIngredient createQuick(
            ItemReference item, int amount, KciResult remainingItem, IngredientConstraints constraints
    ) {
        CustomItemIngredient result = new CustomItemIngredient(true);
        result.setItem(item);
        result.setAmount((byte) amount);
        result.setRemainingItem(remainingItem);
        result.setConstraints(constraints);
        return result;
    }

    private byte amount;
    private ItemReference item;

    public CustomItemIngredient(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.item = null;
    }

    @Override
    public String toString(String emptyString) {
        return item.get().getName() + amountToString(amount) + remainingToString();
    }

    public CustomItemIngredient(CustomItemIngredient toCopy, boolean mutable) {
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
        this.constraints = IngredientConstraints.load(input);
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
    public boolean conflictsWith(KciIngredient other) {
        if (other instanceof CustomItemIngredient) {
            return this.item.equals(((CustomItemIngredient) other).item);
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
        if (other instanceof CustomItemIngredient) {
            CustomItemIngredient otherIngredient = (CustomItemIngredient) other;
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
    public CustomItemIngredient copy(boolean mutable) {
        return new CustomItemIngredient(this, mutable);
    }

    @Override
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
