package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class SimpleVanillaResultValues extends ResultValues {

    static SimpleVanillaResultValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        SimpleVanillaResultValues result = new SimpleVanillaResultValues(false);

        if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("SimpleVanillaResult", encoding);
        }

        return result;
    }

    public static SimpleVanillaResultValues createQuick(CIMaterial material, int amount) {
        SimpleVanillaResultValues result = new SimpleVanillaResultValues(true);
        result.setMaterial(material);
        result.setAmount((byte) amount);
        return result;
    }

    private byte amount;
    private CIMaterial material;

    public SimpleVanillaResultValues(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.material = null;
    }

    public SimpleVanillaResultValues(SimpleVanillaResultValues toCopy, boolean mutable) {
        super(mutable);

        this.amount = toCopy.getAmount();
        this.material = toCopy.getMaterial();
    }

    @Override
    public String toString() {
        return material + (amount == 1 ? "" : " x " + amount);
    }

    @Override
    public List<String> getInfo() {
        List<String> result = new ArrayList<>(2);
        result.add("Vanilla result:");
        result.add(material + " x " + amount);
        return result;
    }

    @Override
    public SimpleVanillaResultValues copy(boolean mutable) {
        return new SimpleVanillaResultValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == SimpleVanillaResultValues.class) {
            SimpleVanillaResultValues otherResult = (SimpleVanillaResultValues) other;
            return this.material == otherResult.material && this.amount == otherResult.amount;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return amount + 123 * material.ordinal();
    }

    private void load1(BitInput input) {
        this.amount = loadAmount(input);
        this.material = CIMaterial.valueOf(input.readJavaString());
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.VANILLA_SIMPLE);
        save1(output);
    }

    private void save1(BitOutput output) {
        saveAmount(output, this.amount);
        output.addJavaString(this.material.name());
    }

    public byte getAmount() {
        return amount;
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setAmount(int newAmount) {
        this.setAmount((byte) newAmount);
    }

    public void setMaterial(CIMaterial newMaterial) {
        assertMutable();
        Checks.notNull(newMaterial);
        this.material = newMaterial;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        if (material == null) throw new ValidationException("You need to choose a material");
    }

    @Override
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
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
