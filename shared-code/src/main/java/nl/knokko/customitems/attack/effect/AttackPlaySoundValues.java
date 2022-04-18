package nl.knokko.customitems.attack.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttackPlaySoundValues extends AttackEffectValues {

    static AttackPlaySoundValues loadOwn(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("AttackPlaySound", encoding);

        AttackPlaySoundValues result = new AttackPlaySoundValues(false);
        result.sound = CISound.valueOf(input.readString());
        result.volume = input.readFloat();
        result.pitch = input.readFloat();
        return result;
    }

    public static AttackPlaySoundValues createQuick(CISound sound, float volume, float pitch) {
        AttackPlaySoundValues result = new AttackPlaySoundValues(true);
        result.setSound(sound);
        result.setVolume(volume);
        result.setPitch(pitch);
        return result;
    }

    private CISound sound;
    private float volume;
    private float pitch;

    public AttackPlaySoundValues(boolean mutable) {
        super(mutable);
        this.sound = CISound.BLOCK_ANVIL_LAND;
        this.volume = 1f;
        this.pitch = 1f;
    }

    public AttackPlaySoundValues(AttackPlaySoundValues toCopy, boolean mutable) {
        super(mutable);
        this.sound = toCopy.getSound();
        this.volume = toCopy.getVolume();
        this.pitch = toCopy.getPitch();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_PLAY_SOUND);
        output.addByte((byte) 1);

        output.addString(sound.name());
        output.addFloat(volume);
        output.addFloat(pitch);
    }

    @Override
    public AttackPlaySoundValues copy(boolean mutable) {
        return new AttackPlaySoundValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AttackPlaySoundValues) {
            AttackPlaySoundValues otherEffect = (AttackPlaySoundValues) other;
            return this.sound == otherEffect.sound && isClose(this.volume, otherEffect.volume) && isClose(this.pitch, otherEffect.pitch);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AttackPlaySound(" + sound + ",volume=" + volume + ",pitch=" + pitch + ")";
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

    public void setSound(CISound sound) {
        assertMutable();
        Checks.notNull(sound);
        this.sound = sound;
    }

    public void setVolume(float volume) {
        assertMutable();
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        assertMutable();
        this.pitch = pitch;
    }

    @Override
    public void validate() throws ValidationException, ProgrammingValidationException {
        if (sound == null) throw new ProgrammingValidationException("No sound");
        if (volume <= 0f) throw new ValidationException("Volume must be positive");
        if (pitch <= 0f) throw new ValidationException("Pich must be positive");
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        if (mcVersion < sound.firstVersion) {
            throw new ValidationException("Sound " + sound + " doesn't exist yet in MC " + MCVersions.createString(mcVersion));
        }
        if (mcVersion > sound.lastVersion) {
            throw new ValidationException("Sound " + sound + " no longer exists in MC " + MCVersions.createString(mcVersion));
        }
    }
}
