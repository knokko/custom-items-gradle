package nl.knokko.customitems.item;

import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class CustomFoodValues extends CustomItemValues {

    static CustomFoodValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        CustomFoodValues result = new CustomFoodValues(false);

        if (encoding == ItemEncoding.ENCODING_FOOD_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_FOOD_12) {
            result.loadFoodPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomFood", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private int foodValue;
    private Collection<PotionEffectValues> eatEffects;
    private int eatTime;

    private SoundValues eatSound;
    private int soundPeriod;

    private byte maxStacksize;

    public CustomFoodValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.foodValue = 4;
        this.eatEffects = new ArrayList<>();
        this.eatTime = 30;
        this.eatSound = SoundValues.createQuick(VanillaSoundType.ENTITY_GENERIC_EAT, 1f, 1f).copy(false);
        this.soundPeriod = 10;
        this.maxStacksize = 64;
    }

    public CustomFoodValues(CustomFoodValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.foodValue = toCopy.getFoodValue();
        this.eatEffects = toCopy.getEatEffects();
        this.eatTime = toCopy.getEatTime();
        this.eatSound = toCopy.getEatSound();
        this.soundPeriod = toCopy.getSoundPeriod();
        this.maxStacksize = toCopy.getMaxStacksize();
    }

    protected void loadFoodPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadSharedPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomFoodNew", encoding);

        this.eatEffects = this.loadPotionEffectList(input);

        if (encoding == 1) {
            VanillaSoundType eatSoundType = VanillaSoundType.valueOf(input.readString());
            this.foodValue = input.readInt();
            this.eatTime = input.readInt();
            this.soundPeriod = input.readInt();
            float eatSoundVolume = input.readFloat();
            float eatSoundPitch = input.readFloat();
            this.maxStacksize = input.readByte();
            this.eatSound = SoundValues.createQuick(eatSoundType, eatSoundVolume, eatSoundPitch).copy(false);
        } else {
            this.foodValue = input.readInt();
            this.eatSound = SoundValues.load(input, itemSet);
            this.eatTime = input.readInt();
            this.soundPeriod = input.readInt();
            this.maxStacksize = input.readByte();
        }
    }

    protected void saveFoodPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveSharedPropertiesNew(output, targetSide);

        output.addByte((byte) 2);

        this.savePotionEffectList(this.eatEffects, output);
        output.addInt(this.foodValue);
        this.eatSound.save(output);
        output.addInts(this.eatTime, this.soundPeriod);
        output.addByte(this.maxStacksize);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_FOOD_12);
        this.saveFoodPropertiesNew(output, side);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBase10(input, itemSet);
        loadFoodOnlyProperties10(input);
    }

    private void loadFoodOnlyProperties10(BitInput input) {
        this.foodValue = input.readInt();
        int numEatEffects = input.readInt();
        this.eatEffects = new ArrayList<>(numEatEffects);
        for (int counter = 0; counter < numEatEffects; counter++) {
            this.eatEffects.add(PotionEffectValues.load2(input, false));
        }
        this.eatTime = input.readInt();
        this.eatSound = SoundValues.createQuick(VanillaSoundType.valueOf(input.readString()), input.readFloat(), input.readFloat()).copy(false);
        this.soundPeriod = input.readInt();
        this.maxStacksize = (byte) input.readInt();
    }

    private void initDefaults10() {
        initBaseDefaults10();
        initFoodOnlyDefaults10();
    }

    private void initFoodOnlyDefaults10() {
        // Nothing to be done until the next encoding is known
    }

    @Override
    public byte getMaxStacksize() {
        return maxStacksize;
    }

    protected boolean areFoodPropertiesEqual(CustomFoodValues other) {
        return areBaseItemPropertiesEqual(other) && this.foodValue == other.foodValue && this.eatEffects.equals(other.eatEffects)
                && this.eatTime == other.eatTime && this.eatSound.equals(other.eatSound) && this.soundPeriod == other.soundPeriod
                && this.maxStacksize == other.maxStacksize;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomFoodValues.class && areFoodPropertiesEqual((CustomFoodValues) other);
    }

    @Override
    public CustomFoodValues copy(boolean mutable) {
        return new CustomFoodValues(this, mutable);
    }

    public int getFoodValue() {
        return foodValue;
    }

    public Collection<PotionEffectValues> getEatEffects() {
        return new ArrayList<>(eatEffects);
    }

    public int getEatTime() {
        return eatTime;
    }

    public SoundValues getEatSound() {
        return eatSound;
    }

    public int getSoundPeriod() {
        return soundPeriod;
    }

    public void setFoodValue(int newFoodValue) {
        assertMutable();
        this.foodValue = newFoodValue;
    }

    public void setEatEffects(Collection<PotionEffectValues> newEatEffects) {
        assertMutable();
        Checks.nonNull(newEatEffects);
        this.eatEffects = Mutability.createDeepCopy(newEatEffects, false);
    }

    public void setEatTime(int newEatTime) {
        assertMutable();
        this.eatTime = newEatTime;
    }

    public void setEatSound(SoundValues newEatSound) {
        assertMutable();
        Checks.notNull(newEatSound);
        this.eatSound = newEatSound.copy(false);
    }

    public void setSoundPeriod(int newPeriod) {
        assertMutable();
        this.soundPeriod = newPeriod;
    }

    public void setMaxStacksize(byte newStacksize) {
        assertMutable();
        this.maxStacksize = newStacksize;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (eatEffects == null) throw new ProgrammingValidationException("No eat effects");
        for (PotionEffectValues eatEffect : eatEffects) {
            if (eatEffect == null) throw new ProgrammingValidationException("Missing an eat effect");
            Validation.scope("Eat effects", eatEffect::validate);
        }

        if (eatTime < 1) throw new ValidationException("Eat time must be positive");
        if (eatSound == null) throw new ProgrammingValidationException("No eat sound");
        if (soundPeriod < 1) throw new ValidationException("Sound period must be positive");
        if (maxStacksize < 1) throw new ValidationException("Maximum stacksize must be positive");
        if (maxStacksize > 64) throw new ValidationException("Maximum stacksize can be at most 64");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        Validation.scope("Eat sound", eatSound::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (PotionEffectValues effect : eatEffects) {
            Validation.scope("Eat effects", () -> effect.validateExportVersion(version));
        }

        Validation.scope("Eat sound", () -> eatSound.validateExportVersion(version));
    }
}
