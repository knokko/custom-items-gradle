package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class AttackEffectDropWeapon extends AttackEffect {

    static AttackEffectDropWeapon loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackDropWeapon", encoding);

        return new AttackEffectDropWeapon(false);
    }

    public AttackEffectDropWeapon(boolean mutable) {
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
    public AttackEffectDropWeapon copy(boolean mutable) {
        return new AttackEffectDropWeapon(mutable);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AttackEffectDropWeapon;
    }

    @Override
    public String toString() {
        return "AttackDropWeapon";
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        // This class doesn't have any properties to be validated
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        // This class doesn't have any properties to be validated
    }
}
