package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.result.SResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.*;

public abstract class SIngredient extends ModelValues {

    public static SIngredient load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == VANILLA_SIMPLE || encoding == VANILLA_SIMPLE_2) {
            return SSimpleVanillaIngredient.load(input, encoding, itemSet);
        } else if (encoding == VANILLA_DATA || encoding == VANILLA_DATA_2) {
            return SDataVanillaIngredient.load(input, encoding, itemSet);
        } else if (encoding == CUSTOM || encoding == CUSTOM_2) {
            return SCustomItemIngredient.load(input, encoding, itemSet);
        } else if (encoding == NONE) {
            return new SNoIngredient(false);
        } else {
            throw new UnknownEncodingException("Ingredient", encoding);
        }
    }

    protected SResult remainingItem;

    SIngredient(boolean mutable) {
        super(mutable);

        remainingItem = null;
    }

    SIngredient(SIngredient toCopy, boolean mutable) {
        super(mutable);

        this.remainingItem = toCopy.getRemainingItem();
    }

    protected void loadRemainingItem(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        if (input.readBoolean()) {
            this.remainingItem = SResult.load(input, itemSet);
        } else {
            this.remainingItem = null;
        }
    }

    public abstract void save(BitOutput output);

    protected void saveRemainingItem(BitOutput output) {
        output.addBoolean(remainingItem != null);
        if (remainingItem != null) {
            remainingItem.save(output);
        }
    }

    public SResult getRemainingItem() {
        return remainingItem;
    }

    public void setRemainingItem(SResult newRemainingItem) {
        assertMutable();
        if (newRemainingItem != null) {
            this.remainingItem = newRemainingItem.copy(false);
        } else {
            this.remainingItem = null;
        }
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (remainingItem != null) Validation.scope("Remaining item", remainingItem::validateIndependent);
    }

    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (remainingItem != null) {
            Validation.scope("Remaining item", () -> remainingItem.validateComplete(itemSet));
        }
    }
}
