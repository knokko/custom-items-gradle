package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Arrays;

public class CustomTridentValues extends CustomToolValues {

    static CustomTridentValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        CustomTridentValues result = new CustomTridentValues(false);

        if (encoding == ItemEncoding.ENCODING_TRIDENT_8) {
            result.load8(input, itemSet);
            result.initTridentDefaults8();
        } else if (encoding == ItemEncoding.ENCODING_TRIDENT_9) {
            result.load9(input, itemSet);
            result.initTridentDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_TRIDENT_10) {
            result.load10(input, itemSet);
            result.initTridentDefaults10();
        } else {
            throw new UnknownEncodingException("CustomTrident", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadTridentEditorOnlyProperties8(input, itemSet);
        }

        return result;
    }

    private int throwDurabilityLoss;

    private double throwDamageMultiplier;
    private double throwSpeedMultiplier;

    private byte[] customInHandModel;
    private byte[] customThrowingModel;

    public CustomTridentValues(boolean mutable) {
        super(mutable, CustomItemType.TRIDENT);

        this.throwDurabilityLoss = 1;

        this.throwDamageMultiplier = 1.0;
        this.throwSpeedMultiplier = 1.0;

        this.customInHandModel = null;
        this.customThrowingModel = null;
    }

    public CustomTridentValues(CustomTridentValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.throwDurabilityLoss = toCopy.getThrowDurabilityLoss();
        this.throwDamageMultiplier = toCopy.getThrowDamageMultiplier();
        this.throwSpeedMultiplier = toCopy.getThrowSpeedMultiplier();
        this.customInHandModel = toCopy.getCustomInHandModel();
        this.customThrowingModel = toCopy.getCustomThrowingModel();
    }

    private void loadTridentEditorOnlyProperties8(BitInput input, SItemSet itemSet) {
        loadEditorOnlyProperties1(input, itemSet, true);

        if (input.readBoolean()) {
            customInHandModel = input.readByteArray();
        } else {
            customInHandModel = null;
        }

        if (input.readBoolean()) {
            customThrowingModel = input.readByteArray();
        } else {
            customThrowingModel = null;
        }
    }

    private void load8(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTridentIdentityProperties8(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        loadTridentOnlyProperties8(input);
    }

    private void load9(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load8(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTridentIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        loadTridentOnlyProperties8(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    private void initTridentDefaults10() {
        initToolDefaults10();
        initTridentOnlyDefaults10();
    }

    private void initTridentOnlyDefaults10() {
        // Nothing to be done until the next encoding
    }

    private void initTridentDefaults9() {
        initToolDefaults9();
        initTridentOnlyDefaults9();
    }

    private void initTridentOnlyDefaults9() {
        initTridentOnlyDefaults10();
    }

    private void initTridentDefaults8() {
        initToolDefaults8();
        initTridentOnlyDefaults8();
    }

    private void initTridentOnlyDefaults8() {
        initTridentOnlyDefaults9();
    }

    private void loadTridentIdentityProperties8(BitInput input) {
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
    }

    private void loadTridentIdentityProperties10(BitInput input) {
        loadTridentIdentityProperties8(input);
        this.alias = input.readString();
    }

    private void loadTridentOnlyProperties8(BitInput input) {
        this.throwDurabilityLoss = input.readInt();
        this.throwDamageMultiplier = input.readDouble();
        this.throwSpeedMultiplier = input.readDouble();
    }

    @Override
    public CustomTridentValues copy(boolean mutable) {
        return new CustomTridentValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_TRIDENT_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveTridentEditorOnlyProperties8(output);
        }
    }

    private void saveTridentEditorOnlyProperties8(BitOutput output) {
        saveEditorOnlyProperties1(output);
        output.addBoolean(customInHandModel != null);
        if (customInHandModel != null) {
            output.addByteArray(customInHandModel);
        }
        output.addBoolean(customThrowingModel != null);
        if (customThrowingModel != null) {
            output.addByteArray(customThrowingModel);
        }
    }

    private void save10(BitOutput output) {
        saveTridentIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveToolOnlyPropertiesA4(output);
        saveItemFlags6(output);
        saveToolOnlyPropertiesB6(output);
        saveTridentOnlyProperties8(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void saveTridentIdentityProperties10(BitOutput output) {
        output.addShort(itemDamage);
        output.addJavaString(name);
        output.addString(alias);
    }

    private void saveTridentOnlyProperties8(BitOutput output) {
        output.addInt(throwDurabilityLoss);
        output.addDoubles(throwDamageMultiplier, throwSpeedMultiplier);
    }

    public int getThrowDurabilityLoss() {
        return throwDurabilityLoss;
    }

    public double getThrowDamageMultiplier() {
        return throwDamageMultiplier;
    }

    public double getThrowSpeedMultiplier() {
        return throwSpeedMultiplier;
    }

    public byte[] getCustomInHandModel() {
        return CollectionHelper.arrayCopy(customInHandModel);
    }

    public byte[] getCustomThrowingModel() {
        return CollectionHelper.arrayCopy(customThrowingModel);
    }

    public void setThrowDurabilityLoss(int newThrowDurabilityLoss) {
        assertMutable();
        this.throwDurabilityLoss = newThrowDurabilityLoss;
    }

    public void setThrowDamageMultiplier(double newThrowDamageMultiplier) {
        assertMutable();
        this.throwDamageMultiplier = newThrowDamageMultiplier;
    }

    public void setThrowSpeedMultiplier(double newThrowSpeedMultiplier) {
        assertMutable();
        this.throwSpeedMultiplier = newThrowSpeedMultiplier;
    }

    public void setCustomInHandModel(byte[] newInHandModel) {
        assertMutable();
        this.customInHandModel = CollectionHelper.arrayCopy(newInHandModel);
    }

    public void setCustomThrowingModel(byte[] newThrowingModel) {
        assertMutable();
        this.customThrowingModel = CollectionHelper.arrayCopy(newThrowingModel);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (throwDurabilityLoss < 0) throw new ValidationException("Durability loss on throwing can't be negative");
        if (throwDamageMultiplier < 0) throw new ValidationException("Throw damage multiplier can't be negative");
        if (throwSpeedMultiplier < 0) throw new ValidationException("Throw speed multiplier can't be negative");
    }
}
