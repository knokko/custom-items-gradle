package nl.knokko.customitems.item;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class KciMusicDisc extends KciItem {

    static KciMusicDisc load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("MusicDisc", encoding);

        KciMusicDisc result = new KciMusicDisc(false);
        result.loadSharedPropertiesNew(input, itemSet);
        result.music = KciSound.load(input, itemSet);
        return result;
    }

    private KciSound music;

    public KciMusicDisc(boolean mutable) {
        super(mutable, KciItemType.OTHER);
        this.otherMaterial = VMaterial.MUSIC_DISC_11;
        this.music = KciSound.createQuick(VSoundType.MUSIC_DISC_11, 4f, 1f).copy(false);
    }

    public KciMusicDisc(KciMusicDisc toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.music = toCopy.getMusic();
    }

    @Override
    public KciMusicDisc copy(boolean mutable) {
        return new KciMusicDisc(this, mutable);
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
        if (other instanceof KciMusicDisc) {
            KciMusicDisc otherDisc = (KciMusicDisc) other;
            return this.areBaseItemPropertiesEqual(otherDisc) && this.music.equals(otherDisc.music);
        } else {
            return false;
        }
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    public KciSound getMusic() {
        return music;
    }

    public void setMusic(KciSound newMusic) {
        assertMutable();
        this.music = newMusic.copy(false);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (itemType != KciItemType.OTHER) throw new ProgrammingValidationException("Only the Other item type is allowed");
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
