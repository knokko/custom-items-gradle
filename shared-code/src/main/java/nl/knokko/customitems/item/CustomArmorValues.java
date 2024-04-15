package nl.knokko.customitems.item;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.FancyPantsArmorTextureReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.ArmorTextureValues;
import nl.knokko.customitems.texture.FancyPantsArmorTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;
import java.util.UUID;

public class CustomArmorValues extends CustomToolValues {

    static CustomArmorValues load(
            BitInput input, byte encoding, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        // Note: Initial item type will be overwritten during loading, so its value doesn't matter
        CustomArmorValues result = new CustomArmorValues(false, CustomItemType.IRON_HELMET);

        if (encoding == ItemEncoding.ENCODING_ARMOR_4) {
            result.load4(input, itemSet);
            result.initDefaults4();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_6) {
            result.load6(input, itemSet);
            result.initDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_7) {
            result.load7(input, itemSet);
            result.initDefaults7();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_8) {
            result.load8(input, itemSet);
            result.initDefaults8();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_11) {
            result.load11(input, itemSet);
            result.initDefaults11();
        } else if (encoding == ItemEncoding.ENCODING_ARMOR_12) {
            result.loadArmorPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomArmor", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private int red, green, blue;

    private DamageResistanceValues damageResistances;
    private ArmorTextureReference armorTexture;
    private FancyPantsArmorTextureReference fancyPantsTexture;

    public CustomArmorValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable, initialItemType);

        this.red = 160;
        this.green = 101;
        this.blue = 64;
        this.damageResistances = new DamageResistanceValues(false);
        this.armorTexture = null;
        this.fancyPantsTexture = null;
    }

    public CustomArmorValues(CustomArmorValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.red = toCopy.getRed();
        this.green = toCopy.getGreen();
        this.blue = toCopy.getBlue();
        this.damageResistances = toCopy.getDamageResistances();
        this.armorTexture = toCopy.getArmorTextureReference();
        this.fancyPantsTexture = toCopy.getFancyPantsTextureReference();
    }

    protected void loadArmorPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadToolPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomArmorNew", encoding);

        this.loadLeatherColors(input);
        this.damageResistances = DamageResistanceValues.loadNew(input, itemSet);
        if (itemSet.getSide() == ItemSet.Side.EDITOR && input.readBoolean()) {
            this.armorTexture = itemSet.armorTextures.getReference(input.readString());
        } else {
            this.armorTexture = null;
        }

