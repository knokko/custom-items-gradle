package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.SIngredient;
import nl.knokko.customitems.texture.BowTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomBowValues extends CustomToolValues {

    static CustomBowValues load(
            BitInput input, byte encoding, SItemSet itemSet, boolean checkCustomModel
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
        } else {
            throw new UnknownEncodingException("CustomBow", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private double damageMultiplier, speedMultiplier;
    private int knockbackStrength;
    private boolean hasGravity;
    private int shootDurabilityLoss;

    public CustomBowValues(boolean mutable) {
        super(mutable, CustomItemType.BOW);

        this.damageMultiplier = 1.0;
        this.speedMultiplier = 1.0;
        this.knockbackStrength = 0;
        this.hasGravity = true;
        this.shootDurabilityLoss = 1;
    }

    public CustomBowValues(CustomBowValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.damageMultiplier = toCopy.getDamageMultiplier();
        this.speedMultiplier = toCopy.getSpeedMultiplier();
        this.knockbackStrength = toCopy.getKnockbackStrength();
        this.hasGravity = toCopy.hasGravity();
        this.shootDurabilityLoss = toCopy.getShootDurabilityLoss();
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_BOW_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void loadBowIdentityProperties3(BitInput input) {
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
    }

    private void loadBowIdentityProperties10(BitInput input) {
        loadBowIdentityProperties3(input);
        this.alias = input.readString();
    }

    private void loadBowPropertiesA3(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        int durability = input.readInt();
        if (durability == -1) {
            this.maxDurability = null;
        } else {
            this.maxDurability = (long) durability;
        }
        loadBowPropertiesPostA(input, itemSet);
    }

    private void loadBowPropertiesA4(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        long durability = input.readLong();
        if (durability == -1L) {
            this.maxDurability = null;
        } else {
            this.maxDurability = durability;
        }
        loadBowPropertiesPostA(input, itemSet);
    }

    private void loadBowPropertiesPostA(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.damageMultiplier = input.readDouble();
        this.speedMultiplier = input.readDouble();
        this.knockbackStrength = input.readInt();
        this.hasGravity = input.readBoolean();
        this.allowEnchanting = input.readBoolean();
        this.allowAnvilActions = input.readBoolean();
        this.repairItem = SIngredient.load(input, itemSet);
    }

    private void load3(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadBowIdentityProperties3(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers2(input);
        loadBowPropertiesA3(input, itemSet);
    }

    private void load4(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadBowIdentityProperties3(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadBowPropertiesA4(input, itemSet);
    }

    private void load6(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.shootDurabilityLoss = input.readInt();
    }

    private void load9(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
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

    private void save10(BitOutput output) {
        saveBowIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveBowPropertiesA4(output);
        saveItemFlags6(output);
        saveToolOnlyPropertiesB6(output);
        output.addInt(shootDurabilityLoss);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void saveBowIdentityProperties10(BitOutput output) {
        output.addShort(itemDamage);
        output.addJavaString(name);
        output.addString(alias);
    }

    private void saveBowPropertiesA4(BitOutput output) {
        if (maxDurability != null) {
            output.addLong(maxDurability);
        } else {
            output.addLong(-1L);
        }
        output.addDoubles(damageMultiplier, speedMultiplier);
        output.addInt(knockbackStrength);
        output.addBooleans(hasGravity, allowEnchanting, allowAnvilActions);
        repairItem.save(output);
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
        // Nothing to be done until the next encoding is known
    }

    @Override
    public CustomBowValues copy(boolean mutable) {
        return new CustomBowValues(this, mutable);
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

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (damageMultiplier < 0.0) throw new ValidationException("Damage multiplier can't be negative");
        // Note: having a negative speed multiplier or knockback strength is allowed
        if (shootDurabilityLoss < 0) throw new ValidationException("Shoot durability loss can't be negative");

        if (customModel != null) throw new ProgrammingValidationException("Bows can't have custom models");
    }

    @Override
    public void validateComplete(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!(texture.get() instanceof BowTextureValues)) throw new ProgrammingValidationException("Texture must be a bow texture");
    }
}
