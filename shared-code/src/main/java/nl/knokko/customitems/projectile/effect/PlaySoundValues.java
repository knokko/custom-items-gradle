package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Locale;

import static nl.knokko.customitems.util.Checks.isClose;

public class PlaySoundValues extends ProjectileEffectValues {

    static PlaySoundValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        PlaySoundValues result = new PlaySoundValues(false);

        if (encoding == ENCODING_PLAY_SOUND_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("PlaySoundProjectileEffect", encoding);
        }

        return result;
    }

    public static PlaySoundValues createQuick(VanillaSoundType sound, float volume, float pitch) {
        PlaySoundValues result = new PlaySoundValues(true);
        result.setSound(sound);
        result.setVolume(volume);
        result.setPitch(pitch);
        return result;
    }

    private VanillaSoundType sound;
    private float volume, pitch;

    public PlaySoundValues(boolean mutable) {
        super(mutable);
        this.sound = VanillaSoundType.ENTITY_BLAZE_SHOOT;
        this.volume = 1f;
        this.pitch = 1f;
    }

    public PlaySoundValues(PlaySoundValues toCopy, boolean mutable) {
        super(mutable);
        this.sound = toCopy.getSound();
        this.volume = toCopy.getVolume();
        this.pitch = toCopy.getPitch();
    }

    @Override
    public String toString() {
        return "PlaySound(" + sound.name().toLowerCase(Locale.ROOT) + ")";
    }

    private void load1(BitInput input) {
        this.sound = VanillaSoundType.valueOf(input.readString());
        this.volume = input.readFloat();
        this.pitch = input.readFloat();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_PLAY_SOUND_1);
        output.addString(sound.name());
        output.addFloats(volume, pitch);
    }

    @Override
    public PlaySoundValues copy(boolean mutable) {
        return new PlaySoundValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PlaySoundValues.class) {
            PlaySoundValues otherEffect = (PlaySoundValues) other;
            return this.sound == otherEffect.sound && isClose(this.volume, otherEffect.volume)
                    && isClose(this.pitch, otherEffect.pitch);
        } else {
            return false;
        }
    }

    public VanillaSoundType getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setSound(VanillaSoundType newSound) {
        assertMutable();
        Checks.notNull(newSound);
        this.sound = newSound;
    }

    public void setVolume(float newVolume) {
        assertMutable();
        this.volume = newVolume;
    }

    public void setPitch(float newPitch) {
        assertMutable();
        this.pitch = newPitch;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (sound == null) throw new ProgrammingValidationException("No sound");
        if (volume <= 0f) throw new ValidationException("Volume must be positive");
        if (pitch <= 0f) throw new ValidationException("Pitch must be positive");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < sound.firstVersion) {
            throw new ValidationException(sound + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > sound.lastVersion) {
            throw new ValidationException(sound + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
    }
}
