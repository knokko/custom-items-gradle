package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class SimpleVanillaDisplayItemValues extends SlotDisplayItemValues {

    static SimpleVanillaDisplayItemValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        SimpleVanillaDisplayItemValues result = new SimpleVanillaDisplayItemValues(false);

        if (encoding == Encodings.SIMPLE_VANILLA1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("SimpleVanillaDisplayItem", encoding);
        }

        return result;
    }

    public static SimpleVanillaDisplayItemValues createQuick(CIMaterial material) {
        SimpleVanillaDisplayItemValues result = new SimpleVanillaDisplayItemValues(true);
        result.setMaterial(material);
        return result;
    }

    private CIMaterial material;

    public SimpleVanillaDisplayItemValues(boolean mutable) {
        super(mutable);
        this.material = CIMaterial.STONE;
    }

    public SimpleVanillaDisplayItemValues(SimpleVanillaDisplayItemValues toCopy, boolean mutable) {
        super(mutable);
        this.material = toCopy.getMaterial();
    }

    private void load1(BitInput input) {
        this.material = CIMaterial.valueOf(input.readString());
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.SIMPLE_VANILLA1);
        output.addString(material.name());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleVanillaDisplayItemValues) {
            return this.material == ((SimpleVanillaDisplayItemValues) other).material;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(this.material.name());
    }

    @Override
    public SimpleVanillaDisplayItemValues copy(boolean mutable) {
        return new SimpleVanillaDisplayItemValues(this, mutable);
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public void setMaterial(CIMaterial material) {
        assertMutable();
        Checks.notNull(material);
        this.material = material;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (material == null) throw new ProgrammingValidationException("No material");
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
