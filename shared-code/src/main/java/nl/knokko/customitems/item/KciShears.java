package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class KciShears extends KciTool {

    static KciShears load(
            BitInput input, byte encoding, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        KciShears result = new KciShears(false);

        if (encoding == ItemEncoding.ENCODING_SHEAR_6) {
            result.load6(input, itemSet);
            result.initDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_SHEAR_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_SHEAR_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_SHEARS_12) {
            result.loadShearsPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomShears", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return result;
    }

    private int shearDurabilityLoss;

    public KciShears(boolean mutable) {
        super(mutable, KciItemType.SHEARS);
    }

    KciShears(KciTool toCopy, int shearDurabilityLoss, boolean mutable) {
        super(toCopy, mutable);

        this.shearDurabilityLoss = shearDurabilityLoss;
    }

    public KciShears(KciShears toCopy, boolean mutable) {
        this(toCopy, toCopy.getShearDurabilityLoss(), mutable);
    }

    protected void loadShearsPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadToolPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomShearsNew", encoding);

        this.shearDurabilityLoss = input.readInt();
    }

    protected void saveShearsPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveToolPropertiesNew(output, targetSide);

        output.addByte((byte) 1);

        output.addInt(this.shearDurabilityLoss);
    }

    private void load6(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadTool6(input, itemSet);
        this.shearDurabilityLoss = input.readInt();
    }

    private void load9(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load6(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void loadShearsIdentityProperties10(BitInput input) {
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
        this.alias = input.readString();
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadShearsIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        this.shearDurabilityLoss = input.readInt();
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    protected boolean areShearsPropertiesEqual(KciShears other) {
        return areToolPropertiesEqual(other) && this.shearDurabilityLoss == other.shearDurabilityLoss;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == KciShears.class && areShearsPropertiesEqual((KciShears) other);
    }

    @Override
    public KciShears copy(boolean mutable) {
        return new KciShears(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_SHEARS_12);
        saveShearsPropertiesNew(output, side);
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

    public int getShearDurabilityLoss() {
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
