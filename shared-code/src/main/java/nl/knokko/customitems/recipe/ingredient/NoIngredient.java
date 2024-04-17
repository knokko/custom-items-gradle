package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

public class NoIngredient extends KciIngredient {

    public NoIngredient() {
        super(false);
    }

    NoIngredient(NoIngredient toCopy) {
        super(toCopy, false);
    }

    @Override
    public boolean conflictsWith(KciIngredient other) {
        return other instanceof NoIngredient;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof NoIngredient) {
            return Objects.equals(this.remainingItem, ((NoIngredient) other).remainingItem);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.remainingItem);
    }

    @Override
    public String toString(String emptyString) {
        return emptyString;
    }

    @Override
    public byte getAmount() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NoIngredient copy(boolean mutable) {
        return new NoIngredient(this);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.NONE);
    }
}
