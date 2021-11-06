package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitOutput;

public class SNoIngredient extends IngredientValues {

    public SNoIngredient() {
        super(false);
    }

    SNoIngredient(SNoIngredient toCopy) {
        super(toCopy, false);
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        return other instanceof SNoIngredient;
    }

    @Override
    public SNoIngredient copy(boolean mutable) {
        return new SNoIngredient(this);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.NONE);
    }
}