        if (encoding >= 2 && input.readBoolean()) {
            this.fancyPantsTexture = itemSet.fancyPants.getReference(new UUID(input.readLong(), input.readLong()));
        } else this.fancyPantsTexture = null;
    }

    protected void saveArmorPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveToolPropertiesNew(output, targetSide);

        output.addByte((byte) 2);
        if (this.itemType.isLeatherArmor()) {
            output.addBytes((byte) this.red, (byte) this.green, (byte) this.blue);
        }
        this.damageResistances.saveNew(output);
        if (targetSide == ItemSet.Side.EDITOR) {
            output.addBoolean(this.armorTexture != null);
            if (this.armorTexture != null) {
                output.addString(this.armorTexture.get().getName());
            }
        }

        output.addBoolean(this.fancyPantsTexture != null);
        if (this.fancyPantsTexture != null) {
            output.addLong(this.fancyPantsTexture.get().getId().getMostSignificantBits());
            output.addLong(this.fancyPantsTexture.get().getId().getLeastSignificantBits());
        }
    }

    private void loadLeatherColors(BitInput input) {
        if (itemType.isLeatherArmor()) {
            this.red = input.readByte() & 0xFF;
            this.green = input.readByte() & 0xFF;
            this.blue = input.readByte() & 0xFF;
        } else {
            this.red = 160;
            this.green = 101;
            this.blue = 64;
        }
    }

    private void load4(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadTool4(input, itemSet);
        loadLeatherColors(input);
    }

    private void load6(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
    }

    private void load7(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        this.damageResistances = DamageResistanceValues.load12(input, itemSet);
    }

    private void load8(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        this.damageResistances = DamageResistanceValues.load14(input, itemSet);
    }

    private void load9(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load8(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void loadPre10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadLeatherColors(input);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
    }

    private void loadPost10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        this.extraItemNbt = LegacyItemNbt.load(input);
        if (input.readBoolean()) {
            String armorTextureName = input.readString();
            if (itemSet.getSide() == ItemSet.Side.EDITOR) {
                this.armorTexture = itemSet.armorTextures.getReference(armorTextureName);
            } else {
                this.armorTexture = null;
            }
        } else {
            this.armorTexture = null;
        }
        this.attackRange = input.readFloat();
    }

    protected void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadPre10(input, itemSet);
        this.damageResistances = DamageResistanceValues.load14(input, itemSet);
        loadPost10(input, itemSet);
    }

    protected void load11(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadPre10(input, itemSet);
        this.damageResistances = DamageResistanceValues.load17(input, itemSet);
        loadPost10(input, itemSet);
    }

    protected boolean areArmorPropertiesEqual(CustomArmorValues other) {
        return areToolPropertiesEqual(other) && (!itemType.isLeatherArmor() || (
                this.red == other.red && this.green == other.green && this.blue == other.blue))
                && this.damageResistances.equals(other.damageResistances)
                && Objects.equals(this.fancyPantsTexture, other.fancyPantsTexture);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomArmorValues.class && areArmorPropertiesEqual((CustomArmorValues) other);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_ARMOR_12);
        saveArmorPropertiesNew(output, side);
    }

    private void initDefaults4() {
        initToolDefaults4();
        initArmorOnlyDefaults4();
    }

    private void initArmorOnlyDefaults4() {
        initArmorOnlyDefaults6();
        // No armor-only properties were introduced in encoding 6
    }

    private void initDefaults6() {
        initToolDefaults6();
        initArmorOnlyDefaults6();
    }

    private void initArmorOnlyDefaults6() {
        initArmorOnlyDefaults7();
        this.damageResistances = new DamageResistanceValues(false);
    }

    private void initDefaults7() {
        initToolDefaults8();
        initArmorOnlyDefaults7();
    }

    private void initArmorOnlyDefaults7() {
        initArmorOnlyDefaults8();
        // In encoding 8, damage resistances received a minecraft update, but this is already taken care off
    }

    private void initDefaults8() {
        initToolDefaults8();
        initArmorOnlyDefaults8();
    }

    private void initArmorOnlyDefaults8() {
        initArmorOnlyDefaults9();
        // No new armor-only properties were introduced in encoding 9
    }

    private void initDefaults9() {
        initToolDefaults9();
        initArmorOnlyDefaults9();
    }

    private void initArmorOnlyDefaults9() {
        initArmorOnlyDefaults10();
        // No new armor-only properties were introduced in encoding 10
    }

    protected void initDefaults10() {
        initToolDefaults10();
        initArmorOnlyDefaults10();
    }

    private void initArmorOnlyDefaults10() {
        initArmorOnlyDefaults11();
        // Minecraft 1.17 damage sources were added, but this is already taken care off in (S)DamageResistances
    }

    protected void initDefaults11() {
        // Call initToolDefaultsXX once it is made
        initArmorOnlyDefaults11();
    }

    private void initArmorOnlyDefaults11() {
        this.fancyPantsTexture = null;
    }

    @Override
    public CustomArmorValues copy(boolean mutable) {
        return new CustomArmorValues(this, mutable);
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public DamageResistanceValues getDamageResistances() {
        return damageResistances;
    }

    public ArmorTextureReference getArmorTextureReference() {
        return armorTexture;
    }

    public ArmorTextureValues getArmorTexture() {
        return armorTexture != null ? armorTexture.get() : null;
    }

    public FancyPantsArmorTextureReference getFancyPantsTextureReference() {
        return fancyPantsTexture;
    }

    public FancyPantsArmorTextureValues getFancyPantsTexture() {
        return fancyPantsTexture == null ? null : fancyPantsTexture.get();
    }

    public void setRed(int newRed) {
        assertMutable();
        this.red = newRed;
    }

    public void setGreen(int newGreen) {
        assertMutable();
        this.green = newGreen;
    }

    public void setBlue(int newBlue) {
        assertMutable();
        this.blue = newBlue;
    }

    public void setDamageResistances(DamageResistanceValues newDamageResistances) {
        assertMutable();
        this.damageResistances = newDamageResistances.copy(false);
    }

    public void setArmorTexture(ArmorTextureReference newArmorTexture) {
        assertMutable();
        this.armorTexture = newArmorTexture;
    }

    public void setFancyPantsTexture(FancyPantsArmorTextureReference newFpTexture) {
        assertMutable();
        this.fancyPantsTexture = newFpTexture;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (red < 0) throw new ValidationException("Red can't be negative");
        if (red > 255) throw new ValidationException("Red can be at most 255");
        if (green < 0) throw new ValidationException("Green can't be negative");
        if (green > 255) throw new ValidationException("Green can be at most 255");
        if (blue < 0) throw new ValidationException("Blue can't be negative");
        if (blue > 255) throw new ValidationException("Blue can be at most 255");

        if (damageResistances == null) throw new ProgrammingValidationException("No damage resistances");
        if (fancyPantsTexture != null && !itemType.isLeatherArmor()) {
            throw new ValidationException("FancyPants armor only works on leather armor");
        }
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (armorTexture != null && !itemSet.armorTextures.isValid(armorTexture)) {
            throw new ProgrammingValidationException("Armor texture is no longer valid");
        }
        if (fancyPantsTexture != null && !itemSet.fancyPants.isValid(fancyPantsTexture)) {
            throw new ProgrammingValidationException("FP texture is no longer valid");
        }

        Validation.scope("Damage resistances", damageResistances::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);

        for (DamageSource damageSource : DamageSource.values()) {
            if (damageResistances.getResistance(damageSource) != 0) {
                if (version < damageSource.firstVersion) {
                    throw new ValidationException(damageSource + " doesn't exist yet in mc " + MCVersions.createString(version));
                }
                if (version > damageSource.lastVersion) {
                    throw new ValidationException(damageSource + " doesn't exist anymore in mc " + MCVersions.createString(version));
                }
            }
        }
    }
}
