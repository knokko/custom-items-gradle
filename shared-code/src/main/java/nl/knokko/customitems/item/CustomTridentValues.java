package nl.knokko.customitems.item;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;
import java.util.UUID;

import static nl.knokko.customitems.util.Checks.isClose;

public class CustomTridentValues extends CustomToolValues {

    static CustomTridentValues load(
            BitInput input, byte encoding, ItemSet itemSet
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
        } else if (encoding == ItemEncoding.ENCODING_TRIDENT_12) {
            result.loadTridentPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomTrident", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadTridentEditorOnlyProperties8(input, itemSet);
        }

        return result;
    }

    private int throwDurabilityLoss;

    private double throwDamageMultiplier;
    private double throwSpeedMultiplier;

    private ItemModel inHandModel;
    private ItemModel throwingModel;

    private CustomDamageSourceReference customThrowDamageSource;

    public CustomTridentValues(boolean mutable) {
        super(mutable, CustomItemType.TRIDENT);

        this.throwDurabilityLoss = 1;

        this.throwDamageMultiplier = 1.0;
        this.throwSpeedMultiplier = 1.0;

        this.inHandModel = new DefaultItemModel(DefaultModelType.TRIDENT_IN_HAND.recommendedParents.get(0));
        this.throwingModel = new DefaultItemModel(DefaultModelType.TRIDENT_THROWING.recommendedParents.get(0));
        this.customThrowDamageSource = null;
    }

    public CustomTridentValues(CustomTridentValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.throwDurabilityLoss = toCopy.getThrowDurabilityLoss();
        this.throwDamageMultiplier = toCopy.getThrowDamageMultiplier();
        this.throwSpeedMultiplier = toCopy.getThrowSpeedMultiplier();
        this.inHandModel = toCopy.getInHandModel();
        this.throwingModel = toCopy.getThrowingModel();
        this.customThrowDamageSource = toCopy.getCustomThrowDamageSourceReference();
    }

    @Override
    public DefaultModelType getDefaultModelType() {
        return DefaultModelType.TRIDENT;
    }

    protected void loadTridentPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadToolPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 3) throw new UnknownEncodingException("CustomTridentNew", encoding);

        this.throwDurabilityLoss = input.readInt();
        this.throwDamageMultiplier = input.readDouble();
        this.throwSpeedMultiplier = input.readDouble();

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            if (encoding >= 2) {
                this.inHandModel = ItemModel.load(input);
                this.throwingModel = ItemModel.load(input);
            } else {
                if (input.readBoolean()) {
                    this.inHandModel = new LegacyCustomItemModel(input.readByteArray());
                } else {
                    this.inHandModel = new DefaultItemModel(DefaultModelType.TRIDENT_IN_HAND.recommendedParents.get(0));
                }
                if (input.readBoolean()) {
                    this.throwingModel = new LegacyCustomItemModel(input.readByteArray());
                } else {
                    this.throwingModel = new DefaultItemModel(DefaultModelType.TRIDENT_THROWING.recommendedParents.get(0));
                }
            }
        }

        if (encoding >= 3 && input.readBoolean()) {
            this.customThrowDamageSource = itemSet.getDamageSourceReference(new UUID(input.readLong(), input.readLong()));
        } else this.customThrowDamageSource = null;
    }

    protected void saveTridentPropertiesNew(BitOutput output, ItemSet.Side side) {
        this.saveToolPropertiesNew(output, side);

        output.addByte((byte) 3);

        output.addInt(this.throwDurabilityLoss);
        output.addDoubles(this.throwDamageMultiplier, this.throwSpeedMultiplier);
        if (side == ItemSet.Side.EDITOR) {
            this.inHandModel.save(output);
            this.throwingModel.save(output);
        }
        output.addBoolean(this.customThrowDamageSource != null);
        if (this.customThrowDamageSource != null) {
            output.addLong(this.customThrowDamageSource.get().getId().getMostSignificantBits());
            output.addLong(this.customThrowDamageSource.get().getId().getLeastSignificantBits());
        }
    }

    private void loadTridentEditorOnlyProperties8(BitInput input, ItemSet itemSet) {
        loadEditorOnlyProperties1(input, itemSet, true);

        if (input.readBoolean()) {
            this.inHandModel = new LegacyCustomItemModel(input.readByteArray());
        } else {
            this.inHandModel = new DefaultItemModel(DefaultModelType.TRIDENT_IN_HAND.recommendedParents.get(0));
        }

        if (input.readBoolean()) {
            this.throwingModel = new LegacyCustomItemModel(input.readByteArray());
        } else {
            this.throwingModel = new DefaultItemModel(DefaultModelType.TRIDENT_THROWING.recommendedParents.get(0));
        }
    }

    private void load8(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadTridentIdentityProperties8(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        loadTridentOnlyProperties8(input);
    }

    private void load9(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        load8(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
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
        initTridentOnlyDefaults11();
        this.customThrowDamageSource = null;
    }

    private void initTridentOnlyDefaults11() {
        // Nothing to be done until the next encoding is known
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

    protected boolean areTridentPropertiesEqual(CustomTridentValues other) {
        return areToolPropertiesEqual(other) && this.throwDurabilityLoss == other.throwDurabilityLoss
                && isClose(this.throwDamageMultiplier, other.throwDamageMultiplier)
                && isClose(this.throwSpeedMultiplier, other.throwSpeedMultiplier)
                && Objects.equals(this.customThrowDamageSource, other.customThrowDamageSource);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomTridentValues.class && areTridentPropertiesEqual((CustomTridentValues) other);
    }

    @Override
    public CustomTridentValues copy(boolean mutable) {
        return new CustomTridentValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_TRIDENT_12);
        this.saveTridentPropertiesNew(output, side);
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

    public ItemModel getInHandModel() {
        return inHandModel;
    }

    public ItemModel getThrowingModel() {
        return throwingModel;
    }

    public CustomDamageSourceReference getCustomThrowDamageSourceReference() {
        return customThrowDamageSource;
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

    public void setInHandModel(ItemModel newModel) {
        assertMutable();
        this.inHandModel = newModel;
    }

    public void setThrowingModel(ItemModel newModel) {
        assertMutable();
        this.throwingModel = newModel;
    }

    public void setCustomThrowDamageSource(CustomDamageSourceReference newDamageSource) {
        assertMutable();
        this.customThrowDamageSource = newDamageSource;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (throwDurabilityLoss < 0) throw new ValidationException("Durability loss on throwing can't be negative");
        if (throwDamageMultiplier < 0) throw new ValidationException("Throw damage multiplier can't be negative");
        if (throwSpeedMultiplier < 0) throw new ValidationException("Throw speed multiplier can't be negative");

        if (inHandModel == null) throw new ProgrammingValidationException("No in-hand model");
        if (throwingModel == null) throw new ProgrammingValidationException("No throwing model");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (customThrowDamageSource != null && !itemSet.isReferenceValid(customThrowDamageSource)) {
            throw new ProgrammingValidationException("Invalid custom throw damage source");
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version > MCVersions.VERSION1_14) {
            throw new ValidationException("No custom tridents in MC 1.15+. See github.com/knokko/custom-items-gradle/issues/7");
        }
        super.validateExportVersion(version);
    }
}
