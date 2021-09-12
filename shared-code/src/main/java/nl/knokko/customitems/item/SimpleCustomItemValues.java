package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;

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

    private int maxStacksize;

    public SimpleCustomItemValues(boolean mutable) {
        super(mutable);

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
    public void save(BitOutput output) {
        output.addByte(ItemEncoding.ENCODING_SIMPLE_10);
        save10(output);
    }

    private void save10(BitOutput output) {
        saveIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        output.addByte((byte) maxStacksize);
        saveItemFlags6(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    private void initDefaults10() {
        // Nothing to be done until the next encoding is made
    }

    private void initDefaults9() {
        initDefaults10();

        this.alias = "";

        this.equippedEffects = new ArrayList<>();

        this.replaceConditions = new ArrayList<>();
        this.conditionOp = SReplaceCondition.ConditionOperation.NONE;

        this.extraItemNbt = new SExtraItemNbt(false);
        this.attackRange = 1f;
    }

    private void initDefaults6() {
        initDefaults9();

        this.playerEffects = new ArrayList<>();
        this.targetEffects = new ArrayList<>();

        this.commands = new ArrayList<>();
    }

    private void initDefaults5() {
        initDefaults6();

        this.itemFlags = ItemFlag.getDefaultValuesList();
    }

    private void initDefaults4() {
        initDefaults5();

        this.maxStacksize = 64;
    }

    private void initDefaults2() {
        initDefaults4();

        this.defaultEnchantments = new ArrayList<>();
    }

    private void initDefaults1() {
        initDefaults2();

        this.attributeModifiers = new ArrayList<>();
    }

    @Override
    public SimpleCustomItemValues copy(boolean mutable) {
        return new SimpleCustomItemValues(this, mutable);
    }

    public int getMaxStacksize() {
        return maxStacksize;
    }

    public void setMaxStacksize(int newStacksize) {
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
