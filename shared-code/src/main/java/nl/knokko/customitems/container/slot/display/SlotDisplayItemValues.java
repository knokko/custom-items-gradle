package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public abstract class SlotDisplayItemValues extends ModelValues {

    static class Encodings {
        static final byte CUSTOM1 = 0;
        static final byte DATA_VANILLA1 = 1;
        static final byte SIMPLE_VANILLA1 = 2;
    }

    public static SlotDisplayItemValues load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == Encodings.CUSTOM1) {
            return CustomDisplayItemValues.load(input, encoding, itemSet);
        } else if (encoding == Encodings.DATA_VANILLA1) {
            return DataVanillaDisplayItemValues.load(input, encoding);
        } else if (encoding == Encodings.SIMPLE_VANILLA1) {
            return SimpleVanillaDisplayItemValues.load(input, encoding);
        } else {
            throw new UnknownEncodingException("SlotDisplayItem", encoding);
        }
    }

    SlotDisplayItemValues(boolean mutable) {
        super(mutable);
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract SlotDisplayItemValues copy(boolean mutable);

    public abstract void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    public abstract void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException;
}
