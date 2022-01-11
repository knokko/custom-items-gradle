package nl.knokko.customitems.item;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.util.Checks.isClose;

public class CustomFoodValues extends CustomItemValues {

    static CustomFoodValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        CustomFoodValues result = new CustomFoodValues(false);

        if (encoding == ItemEncoding.ENCODING_FOOD_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomFood", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private int foodValue;
    private Collection<PotionEffectValues> eatEffects;
    private int eatTime;

    private CISound eatSound;
    private float soundVolume;
    private float soundPitch;
    private int soundPeriod;

    private byte maxStacksize;

    public CustomFoodValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.foodValue = 4;
        this.eatEffects = new ArrayList<>();
        this.eatTime = 30;
        this.eatSound = CISound.ENTITY_GENERIC_EAT;
        this.soundVolume = 1f;
        this.soundPitch = 1f;
        this.soundPeriod = 10;
        this.maxStacksize = 64;
    }

    public CustomFoodValues(CustomFoodValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.foodValue = toCopy.getFoodValue();
        this.eatEffects = toCopy.getEatEffects();
        this.eatTime = toCopy.getEatTime();
        this.eatSound = toCopy.getEatSound();
        this.soundVolume = toCopy.getSoundVolume();
        this.soundPitch = toCopy.getSoundPitch();
        this.soundPeriod = toCopy.getSoundPeriod();
        this.maxStacksize = toCopy.getMaxStacksize();
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_FOOD_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadBase10(input, itemSet);
        loadFoodOnlyProperties10(input);
    }

    private void save10(BitOutput output) {
        saveBase10(output);
        saveFoodOnlyProperties10(output);
    }

    private void loadFoodOnlyProperties10(BitInput input) {
        this.foodValue = input.readInt();
        int numEatEffects = input.readInt();
        this.eatEffects = new ArrayList<>(numEatEffects);
        for (int counter = 0; counter < numEatEffects; counter++) {
            this.eatEffects.add(PotionEffectValues.load2(input, false));
        }
        this.eatTime = input.readInt();
        this.eatSound = CISound.valueOf(input.readString());
        this.soundVolume = input.readFloat();
        this.soundPitch = input.readFloat();
        this.soundPeriod = input.readInt();
        this.maxStacksize = (byte) input.readInt();
    }

    private void saveFoodOnlyProperties10(BitOutput output) {
        output.addInt(foodValue);
        output.addInt(eatEffects.size());
        for (PotionEffectValues eatEffect : eatEffects) {
            eatEffect.save2(output);
        }
        output.addInt(eatTime);
        output.addString(eatSound.name());
        output.addFloats(soundVolume, soundPitch);
        output.addInt(soundPeriod);
        output.addInt(maxStacksize);
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
                && this.eatTime == other.eatTime && this.eatSound == other.eatSound && isClose(this.soundPitch, other.soundPitch)
                && isClose(this.soundVolume, other.soundVolume) && this.soundPeriod == other.soundPeriod
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

    public CISound getEatSound() {
        return eatSound;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getSoundPitch() {
        return soundPitch;
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

    public void setEatSound(CISound newEatSound) {
        assertMutable();
        Checks.notNull(newEatSound);
        this.eatSound = newEatSound;
    }

    public void setSoundVolume(float newVolume) {
        assertMutable();
        this.soundVolume = newVolume;
    }

    public void setSoundPitch(float newPitch) {
        assertMutable();
        this.soundPitch = newPitch;
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
        if (soundVolume <= 0f) throw new ValidationException("Sound volume must be positive");
        if (soundPitch <= 0f) throw new ValidationException("Sound pitch must be positive");
        if (soundPeriod < 1) throw new ValidationException("Sound period must be positive");
        if (maxStacksize < 1) throw new ValidationException("Maximum stacksize must be positive");
        if (maxStacksize > 64) throw new ValidationException("Maximum stacksize can be at most 64");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (PotionEffectValues effect : eatEffects) {
            Validation.scope("Eat effects", () -> effect.validateExportVersion(version));
        }

        if (version < eatSound.firstVersion) {
            throw new ValidationException(eatSound + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > eatSound.lastVersion) {
            throw new ValidationException(eatSound + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
    }
}
