package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Objects;

public class CustomToolValues extends CustomItemValues {

    static CustomToolValues load(
            BitInput input, byte encoding, SItemSet itemSet, boolean checkModel
    ) throws UnknownEncodingException {
        // Note: it doesn't really matter which CustomItemType is used since it will be overwritten anyway
        CustomToolValues result = new CustomToolValues(false, CustomItemType.IRON_PICKAXE);

        if (encoding == ItemEncoding.ENCODING_TOOL_2) {
            result.loadTool2(input);
            result.initToolDefaults2();
        } else if (encoding == ItemEncoding.ENCODING_TOOL_3) {
            result.loadTool3(input, itemSet);
            result.initToolDefaults3();
        } else if (encoding == ItemEncoding.ENCODING_TOOL_4) {
            result.loadTool4(input, itemSet);
            result.initToolDefaults4();
        } else if (encoding == ItemEncoding.ENCODING_TOOL_6) {
            result.loadTool6(input, itemSet);
            result.initToolDefaults6();
        } else if (encoding == ItemEncoding.ENCODING_TOOL_9) {
            result.loadTool9(input, itemSet);
            result.initToolDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_TOOL_10) {
            result.loadTool10(input, itemSet);
            result.initToolDefaults10();
        } else {
            throw new UnknownEncodingException("CustomTool", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, checkModel);
        }

        // Handle the case where a custom hoe was created before custom hoes had their own class
        if (result.itemType.canServe(CustomItemType.Category.HOE)) {
            return new CustomHoeValues(result, 1, false);
        }

        // Handle the case where a custom shears was created before custom shears had their own class
        if (result.itemType == CustomItemType.SHEARS) {
            return new CustomShearsValues(result, 1, false);
        }

        return result;
    }

    // Use null to indicate that the tool is unbreakable
    protected Long maxDurability;

    protected boolean allowEnchanting;
    protected boolean allowAnvilActions;

    protected IngredientValues repairItem;

    protected int entityHitDurabilityLoss;
    protected int blockBreakDurabilityLoss;

