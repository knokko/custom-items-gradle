package nl.knokko.customitems.sound;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.SoundTypeReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;
import java.util.UUID;

import static java.lang.Math.abs;

public class KciSound extends ModelValues {

    public static KciSound load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("Sound", encoding);

        KciSound result = new KciSound(false);
        if (input.readBoolean()) {
            result.vanillaSound = VSoundType.valueOf(input.readString());
            result.customSound = null;
        } else {
            result.vanillaSound = null;
            result.customSound = itemSet.soundTypes.getReference(new UUID(input.readLong(), input.readLong()));
        }

        result.volume = input.readFloat();
        result.pitch = input.readFloat();
        return result;
    }

    public static KciSound createQuick(VSoundType soundType, float volume, float pitch) {
        KciSound result = new KciSound(true);
        result.setVanillaSound(soundType);
        result.setCustomSound(null);
        result.setVolume(volume);
        result.setPitch(pitch);
        return result;
    }

    public static KciSound createQuick(SoundTypeReference soundType, float volume, float pitch) {
        KciSound result = new KciSound(true);
        result.setVanillaSound(null);
        result.setCustomSound(soundType);
        result.setVolume(volume);
        result.setPitch(pitch);
        return result;
    }

    private VSoundType vanillaSound;
    private SoundTypeReference customSound;

    private float volume;
    private float pitch;

    public KciSound(boolean mutable) {
        super(mutable);
        this.vanillaSound = VSoundType.ENTITY_GHAST_SCREAM;
        this.customSound = null;
        this.volume = 1f;
        this.pitch = 1f;
    }

    public KciSound(KciSound toCopy, boolean mutable) {
        super(mutable);
        this.vanillaSound = toCopy.getVanillaSound();
        this.customSound = toCopy.getCustomSoundReference();
        this.volume = toCopy.getVolume();
        this.pitch = toCopy.getPitch();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addBoolean(vanillaSound != null);
        if (vanillaSound != null) {
            output.addString(vanillaSound.name());
        } else {
            output.addLong(customSound.get().getId().getMostSignificantBits());
            output.addLong(customSound.get().getId().getLeastSignificantBits());
        }

        output.addFloat(volume);
        output.addFloat(pitch);
    }

    @Override
    public KciSound copy(boolean mutable) {
        return new KciSound(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciSound) {
            KciSound otherSound = (KciSound) other;
            return this.vanillaSound == otherSound.vanillaSound && Objects.equals(this.customSound, otherSound.customSound)
                    && abs(this.volume - otherSound.volume) < 0.001f && abs(this.pitch - otherSound.pitch) < 0.001f;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (vanillaSound != null) return "Sound(" + vanillaSound + ",volume=" + volume + ",pitch=" + pitch + ")";
        else return "Sound(" + customSound.get().getName() + ",volume=" + volume + ",pitch=" + pitch + ")";
    }

    public VSoundType getVanillaSound() {
        return vanillaSound;
    }

    public SoundTypeReference getCustomSoundReference() {
        return customSound;
    }

    public KciSoundType getCustomSound() {
        return customSound != null ? customSound.get() : null;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setVanillaSound(VSoundType vanillaSound) {
        assertMutable();
        this.vanillaSound = vanillaSound;
    }

    public void setCustomSound(SoundTypeReference customSound) {
        assertMutable();
        this.customSound = customSound;
    }

    public void setVolume(float volume) {
        assertMutable();
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        assertMutable();
        this.pitch = pitch;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if ((vanillaSound == null) == (customSound == null)) {
            throw new ValidationException("You must choose either a vanilla sound or a custom sound");
        }
        if (customSound != null && !itemSet.soundTypes.isValid(customSound)) {
            throw new ProgrammingValidationException("Custom sound is invalid");
        }
        if (volume <= 0f) throw new ValidationException("Volume must be positive");
        if (pitch <= 0f) throw new ValidationException("Pitch must be positive");
    }

    public void validateExportVersion(int mcVersion) throws ValidationException {
        if (vanillaSound != null) {
            if (mcVersion < vanillaSound.firstVersion) {
                throw new ValidationException("Sound " + vanillaSound + " doesn't exist yet in MC " + MCVersions.createString(mcVersion));
            }
            if (mcVersion > vanillaSound.lastVersion) {
                throw new ValidationException("Sound " + vanillaSound + " doesn't exist anymore in MC " + MCVersions.createString(mcVersion));
            }
        }
    }
}
