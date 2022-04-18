package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class AttackDropWeaponValues extends AttackEffectValues {

    static AttackDropWeaponValues loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackDropWeapon", encoding);

        return new AttackDropWeaponValues(false);
    }

    public AttackDropWeaponValues(boolean mutable) {
        super(mutable);
        // There are no properties to be initialized
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_DROP_WEAPON);
        output.addByte((byte) 1);

        // This class doesn't have any properties yet
    }

    @Override
    public AttackDropWeaponValues copy(boolean mutable) {
        return new AttackDropWeaponValues(mutable);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AttackDropWeaponValues;
    }

    @Override
    public String toString() {
        return "AttackDropWeapon";
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        // This class doesn't have any properties to be validated
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        // This class doesn't have any properties to be validated
    }
}
