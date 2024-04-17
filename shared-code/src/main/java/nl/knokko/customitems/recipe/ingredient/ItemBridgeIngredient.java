package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class ItemBridgeIngredient extends KciIngredient {

    public static ItemBridgeIngredient load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte internalEncoding = input.readByte();
        if (internalEncoding != 1 && internalEncoding != 2) throw new UnknownEncodingException("ItemBridgeIngredient", internalEncoding);

        ItemBridgeIngredient result = new ItemBridgeIngredient(false);
        result.itemId = input.readString();
        result.amount = input.readInt();
        result.loadRemainingItem(input, itemSet);
        if (internalEncoding > 1) result.constraints = IngredientConstraints.load(input);
        return result;
    }

    public static ItemBridgeIngredient createQuick(
            String itemId, int amount, KciResult remainingItem, IngredientConstraints constraints
    ) {
        ItemBridgeIngredient result = new ItemBridgeIngredient(true);
        result.setItemId(itemId);
        result.setAmount(amount);
        result.setRemainingItem(remainingItem);
        result.setConstraints(constraints);
        return result;
    }

    private String itemId;
    private int amount;

    ItemBridgeIngredient(boolean mutable) {
        super(mutable);
        this.itemId = "minecraft:glass";
        this.amount = 2;
    }

    ItemBridgeIngredient(ItemBridgeIngredient toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.itemId = toCopy.getItemId();
        this.amount = toCopy.getAmount();
    }

    @Override
    public String toString() {
        return toString(null);
    }

    @Override
    public String toString(String emptyString) {
        return "ItemBridge(" + this.itemId + ")" + amountToString((byte) this.amount) + remainingToString();
    }

    @Override
    public byte getAmount() throws UnsupportedOperationException {
        return (byte) amount;
    }

    @Override
    public ItemBridgeIngredient copy(boolean mutable) {
        return new ItemBridgeIngredient(this, mutable);
    }

    @Override
    public boolean conflictsWith(KciIngredient other) {
        return other instanceof ItemBridgeIngredient && this.itemId.equals(((ItemBridgeIngredient) other).itemId);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ItemBridgeIngredient) {
            ItemBridgeIngredient otherIngredient = (ItemBridgeIngredient) other;
            return this.itemId.equals(otherIngredient.itemId) && this.amount == otherIngredient.amount
                    && Objects.equals(this.remainingItem, otherIngredient.remainingItem)
                    && this.constraints.equals(otherIngredient.constraints);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.itemId.hashCode() + this.amount + Objects.hashCode(this.remainingItem);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.ITEM_BRIDGE);
        output.addByte((byte) 2);
        output.addString(this.itemId);
        output.addInt(this.amount);
        saveRemainingItem(output);
        constraints.save(output);
    }

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String newItemId) {
        Checks.notNull(newItemId);
        assertMutable();
        this.itemId = newItemId;
    }

    public void setAmount(int newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (this.itemId == null) throw new ProgrammingValidationException("No item id");
        if (!this.itemId.contains(":")) throw new ValidationException("Item id must contain ':'");
        if (this.amount < 1) throw new ValidationException("Amount must be positive");
        if (this.amount > 64) throw new ValidationException("Amount can be at most 64");
    }
}
