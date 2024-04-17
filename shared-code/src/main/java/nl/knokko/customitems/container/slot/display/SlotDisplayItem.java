package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public abstract class SlotDisplayItem extends ModelValues {

    static class Encodings {
        static final byte CUSTOM1 = 0;
        static final byte DATA_VANILLA1 = 1;
        static final byte SIMPLE_VANILLA1 = 2;
    }

    public static SlotDisplayItem load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == Encodings.CUSTOM1) {
            return CustomDisplayItem.load(input, encoding, itemSet);
        } else if (encoding == Encodings.DATA_VANILLA1) {
            return DataVanillaDisplayItem.load(input, encoding);
        } else if (encoding == Encodings.SIMPLE_VANILLA1) {
            return SimpleVanillaDisplayItem.load(input, encoding);
        } else {
            throw new UnknownEncodingException("SlotDisplayItem", encoding);
        }
    }

    SlotDisplayItem(boolean mutable) {
        super(mutable);
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract SlotDisplayItem copy(boolean mutable);

    public abstract void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    public abstract void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException;
}
