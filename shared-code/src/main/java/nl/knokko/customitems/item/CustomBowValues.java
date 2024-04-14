package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.texture.BowTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;
import java.util.UUID;

import static nl.knokko.customitems.util.Checks.isClose;

public class CustomBowValues extends CustomToolValues {

    static CustomBowValues load(
            BitInput input, byte encoding, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        CustomBowValues result = new CustomBowValues(false);

        if (encoding == ItemEncoding.ENCODING_BOW_3) {
            result.load3(input, itemSet);
            result.initDefaults3();
        } else if (encoding == ItemEncoding.ENCODING_BOW_4) {
            result.load4(input, itemSet);
            result.initDefaults4();
        } else if (encoding == ItemEncoding.ENCODING_BOW_6) {
            result.load6(input, itemSet);
            result.initDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_BOW_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_BOW_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_BOW_12) {
            result.loadBowPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomBow", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private double damageMultiplier, speedMultiplier;
    private int knockbackStrength;
    private boolean hasGravity;
    private int shootDurabilityLoss;
    private CustomDamageSourceReference customShootDamageSource;

    public CustomBowValues(boolean mutable) {
        super(mutable, CustomItemType.BOW);

        this.damageMultiplier = 1.0;
        this.speedMultiplier = 1.0;
        this.knockbackStrength = 0;
        this.hasGravity = true;
        this.shootDurabilityLoss = 1;
        this.customShootDamageSource = null;
    }

    public CustomBowValues(CustomBowValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.damageMultiplier = toCopy.getDamageMultiplier();
        this.speedMultiplier = toCopy.getSpeedMultiplier();
        this.knockbackStrength = toCopy.getKnockbackStrength();
        this.hasGravity = toCopy.hasGravity();
        this.shootDurabilityLoss = toCopy.getShootDurabilityLoss();
        this.customShootDamageSource = toCopy.getCustomShootDamageSourceReference();
    }

    @Override
    public DefaultModelType getDefaultModelType() {
        return null;
    }

    protected void loadBowPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadToolPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomBowNew", encoding);

        this.damageMultiplier = input.readDouble();
        this.speedMultiplier = input.readDouble();
        this.knockbackStrength = input.readInt();
        this.hasGravity = input.readBoolean();
        this.shootDurabilityLoss = input.readInt();
        if (encoding >= 2) {
            if (input.readBoolean()) {
                this.customShootDamageSource = itemSet.damageSources.getReference(new UUID(input.readLong(), input.readLong()));
            } else this.customShootDamageSource = null;
        } else this.customShootDamageSource = null;
    }

    protected void saveBowPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveToolPropertiesNew(output, targetSide);

        output.addByte((byte) 2);

        output.addDouble(this.damageMultiplier);
        output.addDouble(this.speedMultiplier);
        output.addInt(this.knockbackStrength);
        output.addBoolean(this.hasGravity);
        output.addInt(this.shootDurabilityLoss);
        output.addBoolean(this.customShootDamageSource != null);
        if (customShootDamageSource != null) {
            output.addLong(customShootDamageSource.get().getId().getMostSignificantBits());
            output.addLong(customShootDamageSource.get().getId().getLeastSignificantBits());
        }
    }

