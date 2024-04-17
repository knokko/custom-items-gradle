package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class DataVanillaDisplayItem extends SlotDisplayItem {

    static DataVanillaDisplayItem load(BitInput input, byte encoding) throws UnknownEncodingException {
        DataVanillaDisplayItem result = new DataVanillaDisplayItem(false);

        if (encoding == Encodings.DATA_VANILLA1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("DataVanillaDisplayItem", encoding);
        }

        return result;
    }

    public static DataVanillaDisplayItem createQuick(VMaterial material, int dataValue) {
        DataVanillaDisplayItem result = new DataVanillaDisplayItem(true);
        result.setMaterial(material);
        result.setDataValue((byte) dataValue);
        return result;
    }

    private VMaterial material;
    private byte dataValue;

    public DataVanillaDisplayItem(boolean mutable) {
        super(mutable);
        this.material = VMaterial.STONE;
        this.dataValue = 0;
    }

    public DataVanillaDisplayItem(DataVanillaDisplayItem toCopy, boolean mutable) {
        super(mutable);
        this.material = toCopy.getMaterial();
        this.dataValue = toCopy.getDataValue();
    }

    private void load1(BitInput input) {
        this.material = VMaterial.valueOf(input.readString());
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
        if (other instanceof DataVanillaDisplayItem) {
            DataVanillaDisplayItem otherItem = (DataVanillaDisplayItem) other;
            return this.material == otherItem.material && this.dataValue == otherItem.dataValue;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(this.material.name()) + "[" + this.dataValue + "]";
    }

    @Override
    public DataVanillaDisplayItem copy(boolean mutable) {
        return new DataVanillaDisplayItem(this, mutable);
    }

    public VMaterial getMaterial() {
        return material;
    }

    public byte getDataValue() {
        return dataValue;
    }

    public void setMaterial(VMaterial material) {
        assertMutable();
        Checks.notNull(material);
        this.material = material;
    }

    public void setDataValue(byte dataValue) {
        assertMutable();
        this.dataValue = dataValue;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
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
