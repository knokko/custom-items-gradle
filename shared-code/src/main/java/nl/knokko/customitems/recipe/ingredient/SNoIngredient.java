package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.util.bits.BitOutput;

public class SNoIngredient extends SIngredient {

    SNoIngredient(boolean mutable) {
        super(mutable);
    }

    SNoIngredient(SNoIngredient toCopy, boolean mutable) {
        super(toCopy, mutable);
    }

    @Override
    public SNoIngredient copy(boolean mutable) {
        return new SNoIngredient(this, mutable);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.NONE);
    }
}
