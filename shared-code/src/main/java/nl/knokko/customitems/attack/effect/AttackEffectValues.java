package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public abstract class AttackEffectValues extends ModelValues {

    protected static final byte ENCODING_POTION_EFFECT = 0;
    protected static final byte ENCODING_IGNITE = 1;
    protected static final byte ENCODING_DROP_WEAPON = 2;
    protected static final byte ENCODING_LAUNCH = 3;
    protected static final byte ENCODING_DEAL_DAMAGE = 4;
    protected static final byte ENCODING_PLAY_SOUND = 5;

    public static AttackEffectValues load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == ENCODING_POTION_EFFECT) {
            return AttackPotionEffectValues.loadOwn(input);
        } else if (encoding == ENCODING_IGNITE) {
            return AttackIgniteValues.loadOwn(input);
        } else if (encoding == ENCODING_DROP_WEAPON) {
            return AttackDropWeaponValues.loadOwn(input);
        } else if (encoding == ENCODING_LAUNCH) {
            return AttackLaunchValues.loadOwn(input);
        } else if (encoding == ENCODING_DEAL_DAMAGE) {
            return AttackDealDamageValues.loadOwn(input);
        } else if (encoding == ENCODING_PLAY_SOUND) {
            return AttackPlaySoundValues.loadOwn(input);
        } else {
            throw new UnknownEncodingException("AttackEffect", encoding);
        }
    }

    public AttackEffectValues(boolean mutable) {
        super(mutable);
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract AttackEffectValues copy(boolean mutable);

    public abstract void validate() throws ValidationException, ProgrammingValidationException;

    public abstract void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException;
}
