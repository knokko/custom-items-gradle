package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ArmorTextureReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.texture.ArmorTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomArmorValues extends CustomToolValues {

    static CustomArmorValues load(
            BitInput input, byte encoding, SItemSet itemSet, boolean checkCustomModel
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
        } else {
            throw new UnknownEncodingException("CustomArmor", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private int red, green, blue;

    private SDamageResistances damageResistances;
    private ArmorTextureReference armorTexture;

    public CustomArmorValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable, initialItemType);

        this.red = 160;
        this.green = 101;
        this.blue = 64;
        this.damageResistances = new SDamageResistances(false);
        this.armorTexture = null;
    }

    public CustomArmorValues(CustomArmorValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.red = toCopy.getRed();
        this.green = toCopy.getGreen();
        this.blue = toCopy.getBlue();
        this.damageResistances = toCopy.getDamageResistances();
        this.armorTexture = toCopy.getArmorTextureReference();
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

    private void load4(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTool4(input, itemSet);
        loadLeatherColors(input);
    }

    private void load6(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
    }

    private void load7(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        this.damageResistances = SDamageResistances.load12(input);
    }

    private void load8(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        this.damageResistances = SDamageResistances.load14(input);
    }

    private void load9(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load8(input, itemSet);
        loadPotionProperties9(input);
        loadPotionProperties10(input);
    }

    private void loadPre10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadLeatherColors(input);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
    }

    private void loadPost10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        this.extraItemNbt = SExtraItemNbt.load(input, false);
        if (input.readBoolean()) {
            this.armorTexture = itemSet.getArmorTextureReference(input.readString());
        } else {
            this.armorTexture = null;
        }
        this.attackRange = input.readFloat();
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadPre10(input, itemSet);
        this.damageResistances = SDamageResistances.load14(input);
        loadPost10(input, itemSet);
    }

    private void load11(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadPre10(input, itemSet);
        this.damageResistances = SDamageResistances.load17(input);
        loadPost10(input, itemSet);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_ARMOR_11);
        saveArmor11(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    protected void saveArmor11(BitOutput output) {
        saveIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveToolOnlyPropertiesA4(output);
        if (itemType.isLeatherArmor()) {
            output.addBytes((byte) red, (byte) green, (byte) blue);
        }
        saveItemFlags6(output);
        saveToolOnlyPropertiesB6(output);
        damageResistances.save17(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        extraItemNbt.save(output);
        output.addBoolean(armorTexture != null);
        if (armorTexture != null) {
            output.addString(armorTexture.get().getName());
        }
        output.addFloat(attackRange);
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
        this.damageResistances = new SDamageResistances(false);
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

    private void initDefaults10() {
        initToolDefaults10();
        initArmorOnlyDefaults10();
    }

    private void initArmorOnlyDefaults10() {
        initArmorOnlyDefaults11();
        // Minecraft 1.17 damage sources were added, but this is already taken care off in (S)DamageResistances
    }

    private void initDefaults11() {
        // Call initToolDefaultsXX once it is made
        initArmorOnlyDefaults11();
    }

    private void initArmorOnlyDefaults11() {
        // Nothing to be done until the next encoding is known
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

    public SDamageResistances getDamageResistances() {
        return damageResistances;
    }

    public ArmorTextureReference getArmorTextureReference() {
        return armorTexture;
    }

    public ArmorTextureValues getArmorTexture() {
        return armorTexture != null ? armorTexture.get() : null;
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

    public void setDamageResistances(SDamageResistances newDamageResistances) {
        assertMutable();
        this.damageResistances = newDamageResistances.copy(false);
    }

    public void setArmorTexture(ArmorTextureReference newArmorTexture) {
        assertMutable();
        this.armorTexture = newArmorTexture;
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
    }

    @Override
    public void validateComplete(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (armorTexture != null && !itemSet.isReferenceValid(armorTexture)) {
            throw new ProgrammingValidationException("Armor texture is no longer valid");
        }
    }
}
