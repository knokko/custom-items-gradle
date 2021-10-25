package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Locale;

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

    private CISound sound;
    private float volume, pitch;

    public PlaySoundValues(boolean mutable) {
        super(mutable);
        this.sound = CISound.ENTITY_BLAZE_SHOOT;
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
        this.sound = CISound.valueOf(input.readString());
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

    public CISound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setSound(CISound newSound) {
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
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (sound == null) throw new ProgrammingValidationException("No sound");
        if (volume <= 0f) throw new ValidationException("Volume must be positive");
        if (pitch <= 0f) throw new ValidationException("Pitch must be positive");
    }
}
