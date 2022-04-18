package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.CrossbowTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.util.Checks.isClose;

public class CustomCrossbowValues extends CustomToolValues {

    static CustomCrossbowValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        CustomCrossbowValues result = new CustomCrossbowValues(false);

        if (encoding == ItemEncoding.ENCODING_CROSSBOW_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_CROSSBOW_12) {
            result.loadCrossbowPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomCrossbow", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private int arrowDurabilityLoss;
    private int fireworkDurabilityLoss;

    private float arrowDamageMultiplier;
    private float fireworkDamageMultiplier;

    private float arrowSpeedMultiplier;
    private float fireworkSpeedMultiplier;

    private int arrowKnockbackStrength;
    private boolean arrowGravity;

    public CustomCrossbowValues(boolean mutable) {
        super(mutable, CustomItemType.CROSSBOW);

        this.arrowDurabilityLoss = 1;
        this.fireworkDurabilityLoss = 3;
        this.arrowDamageMultiplier = 1f;
        this.fireworkDamageMultiplier = 1f;
        this.arrowSpeedMultiplier = 1f;
        this.fireworkSpeedMultiplier = 1f;
        this.arrowKnockbackStrength = 0;
        this.arrowGravity = true;
    }

    public CustomCrossbowValues(CustomCrossbowValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.arrowDurabilityLoss = toCopy.getArrowDurabilityLoss();
        this.fireworkDurabilityLoss = toCopy.getFireworkDurabilityLoss();
        this.arrowDamageMultiplier = toCopy.getArrowDamageMultiplier();
        this.fireworkDamageMultiplier = toCopy.getFireworkDamageMultiplier();
        this.arrowSpeedMultiplier = toCopy.getArrowSpeedMultiplier();
        this.fireworkSpeedMultiplier = toCopy.getFireworkSpeedMultiplier();
        this.arrowKnockbackStrength = toCopy.getArrowKnockbackStrength();
        this.arrowGravity = toCopy.hasArrowGravity();
    }

    protected void loadCrossbowPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadToolPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomCrossbowNew" , encoding);

        this.arrowDurabilityLoss = input.readInt();
        this.fireworkDurabilityLoss = input.readInt();
        this.arrowKnockbackStrength = input.readInt();
        this.arrowDamageMultiplier = input.readFloat();
        this.fireworkDamageMultiplier = input.readFloat();
        this.arrowSpeedMultiplier = input.readFloat();
        this.fireworkSpeedMultiplier = input.readFloat();
        this.arrowGravity = input.readBoolean();
    }

    protected void saveCrossbowPropertiesNew(BitOutput output, ItemSet.Side side) {
        this.saveToolPropertiesNew(output, side);

        output.addByte((byte) 1);

        output.addInts(this.arrowDurabilityLoss, this.fireworkDurabilityLoss, this.arrowKnockbackStrength);
        output.addFloats(
                this.arrowDamageMultiplier, this.fireworkDamageMultiplier,
                this.arrowSpeedMultiplier, this.fireworkSpeedMultiplier
        );
        output.addBoolean(this.arrowGravity);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadCrossbowIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
        loadCrossbowOnlyProperties10(input);
    }

    private void loadCrossbowOnlyProperties10(BitInput input) {
        this.arrowDurabilityLoss = input.readInt();
        this.fireworkDurabilityLoss = input.readInt();
        this.arrowDamageMultiplier = input.readFloat();
        this.fireworkDamageMultiplier = input.readFloat();
        this.arrowSpeedMultiplier = input.readFloat();
        this.fireworkSpeedMultiplier = input.readFloat();
        this.arrowKnockbackStrength = input.readInt();
        this.arrowGravity = input.readBoolean();
    }

    private void loadCrossbowIdentityProperties10(BitInput input) {
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
        this.alias = input.readString();
    }

    private void initDefaults10() {
        initToolDefaults10();
        initCrossbowOnlyDefaults10();
    }

    private void initCrossbowOnlyDefaults10() {
        // There is nothing to be done until the next encoding
    }

