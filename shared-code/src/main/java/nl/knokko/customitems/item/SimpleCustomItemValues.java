package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleCustomItemValues extends CustomItemValues {

    public static SimpleCustomItemValues load(
            BitInput input, byte encoding, SItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        SimpleCustomItemValues simpleItem = new SimpleCustomItemValues(false);

        if (encoding == ItemEncoding.ENCODING_SIMPLE_1) {
            simpleItem.load1(input);
            simpleItem.initDefaults1();
        } else if (encoding == ItemEncoding.ENCODING_SIMPLE_2) {
            simpleItem.load2(input);
            simpleItem.initDefaults2();
        } else if (encoding == ItemEncoding.ENCODING_SIMPLE_4) {
            simpleItem.load4(input);
            simpleItem.initDefaults4();
        } else if (encoding == ItemEncoding.ENCODING_SIMPLE_5) {
            simpleItem.load5(input);
            simpleItem.initDefaults5();
        } else if (encoding == ItemEncoding.ENCODING_SIMPLE_6) {
            simpleItem.load6(input);
            simpleItem.initDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_SIMPLE_9) {
            simpleItem.load9(input);
            simpleItem.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_SIMPLE_10) {
            simpleItem.load10(input, itemSet);
            simpleItem.initDefaults10();
        } else {
            throw new UnknownEncodingException("SimpleCustomItemValues", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            simpleItem.loadEditorOnlyProperties1(input, itemSet, checkCustomModel);
        }

        return simpleItem;
    }

    private byte maxStacksize;

    public SimpleCustomItemValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.maxStacksize = 64;
    }

    public SimpleCustomItemValues(SimpleCustomItemValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.maxStacksize = toCopy.getMaxStacksize();
    }

    private void load1(BitInput input) {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
    }

    private void load2(BitInput input) {
        load1(input);
        loadAttributeModifiers2(input);
    }

    private void load4(BitInput input) {
        load2(input);
        loadDefaultEnchantments4(input);
    }

    private void load5(BitInput input) {
        load4(input);
        this.maxStacksize = input.readByte();
    }

    private void load6(BitInput input) {
        load5(input);
        loadItemFlags6(input);
    }

    private void load9(BitInput input) {
        load6(input);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        this.maxStacksize = input.readByte();
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_SIMPLE_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void save10(BitOutput output) {
        saveIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        output.addByte(maxStacksize);
        saveItemFlags6(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void initSimpleOnlyDefaults10() {
        // Nothing to be done until the next encoding is made
    }

    private void initDefaults10() {
        initBaseDefaults10();
        initSimpleOnlyDefaults10();
    }

    private void initSimpleOnlyDefaults9() {
        initSimpleOnlyDefaults10();

        // No simple-only properties were introduced in encoding 10
    }

    private void initDefaults9() {
        initBaseDefaults9();
        initSimpleOnlyDefaults9();
    }

    private void initSimpleOnlyDefaults6() {
        initSimpleOnlyDefaults9();

        // No simple-only properties were introduced in encoding 9
    }

    private void initDefaults6() {
        initBaseDefaults6();
        initSimpleOnlyDefaults6();
    }

    private void initSimpleOnlyDefaults5() {
        initSimpleOnlyDefaults6();

        // No simple-only properties were introduced in encoding 6
    }

    private void initDefaults5() {
        initBaseDefaults5();
        initSimpleOnlyDefaults5();
    }

    private void initSimpleOnlyDefaults4() {
        initSimpleOnlyDefaults5();

        this.maxStacksize = 64;
    }

    private void initDefaults4() {
        initBaseDefaults4();
        initSimpleOnlyDefaults4();
    }

    private void initSimpleOnlyDefaults2() {
        initSimpleOnlyDefaults4();

        // No simple-only properties were added in encoding 3
    }

    private void initDefaults2() {
        initBaseDefaults2();
        initSimpleOnlyDefaults2();
    }

    private void initSimpleOnlyDefaults1() {
        initSimpleOnlyDefaults2();

        // No simple-only properties were added in encoding 2
    }

    private void initDefaults1() {
        initBaseDefaults1();
        initSimpleOnlyDefaults1();
    }

    @Override
    public SimpleCustomItemValues copy(boolean mutable) {
        return new SimpleCustomItemValues(this, mutable);
    }

    @Override
    public byte getMaxStacksize() {
        return maxStacksize;
    }

    public void setMaxStacksize(byte newStacksize) {
        assertMutable();
        this.maxStacksize = newStacksize;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (maxStacksize < 1) throw new ValidationException("Maximum stacksize must be positive");
        if (maxStacksize > 64) throw new ValidationException("Maximum stacksize can be at most 64");
    }
}
