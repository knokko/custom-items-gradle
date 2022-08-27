package nl.knokko.customitems.item;

import nl.knokko.customitems.attack.effect.AttackEffectGroupValues;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

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
        } else if (encoding == ItemEncoding.ENCODING_SHIELD_12) {
            result.loadShieldPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomShield", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadShieldEditorOnlyProperties7(input, itemSet);
        }

        return result;
    }

    private double thresholdDamage;
    private Collection<AttackEffectGroupValues> blockingEffects;
    private ItemModel blockingModel;

    public CustomShieldValues(boolean mutable) {
        super(mutable, CustomItemType.SHIELD);

        this.thresholdDamage = 4.0;
        this.blockingEffects = new ArrayList<>();
        this.blockingModel = new DefaultItemModel(DefaultModelType.SHIELD_BLOCKING.recommendedParents.get(0));
    }

    public CustomShieldValues(CustomShieldValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.thresholdDamage = toCopy.getThresholdDamage();
        this.blockingEffects = toCopy.getBlockingEffects();
        this.blockingModel = toCopy.getBlockingModel();
    }

    @Override
    public DefaultModelType getDefaultModelType() {
        return DefaultModelType.SHIELD;
    }

    protected void loadShieldPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadToolPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 3) throw new UnknownEncodingException("CustomShieldNew", encoding);

        this.thresholdDamage = input.readDouble();
        if (encoding >= 2) {
            int numBlockingEffects = input.readInt();
            this.blockingEffects = new ArrayList<>(numBlockingEffects);
            for (int counter = 0; counter < numBlockingEffects; counter++) {
                this.blockingEffects.add(AttackEffectGroupValues.load(input, itemSet));
            }
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            if (encoding >= 3) {
                this.blockingModel = ItemModel.load(input);
            } else {
                if (input.readBoolean()) {
                    this.blockingModel = new LegacyCustomItemModel(input.readByteArray())           ;
                } else {
                    this.blockingModel = new DefaultItemModel(DefaultModelType.SHIELD_BLOCKING.recommendedParents.get(0));
                }
            }
        }
    }

    protected void saveShieldPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveToolPropertiesNew(output, targetSide);

        output.addByte((byte) 3);

        output.addDouble(this.thresholdDamage);
        output.addInt(this.blockingEffects.size());
        for (AttackEffectGroupValues blockingEffectGroup : this.blockingEffects) {
            blockingEffectGroup.save(output);
        }
        if (targetSide == ItemSet.Side.EDITOR) {
            this.blockingModel.save(output);
        }
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

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_SHIELD_12);
        this.saveShieldPropertiesNew(output, side);
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

    private void loadShieldEditorOnlyProperties7(BitInput input, ItemSet itemSet) {
        loadEditorOnlyProperties1(input, itemSet, true);
        if (input.readBoolean()) {
            this.blockingModel = new LegacyCustomItemModel(input.readByteArray());
        } else {
            this.blockingModel = new DefaultItemModel(DefaultModelType.SHIELD_BLOCKING.recommendedParents.get(0));
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
        this.blockingEffects = new ArrayList<>();
    }

    protected boolean areShieldPropertiesEqual(CustomShieldValues other) {
        return areToolPropertiesEqual(other) && isClose(this.thresholdDamage, other.thresholdDamage)
                && this.blockingEffects.equals(other.blockingEffects);
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

    public Collection<AttackEffectGroupValues> getBlockingEffects() {
        return new ArrayList<>(blockingEffects);
    }

    public ItemModel getBlockingModel() {
        return blockingModel;
    }

    public void setThresholdDamage(double newThresholdDamage) {
        assertMutable();
        this.thresholdDamage = newThresholdDamage;
    }

    public void setBlockingEffects(Collection<AttackEffectGroupValues> newBlockingEffects) {
        assertMutable();
        Checks.nonNull(newBlockingEffects);
        this.blockingEffects = Mutability.createDeepCopy(newBlockingEffects, false);
    }

    public void setBlockingModel(ItemModel newModel) {
        assertMutable();
        this.blockingModel = newModel;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (thresholdDamage < 0.0) throw new ValidationException("Threshold damage can't be negative");
        if (blockingEffects == null) throw new ProgrammingValidationException("No blocking effects");
        for (AttackEffectGroupValues blockingEffectGroup : blockingEffects) {
            if (blockingEffectGroup == null) throw new ProgrammingValidationException("Missing a blocking effect");
        }

        if (blockingModel == null) throw new ProgrammingValidationException("No blocking model");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        for (AttackEffectGroupValues blockingEffectGroup : blockingEffects) {
            Validation.scope("Blocking effects", blockingEffectGroup::validate, itemSet);
        }
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(mcVersion);

        for (AttackEffectGroupValues blockingEffectGroup : blockingEffects) {
            Validation.scope("Blocking effects", () -> blockingEffectGroup.validateExportVersion(mcVersion));
        }

    }
}
