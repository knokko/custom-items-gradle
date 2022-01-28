package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public abstract class GunAmmoValues extends ModelValues {

    static final byte ENCODING_DIRECT_1 = 0;
    static final byte ENCODING_INDIRECT_1 = 1;

    public static GunAmmoValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == ENCODING_DIRECT_1) {
            return DirectGunAmmoValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_INDIRECT_1) {
            return IndirectGunAmmoValues.load(input, encoding, itemSet);
        } else {
            throw new UnknownEncodingException("GunAmmo", encoding);
        }
    }

    GunAmmoValues(boolean mutable) {
        super(mutable);
    }

    @Override
    public abstract GunAmmoValues copy(boolean mutable);

    public abstract void save(BitOutput output);

    public abstract int getCooldown();

    public abstract void validateIndependent() throws ValidationException, ProgrammingValidationException;

    public abstract void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    public abstract void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException;
}
