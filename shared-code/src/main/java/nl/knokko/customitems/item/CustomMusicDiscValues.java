package nl.knokko.customitems.item;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class CustomMusicDiscValues extends CustomItemValues {

    static CustomMusicDiscValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("MusicDisc", encoding);

        CustomMusicDiscValues result = new CustomMusicDiscValues(false);
        result.loadSharedPropertiesNew(input, itemSet);
        result.music = SoundValues.load(input, itemSet);
        return result;
    }

    private SoundValues music;

    public CustomMusicDiscValues(boolean mutable) {
        super(mutable, CustomItemType.OTHER);
        this.otherMaterial = CIMaterial.MUSIC_DISC_11;
        this.music = SoundValues.createQuick(VanillaSoundType.MUSIC_DISC_11, 4f, 1f).copy(false);
    }

    public CustomMusicDiscValues(CustomMusicDiscValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.music = toCopy.getMusic();
    }

    @Override
    public CustomMusicDiscValues copy(boolean mutable) {
        return new CustomMusicDiscValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_MUSIC_DISC);
        output.addByte((byte) 1);
        saveSharedPropertiesNew(output, side);

        music.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomMusicDiscValues) {
            CustomMusicDiscValues otherDisc = (CustomMusicDiscValues) other;
            return this.areBaseItemPropertiesEqual(otherDisc) && this.music.equals(otherDisc.music);
        } else {
            return false;
        }
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    public SoundValues getMusic() {
        return music;
    }

    public void setMusic(SoundValues newMusic) {
        assertMutable();
        this.music = newMusic.copy(false);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (itemType != CustomItemType.OTHER) throw new ProgrammingValidationException("Only the Other item type is allowed");
        if (!otherMaterial.name().startsWith("MUSIC_DISC_")) {
            throw new ValidationException("The item type must be a music disc");
        }

        if (music == null) throw new ProgrammingValidationException("No music");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);
        music.validate(itemSet);
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(mcVersion);
        music.validateExportVersion(mcVersion);
    }
}
