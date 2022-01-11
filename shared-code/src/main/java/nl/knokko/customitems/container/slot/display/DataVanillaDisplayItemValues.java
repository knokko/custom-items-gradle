package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class DataVanillaDisplayItemValues extends SlotDisplayItemValues {

    static DataVanillaDisplayItemValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        DataVanillaDisplayItemValues result = new DataVanillaDisplayItemValues(false);

        if (encoding == Encodings.DATA_VANILLA1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("DataVanillaDisplayItem", encoding);
        }

        return result;
    }

    public static DataVanillaDisplayItemValues createQuick(CIMaterial material, int dataValue) {
        DataVanillaDisplayItemValues result = new DataVanillaDisplayItemValues(true);
        result.setMaterial(material);
        result.setDataValue((byte) dataValue);
        return result;
    }

    private CIMaterial material;
    private byte dataValue;

    public DataVanillaDisplayItemValues(boolean mutable) {
        super(mutable);
        this.material = CIMaterial.STONE;
        this.dataValue = 0;
    }

    public DataVanillaDisplayItemValues(DataVanillaDisplayItemValues toCopy, boolean mutable) {
        super(mutable);
        this.material = toCopy.getMaterial();
        this.dataValue = toCopy.getDataValue();
    }

    private void load1(BitInput input) {
        this.material = CIMaterial.valueOf(input.readString());
        this.dataValue = input.readByte();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.DATA_VANILLA1);
        output.addString(material.name());
        output.addByte(dataValue);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DataVanillaDisplayItemValues) {
            DataVanillaDisplayItemValues otherItem = (DataVanillaDisplayItemValues) other;
            return this.material == otherItem.material && this.dataValue == otherItem.dataValue;
        } else {
            return false;
        }
    }

    @Override
    public DataVanillaDisplayItemValues copy(boolean mutable) {
        return new DataVanillaDisplayItemValues(this, mutable);
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public byte getDataValue() {
        return dataValue;
    }

    public void setMaterial(CIMaterial material) {
        assertMutable();
        Checks.notNull(material);
        this.material = material;
    }

    public void setDataValue(byte dataValue) {
        assertMutable();
        this.dataValue = dataValue;
    }

    @Override
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (material == null) throw new ProgrammingValidationException("No material");
        if (dataValue < 0) throw new ValidationException("Data value can't be negative");
        if (dataValue > 15) throw new ValidationException("Data value can be at most 15");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < material.firstVersion) {
            throw new ValidationException(material + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > material.lastVersion) {
            throw new ValidationException(material + " was renamed after " + MCVersions.createString(material.lastVersion));
        }
    }
}