    protected boolean areCrossbowPropertiesEqual(CustomCrossbowValues other) {
        return areToolPropertiesEqual(other) && this.arrowDurabilityLoss == other.arrowDurabilityLoss
                && this.fireworkDurabilityLoss == other.fireworkDurabilityLoss
                && isClose(this.arrowDamageMultiplier, other.arrowDamageMultiplier)
                && isClose(this.fireworkDamageMultiplier, other.fireworkDamageMultiplier)
                && isClose(this.arrowSpeedMultiplier, other.arrowSpeedMultiplier)
                && isClose(this.fireworkSpeedMultiplier, other.fireworkSpeedMultiplier)
                && this.arrowKnockbackStrength == other.arrowKnockbackStrength && this.arrowGravity == other.arrowGravity;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomCrossbowValues.class && areCrossbowPropertiesEqual((CustomCrossbowValues) other);
    }

    @Override
    public CustomCrossbowValues copy(boolean mutable) {
        return new CustomCrossbowValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_CROSSBOW_12);
        saveCrossbowPropertiesNew(output, side);
    }

    @Override
    public CrossbowTextureValues getTexture() {
        return (CrossbowTextureValues) super.getTexture();
    }

    public int getArrowDurabilityLoss() {
        return arrowDurabilityLoss;
    }

    public int getFireworkDurabilityLoss() {
        return fireworkDurabilityLoss;
    }

    public float getArrowDamageMultiplier() {
        return arrowDamageMultiplier;
    }

    public float getFireworkDamageMultiplier() {
        return fireworkDamageMultiplier;
    }

    public float getArrowSpeedMultiplier() {
        return arrowSpeedMultiplier;
    }

    public float getFireworkSpeedMultiplier() {
        return fireworkSpeedMultiplier;
    }

    public int getArrowKnockbackStrength() {
        return arrowKnockbackStrength;
    }

    public boolean hasArrowGravity() {
        return arrowGravity;
    }

    @Override
    public void setTexture(TextureReference newTexture) {
        if (!(newTexture.get() instanceof CrossbowTextureValues)) {
            throw new IllegalArgumentException("Only crossbow textures are allowed");
        }
        super.setTexture(newTexture);
    }

    public void setArrowDurabilityLoss(int newArrowDurabilityLoss) {
        assertMutable();
        this.arrowDurabilityLoss = newArrowDurabilityLoss;
    }

    public void setFireworkDurabilityLoss(int newFireworkDurabilityLoss) {
        assertMutable();
        this.fireworkDurabilityLoss = newFireworkDurabilityLoss;
    }

    public void setArrowDamageMultiplier(float newArrowDamageMultiplier) {
        assertMutable();
        this.arrowDamageMultiplier = newArrowDamageMultiplier;
    }

    public void setFireworkDamageMultiplier(float newFireworkDamageMultiplier) {
        assertMutable();
        this.fireworkDamageMultiplier = newFireworkDamageMultiplier;
    }

    public void setArrowSpeedMultiplier(float newArrowSpeedMultiplier) {
        assertMutable();
        this.arrowSpeedMultiplier = newArrowSpeedMultiplier;
    }

    public void setFireworkSpeedMultiplier(float newFireworkSpeedMultiplier) {
        assertMutable();
        this.fireworkSpeedMultiplier = newFireworkSpeedMultiplier;
    }

    public void setArrowKnockbackStrength(int newArrowKnockbackStrength) {
        assertMutable();
        this.arrowKnockbackStrength = newArrowKnockbackStrength;
    }

    public void setArrowGravity(boolean newArrowGravity) {
        assertMutable();
        this.arrowGravity = newArrowGravity;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (arrowDurabilityLoss < 0) throw new ValidationException("Arrow durability loss can't be negative");
        if (fireworkDurabilityLoss < 0) throw new ValidationException("Firework durability loss can't be negative");
        if (arrowDamageMultiplier < 0f) throw new ValidationException("Arrow damage multiplier can't be negative");
        if (fireworkDamageMultiplier < 0f) throw new ValidationException("Firework damage multiplier can't be negative");

        // The speed multipliers are allowed to be negative
        // The arrow knockback strength is also allowed to be negative

        if (customModel != null) throw new ProgrammingValidationException("Crossbows can't have custom models");

        if (!(texture.get() instanceof CrossbowTextureValues)) {
            throw new ProgrammingValidationException("Only crossbow textures are allowed");
        }
    }
}
