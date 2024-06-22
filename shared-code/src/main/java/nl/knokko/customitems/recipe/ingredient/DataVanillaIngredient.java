package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.VANILLA_DATA_NEW;
import static nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient.checkMaterial;

public class DataVanillaIngredient extends KciIngredient {

    static DataVanillaIngredient load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        DataVanillaIngredient ingredient = new DataVanillaIngredient(false);

        if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA) {
            ingredient.load1(input);
        } else if (encoding == RecipeEncoding.Ingredient.VANILLA_DATA_2) {
            ingredient.load2(input, itemSet);
        } else if (encoding == VANILLA_DATA_NEW) {
            ingredient.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("DataVanillaIngredient", encoding);
        }

        return ingredient;
    }

    public static DataVanillaIngredient createQuick(VMaterial material, int dataValue, int amount) {
        return createQuick(material, dataValue, amount, null, new IngredientConstraints(true));
    }

    public static DataVanillaIngredient createQuick(
            VMaterial material, int dataValue, int amount,
            KciResult remainingItem, IngredientConstraints constraints
    ) {
        DataVanillaIngredient result = new DataVanillaIngredient(true);
        result.setMaterial(material);
        result.setAmount((byte) amount);
        result.setDataValue((byte) dataValue);
        result.setRemainingItem(remainingItem);
        result.setConstraints(constraints);
        return result;
    }

    private byte amount;
    private VMaterial material;
    private byte dataValue;

    DataVanillaIngredient(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.material = null;
        this.dataValue = 0;
    }

    @Override
    public String toString(String emptyString) {
        return material + "(" + dataValue + ")" + amountToString(amount) + remainingToString();
    }

    DataVanillaIngredient(DataVanillaIngredient toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.amount = toCopy.getAmount();
        this.material = toCopy.getMaterial();
        this.dataValue = toCopy.getDataValue();
    }

    @Override
    public boolean conflictsWith(KciIngredient other) {
        if (other instanceof DataVanillaIngredient) {
            DataVanillaIngredient otherIngredient = (DataVanillaIngredient) other;
            return this.material == otherIngredient.material && this.dataValue == otherIngredient.dataValue;
        } else if (other instanceof SimpleVanillaIngredient) {
            return this.material == ((SimpleVanillaIngredient) other).getMaterial();
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
        if (other instanceof DataVanillaIngredient) {
            DataVanillaIngredient otherIngredient = (DataVanillaIngredient) other;
            return this.material == otherIngredient.material && this.dataValue == otherIngredient.dataValue
                    && this.amount == otherIngredient.amount && Objects.equals(this.remainingItem, otherIngredient.remainingItem)
                    && this.constraints.equals(otherIngredient.constraints);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return dataValue + 17 * amount + 61 * material.ordinal() + 1731 * Objects.hashCode(remainingItem);
    }

    @Override
    public DataVanillaIngredient copy(boolean mutable) {
        return new DataVanillaIngredient(this, mutable);
    }

    private void load1(BitInput input) {
        this.amount = 1;
        this.remainingItem = null;
        this.material = VMaterial.valueOf(input.readJavaString());
        this.dataValue = (byte) input.readNumber((byte) 4, false);
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.amount = input.readByte();
        loadRemainingItem(input, itemSet);
        this.material = VMaterial.valueOf(input.readJavaString());
        this.dataValue = (byte) input.readNumber((byte) 4, false);
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("InternalDataIngredient", encoding);

        this.amount = input.readByte();
        this.material = VMaterial.valueOf(input.readString());
        this.dataValue = input.readByte();
        loadRemainingItem(input, itemSet);
        this.constraints = IngredientConstraints.load(input);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Ingredient.VANILLA_DATA_NEW);
        output.addByte((byte) 1);

        output.addByte(amount);
        output.addString(material.name());
        output.addByte(dataValue);
        saveRemainingItem(output);
        constraints.save(output);
    }

    @Override
    public byte getAmount() {
        return amount;
    }

    public VMaterial getMaterial() {
        return material;
    }

    public byte getDataValue() {
        return dataValue;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setMaterial(VMaterial newMaterial) {
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

        constraints.validate();

        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        checkMaterial(material);

        if (dataValue < 0) throw new ValidationException("Data value can't be negative");
        if (dataValue > 15) throw new ValidationException("Data value can be at most 15");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);
        if (version < material.firstVersion) {
            throw new ValidationException(material + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > material.lastVersion) {
            throw new ValidationException(material + " was renamed after mc " + MCVersions.createString(material.lastVersion));
        }
    }

    @Override
    public VMaterial getVMaterial(int mcVersion) {
        return material;
    }
}
