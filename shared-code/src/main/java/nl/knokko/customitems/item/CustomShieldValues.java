package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import static nl.knokko.customitems.util.Checks.isClose;

public class CustomShieldValues extends CustomToolValues {

    static CustomShieldValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        CustomShieldValues result = new CustomShieldValues(false);

        if (encoding == ItemEncoding.ENCODING_SHIELD_7) {
            result.load7(input, itemSet);
            result.initDefaults7();
        } else if (encoding == ItemEncoding.ENCODING_SHIELD_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_SHIELD_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomShield", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadShieldEditorOnlyProperties7(input, itemSet);
        }

        return result;
    }

    private double thresholdDamage;
    private byte[] customBlockingModel;

    public CustomShieldValues(boolean mutable) {
        super(mutable, CustomItemType.SHIELD);

        this.thresholdDamage = 4.0;
        this.customBlockingModel = null;
    }

    public CustomShieldValues(CustomShieldValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.thresholdDamage = toCopy.getThresholdDamage();
        this.customBlockingModel = toCopy.getCustomBlockingModel();
    }

    private void load7(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.thresholdDamage = input.readDouble();
    }

    private void load9(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load7(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void loadShieldEditorOnlyProperties10(BitInput input) {
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
        this.alias = input.readString();
    }

    private void saveShieldIdentityProperties10(BitOutput output) {
        output.addShort(itemDamage);
        output.addJavaString(name);
        output.addString(alias);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_SHIELD_10);
        save10(output);

        if (side == ItemSet.Side.EDITOR) {
            saveShieldEditorOnlyProperties7(output);
        }
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadShieldEditorOnlyProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.thresholdDamage = input.readDouble();
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    private void save10(BitOutput output) {
        saveShieldIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveToolOnlyPropertiesA4(output);
        saveItemFlags6(output);
        saveToolOnlyPropertiesB6(output);
        output.addDouble(thresholdDamage);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void loadShieldEditorOnlyProperties7(BitInput input, ItemSet itemSet) {
        loadEditorOnlyProperties1(input, itemSet, true);
        if (input.readBoolean()) {
            this.customBlockingModel = input.readByteArray();
        } else {
            this.customBlockingModel = null;
        }
    }

    private void saveShieldEditorOnlyProperties7(BitOutput output) {
        saveEditorOnlyProperties1(output);
        output.addBoolean(customBlockingModel != null);
        if (customBlockingModel != null) {
            output.addByteArray(customBlockingModel);
        }
    }

    private void initDefaults7() {
        initToolDefaults8();
        initShieldOnlyDefaults7();
    }

    private void initShieldOnlyDefaults7() {
        initShieldOnlyDefaults9();
        // No new shield-only properties were introduced in encoding 9
    }

    private void initDefaults9() {
        initToolDefaults9();
        initShieldOnlyDefaults9();
    }

    private void initShieldOnlyDefaults9() {
        initShieldOnlyDefaults10();
        // No new shield-only properties were introduced in encoding 10
    }

    private void initDefaults10() {
        initToolDefaults10();
        initShieldOnlyDefaults10();
    }

    private void initShieldOnlyDefaults10() {
        // There is nothing to be done until the next encoding is known
    }

    protected boolean areShieldPropertiesEqual(CustomShieldValues other) {
        return areToolPropertiesEqual(other) && isClose(this.thresholdDamage, other.thresholdDamage);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomShieldValues.class && areShieldPropertiesEqual((CustomShieldValues) other);
    }

    @Override
    public CustomShieldValues copy(boolean mutable) {
        return new CustomShieldValues(this, mutable);
    }

    public double getThresholdDamage() {
        return thresholdDamage;
    }

    public byte[] getCustomBlockingModel() {
        return CollectionHelper.arrayCopy(customBlockingModel);
    }

    public void setThresholdDamage(double newThresholdDamage) {
        assertMutable();
        this.thresholdDamage = newThresholdDamage;
    }

    public void setCustomBlockingModel(byte[] newBlockingModel) {
        assertMutable();
        this.customBlockingModel = CollectionHelper.arrayCopy(newBlockingModel);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (thresholdDamage < 0.0) throw new ValidationException("Threshold damage can't be negative");
    }
}
