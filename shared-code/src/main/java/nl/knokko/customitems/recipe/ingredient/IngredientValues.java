package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraintsValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.*;

public abstract class IngredientValues extends ModelValues {

    public static IngredientValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == VANILLA_SIMPLE || encoding == VANILLA_SIMPLE_2 || encoding == VANILLA_SIMPLE_NEW) {
            return SimpleVanillaIngredientValues.load(input, encoding, itemSet);
        } else if (encoding == VANILLA_DATA || encoding == VANILLA_DATA_2 || encoding == VANILLA_DATA_NEW) {
            return DataVanillaIngredientValues.load(input, encoding, itemSet);
        } else if (encoding == CUSTOM || encoding == CUSTOM_2 || encoding == CUSTOM_NEW) {
            return CustomItemIngredientValues.load(input, encoding, itemSet);
        } else if (encoding == MIMIC) {
            return MimicIngredientValues.load(input, itemSet);
        } else if (encoding == ITEM_BRIDGE) {
            return ItemBridgeIngredientValues.load(input, itemSet);
        } else if (encoding == NONE) {
            return new NoIngredientValues();
        } else {
            throw new UnknownEncodingException("Ingredient", encoding);
        }
    }

    protected ResultValues remainingItem;
    protected IngredientConstraintsValues constraints;

    IngredientValues(boolean mutable) {
        super(mutable);

        remainingItem = null;
        constraints = new IngredientConstraintsValues(false);
    }

    IngredientValues(IngredientValues toCopy, boolean mutable) {
        super(mutable);

        this.remainingItem = toCopy.getRemainingItem();
        this.constraints = toCopy.getConstraints();
    }

    protected String remainingToString() {
        if (remainingItem == null) {
            return "";
        } else {
            return " [" + remainingItem + "]";
        }
    }

    protected String amountToString(byte amount) {
        if (amount == 1) {
            return "";
        } else {
            return " x " + amount;
        }
    }

    public abstract String toString(String emptyString);

    public abstract byte getAmount() throws UnsupportedOperationException;

    @Override
    public abstract IngredientValues copy(boolean mutable);

    public abstract boolean conflictsWith(IngredientValues other);

    protected void loadRemainingItem(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        if (input.readBoolean()) {
            this.remainingItem = ResultValues.load(input, itemSet);
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

    public ResultValues getRemainingItem() {
        return remainingItem;
    }

    public IngredientConstraintsValues getConstraints() {
        return constraints;
    }

    public void setRemainingItem(ResultValues newRemainingItem) {
        assertMutable();
        if (newRemainingItem != null) {
            this.remainingItem = newRemainingItem.copy(false);
        } else {
            this.remainingItem = null;
        }
    }

    public void setConstraints(IngredientConstraintsValues constraints) {
        assertMutable();
        this.constraints = constraints.copy(false);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (remainingItem != null) Validation.scope("Remaining item", remainingItem::validateIndependent);
        constraints.validate();
    }

    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (remainingItem != null) {
            Validation.scope("Remaining item", () -> remainingItem.validateComplete(itemSet));
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (remainingItem != null) Validation.scope("Remaining", () -> remainingItem.validateExportVersion(version));
        constraints.validateExportVersion(version);
    }
}
