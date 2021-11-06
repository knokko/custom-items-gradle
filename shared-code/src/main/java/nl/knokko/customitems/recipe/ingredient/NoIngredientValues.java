package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitOutput;

public class NoIngredientValues extends IngredientValues {

    public NoIngredientValues() {
        super(false);
    }

    NoIngredientValues(NoIngredientValues toCopy) {
        super(toCopy, false);
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        return other instanceof NoIngredientValues;
    }

    @Override
    public NoIngredientValues copy(boolean mutable) {
        return new NoIngredientValues(this);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.NONE);
    }
}
