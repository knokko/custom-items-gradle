package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class CopiedIngredient extends KciIngredient {

    public static CopiedIngredient createQuick(
            int amount, String encoded, KciResult remainingItem,
            IngredientConstraints constraints
    ) {
        CopiedIngredient ingredient = new CopiedIngredient(true);
        ingredient.setAmount(amount);
        ingredient.setEncoded(encoded);
        ingredient.setRemainingItem(remainingItem);
        ingredient.setConstraints(constraints);
        return ingredient.copy(false);
    }

    public static CopiedIngredient createQuick(int amount, String encoded) {
        return createQuick(amount, encoded, null, new IngredientConstraints(false));
    }

    static CopiedIngredient load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CopiedIngredient", encoding);

        return createQuick(input.readByte(), input.readString());
    }

    private String encoded;
    private byte amount;

    CopiedIngredient(boolean mutable) {
        super(mutable);
        this.encoded = null;
        this.amount = 1;
    }

    CopiedIngredient(CopiedIngredient toCopy, boolean mutable) {
        super(mutable);
        this.encoded = toCopy.getEncodedItem();
        this.amount = toCopy.getAmount();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CopiedIngredient) {
            CopiedIngredient copied = (CopiedIngredient) other;
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
    public byte getAmount() {
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
    public CopiedIngredient copy(boolean mutable) {
        return new CopiedIngredient(this, mutable);
    }

    @Override
    public boolean conflictsWith(KciIngredient other) {
        return other instanceof CopiedIngredient && this.encoded.equals(((CopiedIngredient) other).encoded);
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

    @Override
    public VMaterial getVMaterial(int mcVersion) {
        // Return *null* because we don't know the Material without Bukkit help
        return null;
    }
}
