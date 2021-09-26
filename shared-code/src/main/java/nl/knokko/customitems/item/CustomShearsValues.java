package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomShearsValues extends CustomToolValues {

    static CustomShearsValues load(
            BitInput input, byte encoding, SItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        CustomShearsValues result = new CustomShearsValues(false);

        if (encoding == ItemEncoding.ENCODING_SHEAR_6) {
            result.load6(input, itemSet);
            result.initDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_SHEAR_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_SHEAR_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomShears", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private int shearDurabilityLoss;

    public CustomShearsValues(boolean mutable) {
        super(mutable, CustomItemType.SHEARS);
    }

    CustomShearsValues(CustomToolValues toCopy, int shearDurabilityLoss, boolean mutable) {
        super(toCopy, mutable);

        this.shearDurabilityLoss = shearDurabilityLoss;
    }

    public CustomShearsValues(CustomShearsValues toCopy, boolean mutable) {
        this(toCopy, toCopy.getShearDurabilityLoss(), mutable);
    }

    private void load6(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTool6(input, itemSet);
        this.shearDurabilityLoss = input.readInt();
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
        this.shearDurabilityLoss = input.readInt();
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    @Override
    public CustomShearsValues copy(boolean mutable) {
        return new CustomShearsValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_SHEAR_10);
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
        output.addInt(shearDurabilityLoss);
        saveItemFlags6(output);
        saveToolOnlyPropertiesB6(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void initDefaults6() {
        initToolDefaults6();
        initShearsOnlyDefaults6();
    }

    private void initShearsOnlyDefaults6() {
        initShearsOnlyDefaults9();
        // No new shears-only properties were introduced in encoding 9
    }

    private void initDefaults9() {
        initToolDefaults9();
        initShearsOnlyDefaults9();
    }

    private void initShearsOnlyDefaults9() {
        initShearsOnlyDefaults10();
        // No new shears-only properties were introduced in encoding 10
    }

    private void initDefaults10() {
        initToolDefaults10();
        initShearsOnlyDefaults10();
    }

    private void initShearsOnlyDefaults10() {
        // There is nothing to be done until the next encoding is known
    }

    private int getShearDurabilityLoss() {
        return shearDurabilityLoss;
    }

    public void setShearDurabilityLoss(int newDurabilityLoss) {
        this.shearDurabilityLoss = newDurabilityLoss;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (shearDurabilityLoss < 0) throw new ValidationException("Shear durability loss can't be negative");
    }
}