    public CustomToolValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable, initialItemType);

        this.maxDurability = 500L;
        this.allowEnchanting = true;
        this.allowAnvilActions = true;
        this.repairItem = new NoIngredientValues();
        this.entityHitDurabilityLoss = CustomToolDurability.defaultEntityHitDurabilityLoss(initialItemType);
        this.blockBreakDurabilityLoss = CustomToolDurability.defaultBlockBreakDurabilityLoss(initialItemType);
    }

    public CustomToolValues(CustomToolValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.maxDurability = toCopy.getMaxDurabilityNew();
        this.allowEnchanting = toCopy.allowEnchanting();
        this.allowAnvilActions = toCopy.allowAnvilActions();
        this.repairItem = toCopy.getRepairItem();
        this.entityHitDurabilityLoss = toCopy.getEntityHitDurabilityLoss();
        this.blockBreakDurabilityLoss = toCopy.getBlockBreakDurabilityLoss();
    }

    protected void loadTool2(BitInput input) {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers2(input);
        loadToolOnlyPropertiesA2(input);
    }

    protected void loadTool3(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers2(input);
        loadToolOnlyPropertiesA3(input, itemSet);
    }

    protected void loadTool4(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
    }

    protected void loadTool6(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTool4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
    }

    protected void loadTool9(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadTool6(input, itemSet);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
    }

    protected void loadTool10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadToolOnlyPropertiesA4(input, itemSet);
        loadItemFlags6(input);
        loadToolOnlyPropertiesB6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    protected void loadToolOnlyPropertiesA2(BitInput input) {
        int storedDurability = input.readInt();
        if (storedDurability == -1) {
            this.maxDurability = null;
        } else {
            this.maxDurability = (long) storedDurability;
        }
        this.allowEnchanting = input.readBoolean();
        this.allowAnvilActions = input.readBoolean();
    }

    protected void loadToolOnlyPropertiesA3(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadToolOnlyPropertiesA2(input);
        this.repairItem = IngredientValues.load(input, itemSet);
    }

    protected void loadToolOnlyPropertiesA4(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        long storedDurability = input.readLong();
        if (storedDurability == -1) {
            this.maxDurability = null;
        } else {
            this.maxDurability = storedDurability;
        }
        this.allowEnchanting = input.readBoolean();
        this.allowAnvilActions = input.readBoolean();
        this.repairItem = IngredientValues.load(input, itemSet);
    }

    protected void loadToolOnlyPropertiesB6(BitInput input) {
        this.entityHitDurabilityLoss = input.readInt();
        this.blockBreakDurabilityLoss = input.readInt();
    }

    protected boolean areToolPropertiesEqual(CustomToolValues other) {
        return areBaseItemPropertiesEqual(other) && Objects.equals(this.maxDurability, other.maxDurability)
                && this.allowEnchanting == other.allowEnchanting && this.allowAnvilActions == other.allowAnvilActions
                && this.repairItem.equals(other.repairItem) && this.entityHitDurabilityLoss == other.entityHitDurabilityLoss
                && this.blockBreakDurabilityLoss == other.blockBreakDurabilityLoss;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomToolValues.class && areToolPropertiesEqual((CustomToolValues) other);
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_TOOL_10);
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
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    protected void initToolOnlyDefaults10() {
        // Nothing to be done until the next encoding is made
    }

    protected void initToolDefaults10() {
        initBaseDefaults10();
        initToolOnlyDefaults10();
    }

    protected void initToolOnlyDefaults9() {
        initToolOnlyDefaults10();

        // No tool-only properties were introduced in encoding 10
    }

    protected void initToolDefaults9() {
        initToolOnlyDefaults9();
        initBaseDefaults9();
    }

    protected void initToolOnlyDefaults8() {
        initToolOnlyDefaults9();

        // No tool-only properties were introduced in encoding 9
    }

    protected void initToolDefaults8() {
        initBaseDefaults8();
        initToolOnlyDefaults8();
    }

    protected void initToolOnlyDefaults6() {
        initToolDefaults8();

        // No tool-only properties were introduced in encoding 8
    }

    protected void initToolDefaults6() {
        initBaseDefaults6();
        initToolOnlyDefaults6();
    }

    protected void initToolOnlyDefaults4() {
        initToolOnlyDefaults6();
        this.entityHitDurabilityLoss = CustomToolDurability.defaultEntityHitDurabilityLoss(this.itemType);
        this.blockBreakDurabilityLoss = CustomToolDurability.defaultBlockBreakDurabilityLoss(this.itemType);
    }

    protected void initToolDefaults4() {
        initBaseDefaults4();
        initToolOnlyDefaults4();
    }

    protected void initToolOnlyDefaults3() {
        initToolOnlyDefaults4();

        // No tool-only properties were introduced in encoding 4
    }

    protected void initToolDefaults3() {
        initBaseDefaults3();
        initToolOnlyDefaults3();
    }

    protected void initToolOnlyDefaults2() {
        this.repairItem = new NoIngredientValues();
    }

    protected void initToolDefaults2() {
        initBaseDefaults2();
        initToolOnlyDefaults2();
    }

    protected void saveToolOnlyPropertiesA4(BitOutput output) {
        if (maxDurability != null) {
            output.addLong(maxDurability);
        } else {
            output.addLong(-1);
        }
        output.addBoolean(allowEnchanting);
        output.addBoolean(allowAnvilActions);
        repairItem.save(output);
    }

    protected void saveToolOnlyPropertiesB6(BitOutput output) {
        output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    @Override
    public CustomToolValues copy(boolean mutable) {
        return new CustomToolValues(this, mutable);
    }

    public Long getMaxDurabilityNew() {
        return maxDurability;
    }

    public boolean allowEnchanting() {
        return allowEnchanting;
    }

    public boolean allowAnvilActions() {
        return allowAnvilActions;
    }

    public IngredientValues getRepairItem() {
        return repairItem;
    }

    public int getEntityHitDurabilityLoss() {
        return entityHitDurabilityLoss;
    }

    public int getBlockBreakDurabilityLoss() {
        return blockBreakDurabilityLoss;
    }

    public void setMaxDurabilityNew(Long newMaxDurability) {
        assertMutable();
        this.maxDurability = newMaxDurability;
    }

    public void setAllowEnchanting(boolean newAllowEnchanting) {
        assertMutable();
        this.allowEnchanting = newAllowEnchanting;
    }

    public void setAllowAnvilActions(boolean newAllowAnvilActions) {
        assertMutable();
        this.allowAnvilActions = newAllowAnvilActions;
    }

    public void setRepairItem(IngredientValues newRepairItem) {
        assertMutable();
        Checks.notNull(newRepairItem);
        this.repairItem = newRepairItem.copy(false);
    }

    public void setEntityHitDurabilityLoss(int newEntityHitDurabilityLoss) {
        assertMutable();
        this.entityHitDurabilityLoss = newEntityHitDurabilityLoss;
    }

    public void setBlockBreakDurabilityLoss(int newBlockBreakDurabilityLoss) {
        assertMutable();
        this.blockBreakDurabilityLoss = newBlockBreakDurabilityLoss;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (maxDurability != null && maxDurability == -1) {
            throw new ProgrammingValidationException("Max durability should be null");
        } else if (maxDurability != null && maxDurability <= 0) {
            throw new ValidationException("Max durability must be positive or -1");
        }

        if (allowEnchanting && !defaultEnchantments.isEmpty()) {
            throw new ValidationException("You can't allow enchanting if the tool has default enchantments");
        }

        if (repairItem == null) throw new ProgrammingValidationException("Repair item is null");
        repairItem.validateIndependent();

        if (entityHitDurabilityLoss < 0) throw new ValidationException("Entity hit durability loss can't be negative");
        if (blockBreakDurabilityLoss < 0) throw new ValidationException("Block break durability loss can't be negative");
    }

    @Override
    public void validateComplete(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        repairItem.validateComplete(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);

        repairItem.validateExportVersion(version);
    }
}
