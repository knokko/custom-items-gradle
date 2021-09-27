package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomHelmet3dValues extends CustomArmorValues {

    static CustomHelmet3dValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        CustomHelmet3dValues result = new CustomHelmet3dValues(false);

        if (encoding == ItemEncoding.ENCODING_HELMET3D_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_HELMET3D_11) {
            result.load11(input, itemSet);
            result.initDefaults11();
        } else {
            throw new UnknownEncodingException("CustomHelmet3D", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, false);
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
    public CustomHelmet3dValues copy(boolean mutable) {
        return new CustomHelmet3dValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_HELMET3D_11);
        saveArmor11(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (customModel != null) throw new ValidationException("3d helmets must have a custom model");
    }
}
