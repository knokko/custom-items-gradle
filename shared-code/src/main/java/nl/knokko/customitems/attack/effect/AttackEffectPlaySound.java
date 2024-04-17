package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class AttackEffectPlaySound extends AttackEffect {

    static AttackEffectPlaySound loadOwn(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("AttackPlaySound", encoding);

        AttackEffectPlaySound result = new AttackEffectPlaySound(false);
        if (encoding == 1) {
            result.sound = KciSound.createQuick(VSoundType.valueOf(input.readString()), input.readFloat(), input.readFloat()).copy(false);
        } else {
            result.sound = KciSound.load(input, itemSet);
        }
        return result;
    }

    public static AttackEffectPlaySound createQuick(KciSound sound) {
        AttackEffectPlaySound result = new AttackEffectPlaySound(true);
        result.setSound(sound);
        return result;
    }

    private KciSound sound;

    public AttackEffectPlaySound(boolean mutable) {
        super(mutable);
        this.sound = new KciSound(false);
    }

    public AttackEffectPlaySound(AttackEffectPlaySound toCopy, boolean mutable) {
        super(mutable);
        this.sound = toCopy.getSound();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_PLAY_SOUND);
        output.addByte((byte) 2);

        sound.save(output);
    }

    @Override
    public AttackEffectPlaySound copy(boolean mutable) {
        return new AttackEffectPlaySound(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackEffectPlaySound) {
            AttackEffectPlaySound otherEffect = (AttackEffectPlaySound) other;
            return this.sound.equals(otherEffect.sound);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return sound.toString();
    }

    public KciSound getSound() {
        return sound;
    }

    public void setSound(KciSound sound) {
        assertMutable();
        this.sound = sound.copy(false);
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (sound == null) throw new ProgrammingValidationException("No sound");
        Validation.scope("Sound", sound::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Sound", () -> sound.validateExportVersion(mcVersion));
    }
}
