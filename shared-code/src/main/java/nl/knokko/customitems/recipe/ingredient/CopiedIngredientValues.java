package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraintsValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class CopiedIngredientValues extends IngredientValues {

    public static CopiedIngredientValues createQuick(
            int amount, String encoded, ResultValues remainingItem,
            IngredientConstraintsValues constraints
    ) {
        CopiedIngredientValues ingredient = new CopiedIngredientValues(true);
        ingredient.setAmount(amount);
        ingredient.setEncoded(encoded);
        ingredient.setRemainingItem(remainingItem);
        ingredient.setConstraints(constraints);
        return ingredient.copy(false);
    }

    public static CopiedIngredientValues createQuick(int amount, String encoded) {
        return createQuick(amount, encoded, null, new IngredientConstraintsValues(false));
    }

    static CopiedIngredientValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CopiedIngredient", encoding);

        return createQuick(input.readByte(), input.readString());
    }

    private String encoded;
    private byte amount;

    CopiedIngredientValues(boolean mutable) {
        super(mutable);
        this.encoded = null;
        this.amount = 1;
    }

    CopiedIngredientValues(CopiedIngredientValues toCopy, boolean mutable) {
        super(mutable);
        this.encoded = toCopy.getEncodedItem();
        this.amount = toCopy.getAmount();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CopiedIngredientValues) {
            CopiedIngredientValues copied = (CopiedIngredientValues) other;
            return this.amount == copied.amount && this.encoded.equals(copied.encoded);
        } else return false;
    }

    @Override
    public int hashCode() {
        return amount + Objects.hashCode(encoded);
    }

    @Override
    public String toString(String emptyString) {
        return amount + " copied item(s)";
    }

    @Override
    public String toString() {
        return toString(null);
    }

    @Override
    public byte getAmount() throws UnsupportedOperationException {
        return amount;
    }

    public String getEncodedItem() {
        return encoded;
    }

    public void setAmount(int newAmount) {
        assertMutable();
        this.amount = (byte) newAmount;
    }

    public void setEncoded(String encoded) {
        assertMutable();
        this.encoded = Objects.requireNonNull(encoded);
    }

    @Override
    public CopiedIngredientValues copy(boolean mutable) {
        return new CopiedIngredientValues(this, mutable);
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        return other instanceof CopiedIngredientValues && this.encoded.equals(((CopiedIngredientValues) other).encoded);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.COPIED);
        output.addByte((byte) 1);

        output.addByte(amount);
        output.addString(encoded);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (amount < 1) throw new ValidationException("Amount must be a positive integer");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        if (encoded == null) throw new ValidationException("You still need to paste the copied text");
        try {
            StringEncoder.decode(encoded);
        } catch (IllegalArgumentException encodedIsInvalid) {
            throw new ValidationException("The copied text is invalid");
        }
    }
}
