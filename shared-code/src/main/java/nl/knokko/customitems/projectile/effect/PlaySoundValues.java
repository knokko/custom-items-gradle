package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class PlaySoundValues extends ProjectileEffectValues {

    static PlaySoundValues load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        PlaySoundValues result = new PlaySoundValues(false);

        if (encoding == ENCODING_PLAY_SOUND_1) {
            result.load1(input);
        } else if (encoding == ENCODING_PLAY_SOUND_NEW) {
            result.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("PlaySoundProjectileEffect", encoding);
        }

        return result;
    }

    public static PlaySoundValues createQuick(SoundValues sound) {
        PlaySoundValues result = new PlaySoundValues(true);
        result.setSound(sound);
        return result;
    }

    private SoundValues sound;

    public PlaySoundValues(boolean mutable) {
        super(mutable);
        this.sound = SoundValues.createQuick(VanillaSoundType.ENTITY_BLAZE_SHOOT, 1f, 1f).copy(false);
    }

    public PlaySoundValues(PlaySoundValues toCopy, boolean mutable) {
        super(mutable);
        this.sound = toCopy.getSound();
    }

    @Override
    public String toString() {
        return sound.toString();
    }

    private void load1(BitInput input) {
        this.sound = SoundValues.createQuick(VanillaSoundType.valueOf(input.readString()), input.readFloat(), input.readFloat());
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("PlaySound", encoding);

        this.sound = SoundValues.load(input, itemSet);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_PLAY_SOUND_NEW);
        output.addByte((byte) 1);
        sound.save(output);
    }

    @Override
    public PlaySoundValues copy(boolean mutable) {
        return new PlaySoundValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PlaySoundValues.class) {
            PlaySoundValues otherEffect = (PlaySoundValues) other;
            return this.sound.equals(otherEffect.sound);
        } else {
            return false;
        }
    }

    public SoundValues getSound() {
        return sound;
    }

    public void setSound(SoundValues newSound) {
        assertMutable();
        Checks.notNull(newSound);
        this.sound = newSound.copy(false);
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (sound == null) throw new ProgrammingValidationException("No sound");
        Validation.scope("Sound", sound::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Sound", () -> sound.validateExportVersion(version));
    }
}
