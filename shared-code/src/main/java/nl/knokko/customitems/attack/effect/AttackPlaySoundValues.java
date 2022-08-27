package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

public class AttackPlaySoundValues extends AttackEffectValues {

    static AttackPlaySoundValues loadOwn(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("AttackPlaySound", encoding);

        AttackPlaySoundValues result = new AttackPlaySoundValues(false);
        if (encoding == 1) {
            result.sound = SoundValues.createQuick(VanillaSoundType.valueOf(input.readString()), input.readFloat(), input.readFloat()).copy(false);
        } else {
            result.sound = SoundValues.load(input, itemSet);
        }
        return result;
    }

    public static AttackPlaySoundValues createQuick(SoundValues sound) {
        AttackPlaySoundValues result = new AttackPlaySoundValues(true);
        result.setSound(sound);
        return result;
    }

    private SoundValues sound;

    public AttackPlaySoundValues(boolean mutable) {
        super(mutable);
        this.sound = new SoundValues(false);
    }

    public AttackPlaySoundValues(AttackPlaySoundValues toCopy, boolean mutable) {
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
    public AttackPlaySoundValues copy(boolean mutable) {
        return new AttackPlaySoundValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackPlaySoundValues) {
            AttackPlaySoundValues otherEffect = (AttackPlaySoundValues) other;
            return this.sound.equals(otherEffect.sound);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return sound.toString();
    }

    public SoundValues getSound() {
        return sound;
    }

    public void setSound(SoundValues sound) {
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
