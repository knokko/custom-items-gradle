package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class CustomHelmet3dValues extends CustomArmorValues {

    static CustomHelmet3dValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        CustomHelmet3dValues result = new CustomHelmet3dValues(false);

        if (encoding == ItemEncoding.ENCODING_HELMET3D_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_HELMET3D_11) {
            result.load11(input, itemSet);
            result.initDefaults11();
        } else if (encoding == ItemEncoding.ENCODING_HELMET3D_12) {
            result.loadArmorPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomHelmet3D", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    public CustomHelmet3dValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);
    }

    public CustomHelmet3dValues(CustomHelmet3dValues toCopy, boolean mutable) {
        super(toCopy, mutable);
    }

    @Override
    public DefaultModelType getDefaultModelType() {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomHelmet3dValues.class && areArmorPropertiesEqual((CustomArmorValues) other);
    }

    @Override
    public CustomHelmet3dValues copy(boolean mutable) {
        return new CustomHelmet3dValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_HELMET3D_12);
        this.saveArmorPropertiesNew(output, side);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (model == null || model instanceof DefaultItemModel) throw new ValidationException("3d helmets must have a custom model");
        if (itemType == CustomItemType.OTHER && otherMaterial.name().contains("HELMET")) {
            throw new ValidationException("You must not use a vanilla helmet as internal item type");
        }
    }
}
