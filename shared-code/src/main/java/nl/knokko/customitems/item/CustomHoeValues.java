package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class CustomHoeValues extends CustomToolValues {

    static CustomHoeValues load(
            BitInput input, byte encoding, SItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        CustomHoeValues result = new CustomHoeValues(false);

        if (encoding == ItemEncoding.ENCODING_HOE_6) {
            result.load6(input, itemSet);
            result.initDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_HOE_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_HOE_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomHoe", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private int tillDurabilityLoss;

    public CustomHoeValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);
    }

    CustomHoeValues(CustomToolValues toCopy, int tillDurabilityLoss, boolean mutable) {
        super(toCopy, mutable);

        this.tillDurabilityLoss = tillDurabilityLoss;
    }

    public CustomHoeValues(CustomHoeValues toCopy, boolean mutable) {
        this(toCopy, toCopy.getTillDurabilityLoss(), mutable);
    }

    private void load6(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTool6(input, itemSet);
        this.tillDurabilityLoss = input.readInt();
    }

    private void load9(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.tillDurabilityLoss = input.readInt();
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_HOE_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void save10(BitOutput output) {
        saveIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveToolOnlyPropertiesA4(output);
        saveItemFlags6(output);
        saveToolOnlyPropertiesB6(output);
        output.addInt(tillDurabilityLoss);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void initDefaults6() {
        initToolDefaults6();
        initHoeOnlyDefaults6();
    }

    private void initHoeOnlyDefaults6() {
        initHoeOnlyDefaults9();
        // No new hoe-only properties were introduced in encoding 9
    }

    private void initDefaults9() {
        initToolDefaults9();
        initHoeOnlyDefaults9();
    }

    private void initHoeOnlyDefaults9() {
        initHoeOnlyDefaults10();
        // No new hoe-only properties were introduced in encoding 10
    }

    private void initDefaults10() {
        initToolDefaults10();
        initHoeOnlyDefaults10();
    }

    private void initHoeOnlyDefaults10() {
        // There is nothing to be done until the next encoding is known
    }

    protected boolean areHoePropertiesEqual(CustomHoeValues other) {
        return areToolPropertiesEqual(other) && this.tillDurabilityLoss == other.tillDurabilityLoss;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomHoeValues.class && areHoePropertiesEqual((CustomHoeValues) other);
    }

    @Override
    public CustomHoeValues copy(boolean mutable) {
        return new CustomHoeValues(this, mutable);
    }

    public int getTillDurabilityLoss() {
        return tillDurabilityLoss;
    }

    public void setTillDurabilityLoss(int newDurabilityLoss) {
        this.tillDurabilityLoss = newDurabilityLoss;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (tillDurabilityLoss < 0) throw new ValidationException("Till durability loss can't be negative");
    }
}
