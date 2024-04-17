package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.*;

public abstract class KciIngredient extends ModelValues {

    public static KciIngredient load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == VANILLA_SIMPLE || encoding == VANILLA_SIMPLE_2 || encoding == VANILLA_SIMPLE_NEW) {
            return SimpleVanillaIngredient.load(input, encoding, itemSet);
        } else if (encoding == VANILLA_DATA || encoding == VANILLA_DATA_2 || encoding == VANILLA_DATA_NEW) {
            return DataVanillaIngredient.load(input, encoding, itemSet);
        } else if (encoding == CUSTOM || encoding == CUSTOM_2 || encoding == CUSTOM_NEW) {
            return CustomItemIngredient.load(input, encoding, itemSet);
        } else if (encoding == MIMIC) {
            return MimicIngredient.load(input, itemSet);
        } else if (encoding == ITEM_BRIDGE) {
            return ItemBridgeIngredient.load(input, itemSet);
        } else if (encoding == NONE) {
            return new NoIngredient();
        } else if (encoding == COPIED) {
            return CopiedIngredient.load(input);
        } else {
            throw new UnknownEncodingException("Ingredient", encoding);
        }
    }

    protected KciResult remainingItem;
    protected IngredientConstraints constraints;

    KciIngredient(boolean mutable) {
        super(mutable);

        remainingItem = null;
        constraints = new IngredientConstraints(false);
    }

    KciIngredient(KciIngredient toCopy, boolean mutable) {
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
    public abstract KciIngredient copy(boolean mutable);

    public abstract boolean conflictsWith(KciIngredient other);

    protected void loadRemainingItem(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        if (input.readBoolean()) {
            this.remainingItem = KciResult.load(input, itemSet);
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

    public KciResult getRemainingItem() {
        return remainingItem;
    }

    public IngredientConstraints getConstraints() {
        return constraints;
    }

    public void setRemainingItem(KciResult newRemainingItem) {
        assertMutable();
        if (newRemainingItem != null) {
            this.remainingItem = newRemainingItem.copy(false);
        } else {
            this.remainingItem = null;
        }
    }

    public void setConstraints(IngredientConstraints constraints) {
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
