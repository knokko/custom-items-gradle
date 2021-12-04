package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Objects;

public class DataVanillaIngredientValues extends IngredientValues {

    static DataVanillaIngredientValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        DataVanillaIngredientValues ingredient = new DataVanillaIngredientValues(false);

        if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA) {
            ingredient.load1(input);
        } else if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA_2) {
            ingredient.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("DataVanillaIngredient", encoding);
        }

        return ingredient;
    }

    public static DataVanillaIngredientValues createQuick(
            CIMaterial material, int dataValue, int amount, ResultValues remainingItem
    ) {
        DataVanillaIngredientValues result = new DataVanillaIngredientValues(true);
        result.setMaterial(material);
        result.setAmount((byte) amount);
        result.setDataValue((byte) dataValue);
        result.setRemainingItem(remainingItem);
        return result;
    }

    private byte amount;
    private CIMaterial material;
    private byte dataValue;

    DataVanillaIngredientValues(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.material = null;
        this.dataValue = 0;
    }

    @Override
    public String toString(String emptyString) {
        return material + "(" + dataValue + ")" + amountToString(amount) + remainingToString();
    }

    DataVanillaIngredientValues(DataVanillaIngredientValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.amount = toCopy.getAmount();
        this.material = toCopy.getMaterial();
        this.dataValue = toCopy.getDataValue();
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        if (other instanceof DataVanillaIngredientValues) {
            DataVanillaIngredientValues otherIngredient = (DataVanillaIngredientValues) other;
            return this.material == otherIngredient.material && this.dataValue == otherIngredient.dataValue;
        } else if (other instanceof SimpleVanillaIngredientValues) {
            return this.material == ((SimpleVanillaIngredientValues) other).getMaterial();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return toString(null);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DataVanillaIngredientValues) {
            DataVanillaIngredientValues otherIngredient = (DataVanillaIngredientValues) other;
            return this.material == otherIngredient.material && this.dataValue == otherIngredient.dataValue
                    && this.amount == otherIngredient.amount && Objects.equals(this.remainingItem, otherIngredient.remainingItem);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return dataValue + 17 * amount + 61 * material.ordinal() + 1731 * Objects.hashCode(remainingItem);
    }

    @Override
    public DataVanillaIngredientValues copy(boolean mutable) {
        return new DataVanillaIngredientValues(this, mutable);
    }

    private void load1(BitInput input) {
        this.amount = 1;
        this.remainingItem = null;
        this.material = CIMaterial.valueOf(input.readJavaString());
        this.dataValue = (byte) input.readNumber((byte) 4, false);
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.amount = input.readByte();
        loadRemainingItem(input, itemSet);
        this.material = CIMaterial.valueOf(input.readJavaString());
        this.dataValue = (byte) input.readNumber((byte) 4, false);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.VANILLA_DATA_2);
        save2(output);
    }

    private void save2(BitOutput output) {
        output.addByte(amount);
        saveRemainingItem(output);
        output.addJavaString(material.name());
        output.addNumber(dataValue, (byte) 4, false);
    }

    public byte getAmount() {
        return amount;
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public byte getDataValue() {
        return dataValue;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setMaterial(CIMaterial newMaterial) {
        assertMutable();
        Checks.notNull(newMaterial);
        this.material = newMaterial;
    }

    public void setDataValue(byte newDataValue) {
        assertMutable();
        this.dataValue = newDataValue;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        if (material == null) throw new ValidationException("You must choose a material");
        if (material == CIMaterial.AIR) throw new ValidationException("Air is not allowed");

        if (dataValue < 0) throw new ValidationException("Data value can't be negative");
        if (dataValue > 15) throw new ValidationException("Data value can be at most 15");
    }
}
