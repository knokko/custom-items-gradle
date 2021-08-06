package nl.knokko.customitems.projectile.effects;

import nl.knokko.customitems.sound.CISound;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class PlaySound extends ProjectileEffect {

    public static PlaySound load1(BitInput input) {
        return new PlaySound(CISound.valueOf(input.readString()), input.readFloat(), input.readFloat());
    }

    public CISound sound;
    public float volume;
    public float pitch;

    public PlaySound(CISound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PlaySound) {
            PlaySound ps = (PlaySound) other;
            return sound == ps.sound && volume == ps.volume && pitch == ps.pitch;
        } else {
            return false;
        }
    }

    @Override
    public void toBits(BitOutput output) {
        output.addByte(ENCODING_PLAY_SOUND_1);
        output.addString(sound.name());
        output.addFloat(volume);
        output.addFloat(pitch);
    }

    @Override
    public String validate() {
        if (sound == null) return  "You need to select a sound";
        if (volume <= 0f) return "The volume must be positive";
        if (pitch <= 0f) return "The pitch must be positive";
        return null;
    }

    @Override
    public String toString() {
        return "Play " + sound + " (" + volume + ", " + pitch + ")";
    }
}
