package nl.knokko.customitems.sound;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.UUID;

public class CustomSoundTypeValues extends ModelValues {

    public static CustomSoundTypeValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("SoundType", encoding);

        CustomSoundTypeValues result = new CustomSoundTypeValues(false);
        result.id = new UUID(input.readLong(), input.readLong());
        result.name = input.readString();
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.oggData = input.readByteArray();
        } else {
            result.oggData = null;
        }
        result.soundCategory = CISoundCategory.valueOf(input.readString());
        return result;
    }

    private UUID id;
    private String name;

    private byte[] oggData;
    private CISoundCategory soundCategory;

    public CustomSoundTypeValues(boolean mutable) {
        super(mutable);
        this.id = UUID.randomUUID();
        this.name = "";
        this.oggData = null;
        this.soundCategory = CISoundCategory.MASTER;
    }

    public CustomSoundTypeValues(CustomSoundTypeValues toCopy, boolean mutable) {
        super(mutable);
        this.id = toCopy.getId();
        this.name = toCopy.getName();
        this.oggData = toCopy.getOggData();
        this.soundCategory = toCopy.getSoundCategory();
    }

    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte((byte) 1);

        output.addLong(id.getMostSignificantBits());
        output.addLong(id.getLeastSignificantBits());
        output.addString(name);

        // To limit the size of .cis files, exclude the actual sound data
        if (side == ItemSet.Side.EDITOR) {
            output.addByteArray(oggData);
        }
        output.addString(soundCategory.name());
    }

    @Override
    public CustomSoundTypeValues copy(boolean mutable) {
        return new CustomSoundTypeValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomSoundTypeValues) {
            CustomSoundTypeValues otherSound = (CustomSoundTypeValues) other;

            // Don't test the sound data because it is only available on the Editor side
            return this.id.equals(otherSound.id) && this.name.equals(otherSound.name)
                    && this.soundCategory == otherSound.soundCategory;
        } else {
            return false;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Note 1: this will be null on the plug-in and should be non-null on the Editor<br>
     * Note 2: modifications to the returned array may modify the internal data of this sound type, which is
     * usually undesired
     */
    public byte[] getOggData() {
        return oggData;
    }

    public CISoundCategory getSoundCategory() {
        return soundCategory;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setOggData(byte[] oggData) {
        assertMutable();
        Checks.notNull(oggData);
        this.oggData = oggData;
    }

    public void setSoundCategory(CISoundCategory soundCategory) {
        assertMutable();
        Checks.notNull(soundCategory);
        this.soundCategory = soundCategory;
    }

    public void validate(ItemSet itemSet, UUID oldID) throws ValidationException, ProgrammingValidationException {
        if (id == null) throw new ProgrammingValidationException("No ID");
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        Validation.safeName(name);
        if (oggData == null && itemSet.getSide() == ItemSet.Side.EDITOR) throw new ValidationException("You must choose a sound");
        if (soundCategory == null) throw new ProgrammingValidationException("No sound category");

        if (oldID != null && !oldID.equals(id)) throw new ProgrammingValidationException("Can't change the ID");
        if (oldID == null && itemSet.soundTypes.get(id).isPresent()) {
            throw new ProgrammingValidationException("Sound type with this ID already exists");
        }
        if (itemSet.soundTypes.stream().anyMatch(type -> !type.getId().equals(id) && type.getName().equals(name))) {
            throw new ValidationException("Sound type with name " + name + " already exists");
        }
    }
}