    protected boolean areBowPropertiesEqual(CustomBowValues other) {
        return areToolPropertiesEqual(other) && isClose(this.damageMultiplier, other.damageMultiplier)
                && isClose(this.speedMultiplier, other.speedMultiplier) && this.knockbackStrength == other.knockbackStrength
                && this.hasGravity == other.hasGravity && this.shootDurabilityLoss == other.shootDurabilityLoss
                && Objects.equals(this.customShootDamageSource, other.customShootDamageSource);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomBowValues.class && areBowPropertiesEqual((CustomBowValues) other);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_BOW_12);
        this.saveBowPropertiesNew(output, side);
    }

    private void loadBowIdentityProperties3(BitInput input) {
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
    }

    private void loadBowIdentityProperties10(BitInput input) {
        loadBowIdentityProperties3(input);
        this.alias = input.readString();
    }

    private void loadBowPropertiesA3(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        int durability = input.readInt();
        if (durability == -1) {
            this.maxDurability = null;
        } else {
            this.maxDurability = (long) durability;
        }
        loadBowPropertiesPostA(input, itemSet);
    }

    private void loadBowPropertiesA4(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        long durability = input.readLong();
        if (durability == -1L) {
            this.maxDurability = null;
        } else {
            this.maxDurability = durability;
        }
        loadBowPropertiesPostA(input, itemSet);
    }

    private void loadBowPropertiesPostA(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.damageMultiplier = input.readDouble();
        this.speedMultiplier = input.readDouble();
        this.knockbackStrength = input.readInt();
        this.hasGravity = input.readBoolean();
        this.allowEnchanting = input.readBoolean();
        this.allowAnvilActions = input.readBoolean();
        this.repairItem = IngredientValues.load(input, itemSet);
    }

    private void load3(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBowIdentityProperties3(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers2(input);
        loadBowPropertiesA3(input, itemSet);
    }

    private void load4(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBowIdentityProperties3(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadBowPropertiesA4(input, itemSet);
    }

    private void load6(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.shootDurabilityLoss = input.readInt();
    }

    private void load9(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBowIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadBowPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.shootDurabilityLoss = input.readInt();
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    private void initDefaults3() {
        initToolDefaults3();
        initBowOnlyDefaults3();
    }

    private void initBowOnlyDefaults3() {
        initBowOnlyDefaults4();
        // No bow-only properties were introduced in encoding 4
    }

    private void initDefaults4() {
        initToolDefaults4();
        initBowOnlyDefaults4();
    }

    private void initBowOnlyDefaults4() {
        initBowOnlyDefaults6();
        this.shootDurabilityLoss = 1;
    }

    private void initDefaults6() {
        initToolDefaults6();
        initBowOnlyDefaults6();
    }

    private void initBowOnlyDefaults6() {
        initBowOnlyDefaults9();
        // No bow-only properties were introduced in encoding 9
    }

    private void initDefaults9() {
        initToolDefaults9();
        initBowOnlyDefaults9();
    }

    private void initBowOnlyDefaults9() {
        initBowOnlyDefaults10();
        // No bow-only properties were introduced in encoding 10
    }

    private void initDefaults10() {
        initToolDefaults10();
        initBowOnlyDefaults10();
    }

    private void initBowOnlyDefaults10() {
        this.customShootDamageSource = null;
        initBowOnlyDefaults11();
    }

    private void initBowOnlyDefaults11() {
        // Nothing to be done until the next encoding is known
    }

    @Override
    public CustomBowValues copy(boolean mutable) {
        return new CustomBowValues(this, mutable);
    }

    @Override
    public BowTextureValues getTexture() {
        return (BowTextureValues) super.getTexture();
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public int getKnockbackStrength() {
        return knockbackStrength;
    }

    public boolean hasGravity() {
        return hasGravity;
    }

    public int getShootDurabilityLoss() {
        return shootDurabilityLoss;
    }

    public CustomDamageSourceReference getCustomShootDamageSourceReference() {
        return customShootDamageSource;
    }

    @Override
    public void setTexture(TextureReference newTexture) {
        if (!(newTexture.get() instanceof BowTextureValues)) {
            throw new IllegalArgumentException("Only bow textures are allowed");
        }
        super.setTexture(newTexture);
    }

    public void setDamageMultiplier(double newDamageMultiplier) {
        assertMutable();
        this.damageMultiplier = newDamageMultiplier;
    }

    public void setSpeedMultiplier(double newSpeedMultiplier) {
        assertMutable();
        this.speedMultiplier = newSpeedMultiplier;
    }

    public void setKnockbackStrength(int newKnockbackStrength) {
        assertMutable();
        this.knockbackStrength = newKnockbackStrength;
    }

    public void setGravity(boolean shouldHaveGravity) {
        assertMutable();
        this.hasGravity = shouldHaveGravity;
    }

    public void setShootDurabilityLoss(int newShootDurabilityLoss) {
        assertMutable();
        this.shootDurabilityLoss = newShootDurabilityLoss;
    }

    public void setCustomShootDamageSource(CustomDamageSourceReference newDamageSource) {
        assertMutable();
        this.customShootDamageSource = newDamageSource;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (!(texture.get() instanceof BowTextureValues)) {
            throw new ProgrammingValidationException("Texture must be a bow texture");
        }

        if (damageMultiplier < 0.0) throw new ValidationException("Damage multiplier can't be negative");
        // Note: having a negative speed multiplier or knockback strength is allowed
        if (shootDurabilityLoss < 0) throw new ValidationException("Shoot durability loss can't be negative");

        if (model != null) throw new ProgrammingValidationException("Bows can't have custom models");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!(texture.get() instanceof BowTextureValues)) throw new ProgrammingValidationException("Texture must be a bow texture");
        if (customShootDamageSource != null && !itemSet.damageSources.isValid(customShootDamageSource)) {
            throw new ProgrammingValidationException("Custom shoot damage source is invalid");
        }
    }
}
