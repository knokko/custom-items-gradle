package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class MimicIngredient extends KciIngredient {

    public static MimicIngredient load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte internalEncoding = input.readByte();
        if (internalEncoding != 1 && internalEncoding != 2) throw new UnknownEncodingException("MimicIngredient", internalEncoding);

        MimicIngredient result = new MimicIngredient(false);
        result.itemId = input.readString();
        result.amount = input.readInt();
        result.loadRemainingItem(input, itemSet);
        if (internalEncoding > 1) result.constraints = IngredientConstraints.load(input);
        return result;
    }

    public static MimicIngredient createQuick(String itemId, int amount) {
        return createQuick(itemId, amount, null, new IngredientConstraints(true));
    }

    public static MimicIngredient createQuick(
            String itemId, int amount, KciResult remainingItem, IngredientConstraints constraints
    ) {
        MimicIngredient result = new MimicIngredient(true);
        result.setItemId(itemId);
        result.setAmount(amount);
        result.setRemainingItem(remainingItem);
        result.setConstraints(constraints);
        return result;
    }

    private String itemId;
    private int amount;

    MimicIngredient(boolean mutable) {
        super(mutable);
        this.itemId = "minecraft:glass";
        this.amount = 2;
    }

    MimicIngredient(MimicIngredient toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.itemId = toCopy.getItemId();
        this.amount = toCopy.getAmount();
    }

    @Override
    public String toString() {
        return toString(null);
    }

    @Override
    public String toString(String emptyString) {
        return "Mimic(" + this.itemId + ")" + amountToString((byte) this.amount) + remainingToString();
    }

    @Override
    public byte getAmount() {
        return (byte) amount;
    }

    @Override
    public MimicIngredient copy(boolean mutable) {
        return new MimicIngredient(this, mutable);
    }

    @Override
    public boolean conflictsWith(KciIngredient other) {
        return other instanceof MimicIngredient && this.itemId.equals(((MimicIngredient) other).itemId);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MimicIngredient) {
            MimicIngredient otherMimic = (MimicIngredient) other;
            return this.itemId.equals(otherMimic.itemId) && this.amount == otherMimic.amount
                    && Objects.equals(this.remainingItem, otherMimic.remainingItem)
                    && this.constraints.equals(otherMimic.constraints);
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
        output.addByte((byte) 2);
        output.addString(this.itemId);
        output.addInt(this.amount);
        this.saveRemainingItem(output);
        constraints.save(output);
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

    @Override
    public VMaterial getVMaterial(int mcVersion) {
        // Return *null* because we don't know the materials of foreign custom items
        return null;
    }
}
