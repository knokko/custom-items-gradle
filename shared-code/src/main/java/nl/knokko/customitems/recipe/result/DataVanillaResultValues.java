package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class DataVanillaResultValues extends ResultValues {

    static DataVanillaResultValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        DataVanillaResultValues result = new DataVanillaResultValues(false);

        if (encoding == RecipeEncoding.Result.VANILLA_DATA) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("DataVanillaResult", encoding);
        }

        return result;
    }

    public static DataVanillaResultValues createQuick(CIMaterial material, int data, int amount) {
        DataVanillaResultValues result = new DataVanillaResultValues(true);
        result.setMaterial(material);
        result.setAmount((byte) amount);
        result.setDataValue((byte) data);
        return result;
    }

    private byte amount;
    private CIMaterial material;
    private byte data;

    DataVanillaResultValues(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.material = null;
        this.data = 0;
    }

    DataVanillaResultValues(DataVanillaResultValues toCopy, boolean mutable) {
        super(mutable);

        this.amount = toCopy.getAmount();
        this.material = toCopy.getMaterial();
        this.data = toCopy.getDataValue();
    }

    @Override
    public String toString() {
        String amountString = amount == 1 ? "" : (" x " + amount);
        return material + "[" + data + "]" + amountString;
    }

    @Override
    public List<String> getInfo() {
        List<String> result = new ArrayList<>(2);
        result.add("Data vanilla result:");
        result.add(material + " [" + data + "] x " + amount);
        return result;
    }

    @Override
    public DataVanillaResultValues copy(boolean mutable) {
        return new DataVanillaResultValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DataVanillaResultValues) {
            DataVanillaResultValues otherResult = (DataVanillaResultValues) other;
            return this.material == otherResult.material && this.amount == otherResult.amount && this.data == otherResult.data;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return data + 17 * amount + 173 * material.ordinal();
    }

    private void load1(BitInput input) {
        this.amount = loadAmount(input);
        this.material = CIMaterial.valueOf(input.readJavaString());
        this.data = (byte) input.readNumber((byte) 4, false);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.VANILLA_DATA);
        save1(output);
    }

    private void save1(BitOutput output) {
        saveAmount(output, this.amount);
        output.addJavaString(this.material.name());
        output.addNumber(this.data, (byte) 4, false);
    }

    public byte getAmount() {
        return amount;
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public byte getDataValue() {
        return data;
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
        this.data = newDataValue;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        if (material == null) throw new ValidationException("You must choose a material");

        if (data < 0) throw new ValidationException("Data value can't be negative");
        if (data > 15) throw new ValidationException("Data value can be at most 15");
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        if (version < material.firstVersion) {
            throw new ValidationException(material + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > material.lastVersion) {
            throw new ValidationException(material + " was renamed after mc " + MCVersions.createString(material.lastVersion));
        }
    }
}
