package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class PEPlaySound extends ProjectileEffect {

    static PEPlaySound load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        PEPlaySound result = new PEPlaySound(false);

        if (encoding == ENCODING_PLAY_SOUND_1) {
            result.load1(input);
        } else if (encoding == ENCODING_PLAY_SOUND_NEW) {
            result.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("PlaySoundProjectileEffect", encoding);
        }

        return result;
    }

    public static PEPlaySound createQuick(KciSound sound) {
        PEPlaySound result = new PEPlaySound(true);
        result.setSound(sound);
        return result;
    }

    private KciSound sound;

    public PEPlaySound(boolean mutable) {
        super(mutable);
        this.sound = KciSound.createQuick(VSoundType.ENTITY_BLAZE_SHOOT, 1f, 1f).copy(false);
    }

    public PEPlaySound(PEPlaySound toCopy, boolean mutable) {
        super(mutable);
        this.sound = toCopy.getSound();
    }

    @Override
    public String toString() {
        return sound.toString();
    }

    private void load1(BitInput input) {
        this.sound = KciSound.createQuick(VSoundType.valueOf(input.readString()), input.readFloat(), input.readFloat());
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("PlaySound", encoding);

        this.sound = KciSound.load(input, itemSet);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_PLAY_SOUND_NEW);
        output.addByte((byte) 1);
        sound.save(output);
    }

    @Override
    public PEPlaySound copy(boolean mutable) {
        return new PEPlaySound(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PEPlaySound.class) {
            PEPlaySound otherEffect = (PEPlaySound) other;
            return this.sound.equals(otherEffect.sound);
        } else {
            return false;
        }
    }

    public KciSound getSound() {
        return sound;
    }

    public void setSound(KciSound newSound) {
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
