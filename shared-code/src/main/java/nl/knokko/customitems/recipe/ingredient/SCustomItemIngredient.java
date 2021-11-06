package nl.knokko.customitems.recipe.ingredient;

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

public class SCustomItemIngredient extends IngredientValues {

    static SCustomItemIngredient load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        SCustomItemIngredient ingredient = new SCustomItemIngredient(false);

        if (encoding == RecipeEncoding.Ingredient.CUSTOM) {
            ingredient.load1(input, itemSet);
        } else if (encoding == RecipeEncoding.Ingredient.CUSTOM_2) {
            ingredient.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomItemIngredient", encoding);
        }

        return ingredient;
    }

    private byte amount;
    private ItemReference item;

    public SCustomItemIngredient(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.item = null;
    }

    public SCustomItemIngredient(SCustomItemIngredient toCopy, boolean mutable) {
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
        if (other instanceof SCustomItemIngredient) {
            return this.item.equals(((SCustomItemIngredient) other).item);
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
    public SCustomItemIngredient copy(boolean mutable) {
        return new SCustomItemIngredient(this, mutable);
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
