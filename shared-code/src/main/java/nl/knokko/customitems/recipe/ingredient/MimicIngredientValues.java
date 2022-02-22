package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class MimicIngredientValues extends IngredientValues {

    public static MimicIngredientValues load(BitInput input) throws UnknownEncodingException {
        byte internalEncoding = input.readByte();
        if (internalEncoding != 1) throw new UnknownEncodingException("MimicIngredient", internalEncoding);

        MimicIngredientValues result = new MimicIngredientValues(false);
        result.itemId = input.readString();
        result.amount = input.readInt();
        return result;
    }

    public static MimicIngredientValues createQuick(String itemId, int amount, ResultValues remainingItem) {
        MimicIngredientValues result = new MimicIngredientValues(true);
        result.setItemId(itemId);
        result.setAmount(amount);
        result.setRemainingItem(remainingItem);
        return result;
    }

    private String itemId;
    private int amount;

    MimicIngredientValues(boolean mutable) {
        super(mutable);
        this.itemId = "minecraft:glass";
        this.amount = 2;
    }

    MimicIngredientValues(MimicIngredientValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.itemId = toCopy.getItemId();
        this.amount = toCopy.getAmount();
    }

    @Override
    public String toString(String emptyString) {
        return "Mimic(" + this.itemId + ")" + amountToString((byte) this.amount) + remainingToString();
    }

    @Override
    public byte getAmount() throws UnsupportedOperationException {
        return (byte) amount;
    }

    @Override
    public MimicIngredientValues copy(boolean mutable) {
        return new MimicIngredientValues(this, mutable);
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        return other instanceof MimicIngredientValues && this.itemId.equals(((MimicIngredientValues) other).itemId);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MimicIngredientValues) {
            MimicIngredientValues otherMimic = (MimicIngredientValues) other;
            return this.itemId.equals(otherMimic.itemId) && this.amount == otherMimic.amount
                    && Objects.equals(this.remainingItem, otherMimic.remainingItem);
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
        output.addByte(RecipeEncoding.Ingredient.MIMIC);
        output.addByte((byte) 1);
        output.addString(this.itemId);
        output.addInt(this.amount);
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
