package nl.knokko.customitems.item;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.attack.effect.AttackEffectGroupValues;
import nl.knokko.customitems.damage.SpecialMeleeDamageValues;
import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.itemset.FakeItemSet;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitInputTracker;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nl.knokko.customitems.encoding.ItemEncoding.*;
import static nl.knokko.customitems.util.Checks.isClose;

public abstract class CustomItemValues extends ModelValues {

    public static final long UNBREAKABLE_TOOL_DURABILITY = -1;

    public static CustomItemValues loadFromBooleanRepresentation(byte[] booleanRepresentation) throws UnknownEncodingException {
        BitInput input = new ByteArrayBitInput(booleanRepresentation);

        return loadRaw(input, new FakeItemSet(), true);
    }

    public static CustomItemValues load(
            BitInput input, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {

        BitInputTracker inputTracker = new BitInputTracker(input, 100);
        CustomItemValues loadedItem = loadRaw(inputTracker, itemSet, checkCustomModel);
        loadedItem.setBooleanRepresentation(inputTracker.getReadBytes());
        return loadedItem;
    }

    private static CustomItemValues loadRaw(
            BitInput input, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (
                encoding == ENCODING_ARMOR_4 || encoding == ENCODING_ARMOR_6 || encoding == ENCODING_ARMOR_7
                || encoding == ENCODING_ARMOR_8 || encoding == ENCODING_ARMOR_9 || encoding == ENCODING_ARMOR_10
                || encoding == ENCODING_ARMOR_11 || encoding == ENCODING_ARMOR_12
        ) {
            return CustomArmorValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_BLOCK_ITEM_10 || encoding == ENCODING_BLOCK_ITEM_12) {
            return CustomBlockItemValues.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_BOW_3 || encoding == ENCODING_BOW_4 || encoding == ENCODING_BOW_6
                || encoding == ENCODING_BOW_9 || encoding == ENCODING_BOW_10 || encoding == ENCODING_BOW_12
        ) {
            return CustomBowValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_CROSSBOW_10 || encoding == ENCODING_CROSSBOW_12) {
            return CustomCrossbowValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_FOOD_10 || encoding == ENCODING_FOOD_12) {
            return CustomFoodValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_GUN_10 || encoding == ENCODING_GUN_12) {
            return CustomGunValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_HELMET3D_10 || encoding == ENCODING_HELMET3D_11 ||
                encoding == ENCODING_HELMET3D_12) {
            return CustomHelmet3dValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_HOE_6 || encoding == ENCODING_HOE_9 ||
                encoding == ENCODING_HOE_10 || encoding == ENCODING_HOE_12) {
            return CustomHoeValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_POCKET_CONTAINER_10 || encoding == ENCODING_POCKET_CONTAINER_12) {
            return CustomPocketContainerValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_SHEAR_6 || encoding == ENCODING_SHEAR_9 ||
                encoding == ENCODING_SHEAR_10 || encoding == ENCODING_SHEARS_12) {
            return CustomShearsValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_SHIELD_7 || encoding == ENCODING_SHIELD_9 ||
                encoding == ENCODING_SHIELD_10 || encoding == ENCODING_SHIELD_12) {
            return CustomShieldValues.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_TOOL_2 || encoding == ENCODING_TOOL_3 || encoding == ENCODING_TOOL_4
                || encoding == ENCODING_TOOL_6 || encoding == ENCODING_TOOL_9 || encoding == ENCODING_TOOL_10
                || encoding == ENCODING_TOOL_12
        ) {
            return CustomToolValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_TRIDENT_8 || encoding == ENCODING_TRIDENT_9 ||
                encoding == ENCODING_TRIDENT_10 || encoding == ENCODING_TRIDENT_12) {
            return CustomTridentValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_WAND_9 || encoding == ENCODING_WAND_10 || encoding == ENCODING_WAND_12) {
            return CustomWandValues.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_SIMPLE_1 || encoding == ENCODING_SIMPLE_2 || encoding == ENCODING_SIMPLE_4
                || encoding == ENCODING_SIMPLE_5 || encoding == ENCODING_SIMPLE_6 || encoding == ENCODING_SIMPLE_9
                || encoding == ENCODING_SIMPLE_10 || encoding == ENCODING_SIMPLE_12
        ) {
            return SimpleCustomItemValues.load(input, encoding, itemSet, checkCustomModel);
        } else {
            throw new UnknownEncodingException("CustomItem", encoding);
        }
    }

    // Identity properties
    protected CustomItemType itemType;
    protected CIMaterial otherMaterial;
    protected short itemDamage;
    protected String name;
    protected String alias;

    // Text display properties
    protected String displayName;
    protected List<String> lore;

    // Item flags (they are not in a group)
    protected List<Boolean> itemFlags;

    // Vanilla based power properties
    protected Collection<AttributeModifierValues> attributeModifiers;
    protected Collection<EnchantmentValues> defaultEnchantments;

    // Potion properties
    protected Collection<ChancePotionEffectValues> playerEffects;
    protected Collection<ChancePotionEffectValues> targetEffects;
    protected Collection<EquippedPotionEffectValues> equippedEffects;

    // Right-click properties
    protected ReplacementConditionValues.ConditionOperation conditionOp;
    protected List<ReplacementConditionValues> replaceConditions;

    // Other properties
    protected ItemCommandSystem commandSystem;
    protected ExtraItemNbtValues extraItemNbt;
    protected float attackRange;
    protected SpecialMeleeDamageValues specialMeleeDamage;
    protected Collection<AttackEffectGroupValues> attackEffects;
    protected boolean updateAutomatically;

    // Editor-only properties
    protected TextureReference texture;
    protected byte[] customModel;

    // Plugin-only properties
    private byte[] booleanRepresentation;

    public CustomItemValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable);

        this.itemType = initialItemType;
        if (initialItemType == CustomItemType.OTHER) {
            this.otherMaterial = CIMaterial.FLINT;
        } else {
            this.otherMaterial = null;
        }
        this.itemDamage = 0; // This will be taken care of later
        this.name = "";
        this.alias = "";

        this.displayName = "";
        this.lore = new ArrayList<>(0);

        this.itemFlags = ItemFlag.getDefaultValuesList();

        this.attributeModifiers = new ArrayList<>(0);
        this.defaultEnchantments = new ArrayList<>(0);

        this.playerEffects = new ArrayList<>(0);
        this.targetEffects = new ArrayList<>(0);
        this.equippedEffects = new ArrayList<>(0);

        this.conditionOp = ReplacementConditionValues.ConditionOperation.NONE;
        this.replaceConditions = new ArrayList<>(0);

        this.commandSystem = new ItemCommandSystem(false);
        this.extraItemNbt = new ExtraItemNbtValues(false);
        this.attackRange = 1f;
        this.specialMeleeDamage = null;
        this.attackEffects = new ArrayList<>();
        this.updateAutomatically = true;

        this.texture = null;
        this.customModel = null;
    }

    public CustomItemValues(CustomItemValues source, boolean mutable) {
        super(mutable);

        this.itemType = source.getItemType();
        this.otherMaterial = source.getOtherMaterial();
        this.itemDamage = source.getItemDamage();
        this.name = source.getName();
        this.alias = source.getAlias();
        this.displayName = source.getDisplayName();
        this.lore = source.getLore();
        this.itemFlags = source.getItemFlags();
        this.attributeModifiers = source.getAttributeModifiers();
        this.defaultEnchantments = source.getDefaultEnchantments();
        this.playerEffects = source.getOnHitPlayerEffects();
        this.targetEffects = source.getOnHitTargetEffects();
        this.equippedEffects = source.getEquippedEffects();
        this.replaceConditions = source.getReplacementConditions();
        this.conditionOp = source.getConditionOp();
        this.commandSystem = source.getCommandSystem();
        this.extraItemNbt = source.getExtraNbt();
        this.attackRange = source.getAttackRange();
        this.specialMeleeDamage = source.getSpecialMeleeDamage();
        this.attackEffects = source.getAttackEffects();
        this.updateAutomatically = source.shouldUpdateAutomatically();
        this.texture = source.getTextureReference();
        this.customModel = source.getCustomModel();
        this.booleanRepresentation = source.getBooleanRepresentation();
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    protected boolean areBaseItemPropertiesEqual(CustomItemValues other) {
        return this.itemType == other.itemType && this.otherMaterial == other.otherMaterial
                && this.name.equals(other.name) && this.alias.equals(other.alias)
                && this.displayName.equals(other.displayName) && this.lore.equals(other.lore)
                && this.itemFlags.equals(other.itemFlags) && this.attributeModifiers.equals(other.attributeModifiers)
                && this.defaultEnchantments.equals(other.defaultEnchantments) && this.playerEffects.equals(other.playerEffects)
                && this.targetEffects.equals(other.targetEffects) && this.equippedEffects.equals(other.equippedEffects)
                && this.commandSystem.equals(other.commandSystem) && this.replaceConditions.equals(other.replaceConditions)
                && this.conditionOp == other.conditionOp && this.extraItemNbt.equals(other.extraItemNbt)
                && isClose(this.attackRange, other.attackRange) && Objects.equals(this.specialMeleeDamage, other.specialMeleeDamage)
                && this.attackEffects.equals(other.attackEffects) && this.updateAutomatically == other.updateAutomatically;
    }

    @Override
    public abstract CustomItemValues copy(boolean mutable);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + name + ")";
    }

    public abstract void save(BitOutput output, ItemSet.Side side);

    protected void loadEditorOnlyProperties1(BitInput input, ItemSet itemSet, boolean checkCustomModel) {
        String textureName = input.readJavaString();
        this.texture = itemSet.getTextureReference(textureName);

        if (checkCustomModel && input.readBoolean()) {
            this.customModel = input.readByteArray();
        } else {
            this.customModel = null;
        }
    }

    protected void loadSharedPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomItemBaseNew", encoding);

        this.loadIdentityProperties10(input);
        if (this.itemType == CustomItemType.OTHER) {
            this.otherMaterial = CIMaterial.valueOf(input.readString());
        }
        this.loadTextDisplayProperties1(input);

        int numItemFlags = input.readInt();
        this.itemFlags = new ArrayList<>(numItemFlags);
        for (int counter = 0; counter < numItemFlags; counter++) {
            this.itemFlags.add(input.readBoolean());
        }

        this.loadVanillaBasedPowers4(input);
        this.playerEffects = this.loadChanceEffects(input);
        this.targetEffects = this.loadChanceEffects(input);
        this.loadEquippedPotionEffects10(input);
        this.loadReplacementConditions10(input, itemSet);
        this.commandSystem = ItemCommandSystem.load(input);
        this.loadExtraProperties10(input);
        if (encoding >= 2) {
            if (input.readBoolean()) {
                this.specialMeleeDamage = SpecialMeleeDamageValues.load(input);
            } else {
                this.specialMeleeDamage = null;
            }
            int numAttackEffectGroups = input.readInt();
            this.attackEffects = new ArrayList<>(numAttackEffectGroups);
            for (int counter = 0; counter < numAttackEffectGroups; counter++) {
                this.attackEffects.add(AttackEffectGroupValues.load(input));
            }
            this.updateAutomatically = input.readBoolean();
        } else {
            this.specialMeleeDamage = null;
            this.attackEffects = new ArrayList<>();
            this.updateAutomatically = true;
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            this.loadEditorOnlyProperties1(input, itemSet, true);
        }
    }

    protected void saveSharedPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        output.addByte((byte) 2);

        this.saveIdentityProperties10(output);
        if (this.itemType == CustomItemType.OTHER) {
            output.addString(this.otherMaterial.name());
        }
        this.saveTextDisplayProperties1(output);

        output.addInt(this.itemFlags.size());
        for (Boolean itemFlag : this.itemFlags) {
            output.addBoolean(itemFlag);
        }

        this.saveVanillaBasedPowers4(output);
        this.saveChanceEffects(output, this.playerEffects);
        this.saveChanceEffects(output, this.targetEffects);
        this.saveEquippedPotionEffects10(output);
        this.saveReplacementConditions10(output);
        this.commandSystem.save(output);
        this.saveExtraProperties10(output);
        output.addBoolean(this.specialMeleeDamage != null);
        if (this.specialMeleeDamage != null) {
            this.specialMeleeDamage.save(output);
        }
        output.addInt(this.attackEffects.size());
        for (AttackEffectGroupValues attackEffectGroup : this.attackEffects) {
            attackEffectGroup.save(output);
        }
        output.addBoolean(this.updateAutomatically);

        if (targetSide == ItemSet.Side.EDITOR) {
            this.saveEditorOnlyProperties1(output);
        }
    }

    protected void saveEditorOnlyProperties1(BitOutput output) {
        output.addJavaString(texture.get().getName());
        output.addBoolean(customModel != null);
        if (customModel != null) {
            output.addByteArray(customModel);
        }
    }

    protected void loadIdentityProperties1(BitInput input) {
        this.itemType = CustomItemType.valueOf(input.readJavaString());
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
    }

    protected void saveIdentityProperties1(BitOutput output) {
        output.addJavaString(itemType.name());
        output.addShort(itemDamage);
        output.addJavaString(name);
    }

    protected void loadIdentityProperties10(BitInput input) {
        loadIdentityProperties1(input);
        this.alias = input.readString();
    }

    protected void saveIdentityProperties10(BitOutput output) {
        saveIdentityProperties1(output);
        output.addString(alias);
    }

    protected void loadItemFlags6(BitInput input) {
        int numItemFlags = 6;
        this.itemFlags = new ArrayList<>(numItemFlags);
        for (int counter = 0; counter < numItemFlags; counter++) {
            this.itemFlags.add(input.readBoolean());
        }
    }

    protected void loadVanillaBasedPowers2(BitInput input) {
        loadAttributeModifiers2(input);
    }

    protected void saveVanillaBasedPowers2(BitOutput output) {
        saveAttributeModifiers2(output);
    }

    protected void loadVanillaBasedPowers4(BitInput input) {
        loadVanillaBasedPowers2(input);
        loadDefaultEnchantments4(input);
    }

    protected void saveVanillaBasedPowers4(BitOutput output) {
        saveVanillaBasedPowers2(output);
        saveDefaultEnchantments4(output);
    }

    protected void loadAttributeModifiers2(BitInput input) {
        int numAttributeModifiers = input.readByte() & 0xFF;
        this.attributeModifiers = new ArrayList<>(numAttributeModifiers);
        for (int counter = 0; counter < numAttributeModifiers; counter++) {
            this.attributeModifiers.add(AttributeModifierValues.load1(input, false));
        }
    }

    protected void saveAttributeModifiers2(BitOutput output) {
        output.addByte((byte) attributeModifiers.size());
        for (AttributeModifierValues attributeModifier : attributeModifiers) {
            attributeModifier.save1(output);
        }
    }

    protected void loadDefaultEnchantments4(BitInput input) {
        int numDefaultEnchantments = input.readByte() & 0xFF;
        this.defaultEnchantments = new ArrayList<>(numDefaultEnchantments);
        for (int counter = 0; counter < numDefaultEnchantments; counter++) {
            this.defaultEnchantments.add(EnchantmentValues.load1(input, false));
        }
    }

    protected void saveDefaultEnchantments4(BitOutput output) {
        output.addByte((byte) defaultEnchantments.size());
        for (EnchantmentValues defaultEnchantment : defaultEnchantments) {
            defaultEnchantment.save1(output);
        }
    }

    protected void saveChanceEffects(BitOutput output, Collection<ChancePotionEffectValues> effects) {
        output.addInt(effects.size());
        for (ChancePotionEffectValues effect : effects) {
            effect.save(output);
        }
    }

    protected Collection<ChancePotionEffectValues> loadChanceEffects(BitInput input) throws UnknownEncodingException {
        int numEffects = input.readInt();
        Collection<ChancePotionEffectValues> result = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            result.add(ChancePotionEffectValues.load(input));
        }
        return result;
    }

    protected void loadPotionProperties9(BitInput input) {
        loadOnHitPlayerEffects9(input);
        loadOnHitTargetEffects9(input);
    }

    protected void loadOnHitPlayerEffects9(BitInput input) {
        Collection<PotionEffectValues> rawEffectList = loadPotionEffectList(input);
        this.playerEffects = rawEffectList.stream().map(
                rawEffect -> ChancePotionEffectValues.createQuick(rawEffect, Chance.percentage(100))
        ).collect(Collectors.toList());
    }

    protected void loadOnHitTargetEffects9(BitInput input) {
        Collection<PotionEffectValues> rawEffectList = loadPotionEffectList(input);
        this.targetEffects = rawEffectList.stream().map(
                rawEffect -> ChancePotionEffectValues.createQuick(rawEffect, Chance.percentage(100))
        ).collect(Collectors.toList());
    }

    protected Collection<PotionEffectValues> loadPotionEffectList(BitInput input) {
        int numEffects = input.readByte() & 0xFF;
        Collection<PotionEffectValues> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(PotionEffectValues.load1(input, false));
        }
        return effects;
    }

    protected void savePotionEffectList(Collection<PotionEffectValues> effects, BitOutput output) {
        output.addByte((byte) effects.size());
        for (PotionEffectValues effect : effects) {
            effect.save1(output);
        }
    }

    protected void loadPotionProperties10(BitInput input) {
        loadPotionProperties9(input);
        loadEquippedPotionEffects10(input);
    }

    protected void loadEquippedPotionEffects10(BitInput input) {
        int numEquippedEffects = input.readInt();
        this.equippedEffects = new ArrayList<>(numEquippedEffects);
        for (int counter = 0; counter < numEquippedEffects; counter++) {
            this.equippedEffects.add(EquippedPotionEffectValues.load1(input, false));
        }
    }

    protected void saveEquippedPotionEffects10(BitOutput output) {
        output.addInt(equippedEffects.size());
        for (EquippedPotionEffectValues effect : equippedEffects) {
            effect.save1(output);
        }
    }

    protected void loadRightClickProperties9(BitInput input) {
        loadCommands9(input);
    }

    protected void loadCommands9(BitInput input) {
        int numLegacyCommands = input.readByte() & 0xFF;
        List<ItemCommand> legacyCommands = new ArrayList<>(numLegacyCommands);
        for (int counter = 0; counter < numLegacyCommands; counter++) {
            legacyCommands.add(ItemCommand.createFromLegacy(input.readJavaString()));
        }

        ItemCommandSystem loadedCommandSystem = new ItemCommandSystem(true);
        loadedCommandSystem.setCommandsFor(ItemCommandEvent.RIGHT_CLICK_GENERAL, legacyCommands);
        this.commandSystem = loadedCommandSystem.copy(false);
    }

    protected void loadRightClickProperties10(BitInput input, ItemSet itemSet) {
        loadRightClickProperties9(input);
        loadReplacementConditions10(input, itemSet);
    }

    protected void loadReplacementConditions10(BitInput input, ItemSet itemSet) {
        int numReplacementConditions = input.readByte() & 0xFF;
        this.replaceConditions = new ArrayList<>(numReplacementConditions);
        for (int counter = 0; counter < numReplacementConditions; counter++) {
            this.replaceConditions.add(ReplacementConditionValues.load1(input, itemSet, false));
        }
        this.conditionOp = ReplacementConditionValues.ConditionOperation.valueOf(input.readJavaString());
    }

    protected void saveReplacementConditions10(BitOutput output) {
        output.addByte((byte) replaceConditions.size());
        for (ReplacementConditionValues replaceCondition : replaceConditions) {
            replaceCondition.save1(output);
        }
        output.addJavaString(conditionOp.name());
    }

    protected void loadExtraProperties10(BitInput input) throws UnknownEncodingException {
        this.extraItemNbt = ExtraItemNbtValues.load(input, false);
        this.attackRange = input.readFloat();
    }

    protected void saveExtraProperties10(BitOutput output) {
        extraItemNbt.save(output);
        output.addFloat(attackRange);
    }

    protected void loadTextDisplayProperties1(BitInput input) {
        this.displayName = input.readJavaString();
        int numLoreLines = input.readByte() & 0xFF;
        this.lore = new ArrayList<>(numLoreLines);
        for (int counter = 0; counter < numLoreLines; counter++) {
            this.lore.add(input.readJavaString());
        }
    }

    protected void saveTextDisplayProperties1(BitOutput output) {
        output.addJavaString(displayName);
        output.addByte((byte) lore.size());
        for (String loreLine : lore) {
            output.addJavaString(loreLine);
        }
    }

    protected void loadBase10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    protected void initBaseDefaults10() {
        this.specialMeleeDamage = null;
        this.attackEffects = new ArrayList<>(0);
        this.updateAutomatically = true;
    }

    protected void initBaseDefaults9() {
        initBaseDefaults10();

        this.alias = "";

        this.equippedEffects = new ArrayList<>(0);

        this.conditionOp = ReplacementConditionValues.ConditionOperation.NONE;
        this.replaceConditions = new ArrayList<>(0);

        this.extraItemNbt = new ExtraItemNbtValues(false);
        this.attackRange = 1f;
    }

    protected void initBaseDefaults8() {
        initBaseDefaults9();

        this.playerEffects = new ArrayList<>(0);
        this.targetEffects = new ArrayList<>(0);

        this.commandSystem = new ItemCommandSystem(false);
    }

    protected void initBaseDefaults7() {
        initBaseDefaults8();

        // No shared item properties were introduced in encoding 8
    }

    protected void initBaseDefaults6() {
        initBaseDefaults7();

        // No shared item properties were introduced in encoding 7
    }

    protected void initBaseDefaults5() {
        initBaseDefaults6();

        this.itemFlags = ItemFlag.getDefaultValuesList();
    }

    protected void initBaseDefaults4() {
        initBaseDefaults5();

        // No shared item properties were introduced in encoding 5
    }

    protected void initBaseDefaults3() {
        initBaseDefaults4();

        this.defaultEnchantments = new ArrayList<>(0);
    }

    protected void initBaseDefaults2() {
        initBaseDefaults3();

        // No shared item properties were introduced in encoding 3
    }

    protected void initBaseDefaults1() {
        initBaseDefaults2();

        this.attributeModifiers = new ArrayList<>();
    }

    public CustomItemType getItemType() {
        return itemType;
    }

    public CIMaterial getOtherMaterial() {
        return otherMaterial;
    }

    public short getItemDamage() {
        return itemDamage;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public abstract byte getMaxStacksize();

    public boolean canStack() {
        return getMaxStacksize() > 1;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    public List<Boolean> getItemFlags() {
        return new ArrayList<>(itemFlags);
    }

    public Collection<AttributeModifierValues> getAttributeModifiers() {
        return new ArrayList<>(attributeModifiers);
    }

    public Collection<EnchantmentValues> getDefaultEnchantments() {
        return new ArrayList<>(defaultEnchantments);
    }

    public Collection<ChancePotionEffectValues> getOnHitPlayerEffects() {
        return new ArrayList<>(playerEffects);
    }

    public Collection<ChancePotionEffectValues> getOnHitTargetEffects() {
        return new ArrayList<>(targetEffects);
    }

    public Collection<EquippedPotionEffectValues> getEquippedEffects() {
        return new ArrayList<>(equippedEffects);
    }

    public ItemCommandSystem getCommandSystem() {
        return this.commandSystem;
    }

    public List<ReplacementConditionValues> getReplacementConditions() {
        return new ArrayList<>(replaceConditions);
    }

    public ReplacementConditionValues.ConditionOperation getConditionOp() {
        return conditionOp;
    }

    public ExtraItemNbtValues getExtraNbt() {
        return extraItemNbt;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public SpecialMeleeDamageValues getSpecialMeleeDamage() {
        return specialMeleeDamage;
    }

    public Collection<AttackEffectGroupValues> getAttackEffects() {
        return new ArrayList<>(attackEffects);
    }

    public boolean shouldUpdateAutomatically() {
        return updateAutomatically;
    }

    public BaseTextureValues getTexture() {
        return texture.get();
    }

    public TextureReference getTextureReference() {
        return texture;
    }

    public byte[] getCustomModel() {
        return CollectionHelper.arrayCopy(customModel);
    }

    public byte[] getBooleanRepresentation() {
        return CollectionHelper.arrayCopy(booleanRepresentation);
    }

    public boolean allowAnvilActions() {
        return false;
    }

    public boolean allowEnchanting() {
        return false;
    }

    public void setItemType(CustomItemType newItemType) {
        assertMutable();
        Checks.nonNull(newItemType);
        if (newItemType != this.itemType) {
            if (newItemType == CustomItemType.OTHER) {
                this.otherMaterial = CIMaterial.FLINT;
            } else {
                this.otherMaterial = null;
            }
            this.itemType = newItemType;
        }
    }

    public void setOtherMaterial(CIMaterial newOtherMaterial) {
        assertMutable();
        if (newOtherMaterial == null) {
            if (this.itemType == CustomItemType.OTHER) {
                throw new UnsupportedOperationException("newOtherMaterial can't be null when itemType is OTHER");
            }
        } else {
            if (this.itemType != CustomItemType.OTHER) {
                throw new IllegalArgumentException("newOtherMaterial must be null when itemType is not OTHER");
            }
        }
        this.otherMaterial = newOtherMaterial;
    }

    public void setItemDamage(short newItemDamage) {
        assertMutable();
        this.itemDamage = newItemDamage;
    }

    public void setName(String newName) {
        assertMutable();
        Checks.notNull(newName);
        this.name = newName;
    }

    public void setAlias(String newAlias) {
        assertMutable();
        Checks.notNull(newAlias);
        this.alias = newAlias;
    }

    private String transformToColorCodes(String original) {
        int[] codePoints = original.codePoints().toArray();
        for (int index = 0; index < codePoints.length - 1; index++) {

            // I will consider every occurrence of &x a color code if and only if x is a letter or digit
            if (codePoints[index] == '&') {
                int nextChar = codePoints[index + 1];
                if ((nextChar >= '0' && nextChar <= '9') || (nextChar >= 'a' && nextChar <= 'z')) {
                    // 167 is the code for the color code character
                    codePoints[index] = 167;
                }
            }
        }
        return IntStream.of(codePoints).collect(
                StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append
        ).toString();
    }

    public void setDisplayName(String newDisplayName) {
        assertMutable();
        Checks.notNull(newDisplayName);
        this.displayName = transformToColorCodes(newDisplayName);
    }

    public void setLore(List<String> newLore) {
        assertMutable();
        Checks.nonNull(newLore);
        this.lore = newLore.stream().map(this::transformToColorCodes).collect(Collectors.toList());
    }

    public void setItemFlags(List<Boolean> newItemFlags) {
        assertMutable();
        Checks.nonNull(newItemFlags);
        this.itemFlags = new ArrayList<>(newItemFlags);
    }

    public void setAttributeModifiers(Collection<AttributeModifierValues> newAttributeModifiers) {
        assertMutable();
        Checks.nonNull(newAttributeModifiers);
        this.attributeModifiers = Mutability.createDeepCopy(newAttributeModifiers, false);
    }

    public void setDefaultEnchantments(Collection<EnchantmentValues> newDefaultEnchantments) {
        assertMutable();
        Checks.nonNull(newDefaultEnchantments);
        this.defaultEnchantments = Mutability.createDeepCopy(newDefaultEnchantments, false);
    }

    public void setPlayerEffects(Collection<ChancePotionEffectValues> newPlayerEffects) {
        assertMutable();
        Checks.nonNull(newPlayerEffects);
        this.playerEffects = Mutability.createDeepCopy(newPlayerEffects, false);
    }

    public void setTargetEffects(Collection<ChancePotionEffectValues> newTargetEffects) {
        assertMutable();
        Checks.nonNull(newTargetEffects);
        this.targetEffects = Mutability.createDeepCopy(newTargetEffects, false);
    }

    public void setEquippedEffects(Collection<EquippedPotionEffectValues> newEquippedEffects) {
        assertMutable();
        Checks.nonNull(newEquippedEffects);
        this.equippedEffects = Mutability.createDeepCopy(equippedEffects, false);
    }

    public void setCommandSystem(ItemCommandSystem newCommandSystem) {
        assertMutable();
        Checks.nonNull(newCommandSystem);
        this.commandSystem = newCommandSystem;
    }

    public void setConditionOp(ReplacementConditionValues.ConditionOperation newConditionOp) {
        assertMutable();
        Checks.notNull(newConditionOp);
        this.conditionOp = newConditionOp;
    }

    public void setReplaceConditions(List<ReplacementConditionValues> newReplaceConditions) {
        assertMutable();
        Checks.nonNull(newReplaceConditions);
        this.replaceConditions = Mutability.createDeepCopy(newReplaceConditions, false);
    }

    public void setExtraItemNbt(ExtraItemNbtValues newExtraNbt) {
        assertMutable();
        Checks.notNull(newExtraNbt);
        this.extraItemNbt = newExtraNbt.copy(false);
    }

    public void setAttackRange(float newAttackRange) {
        assertMutable();
        this.attackRange = newAttackRange;
    }

    public void setSpecialMeleeDamage(SpecialMeleeDamageValues newSpecialDamage) {
        assertMutable();
        this.specialMeleeDamage = newSpecialDamage;
    }

    public void setAttackEffects(Collection<AttackEffectGroupValues> attackEffects) {
        assertMutable();
        Checks.nonNull(attackEffects);
        this.attackEffects = Mutability.createDeepCopy(attackEffects, false);
    }

    public void setUpdateAutomatically(boolean updateAutomatically) {
        assertMutable();
        this.updateAutomatically = updateAutomatically;
    }

    public void setTexture(TextureReference newTexture) {
        assertMutable();
        Checks.notNull(newTexture);
        this.texture = newTexture;
    }

    public void setCustomModel(byte[] newModel) {
        assertMutable();
        this.customModel = CollectionHelper.arrayCopy(newModel);
    }

    private void setBooleanRepresentation(byte[] newRepresentation) {
        this.booleanRepresentation = CollectionHelper.arrayCopy(newRepresentation);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (itemType == CustomItemType.OTHER && otherMaterial == null) {
            throw new ProgrammingValidationException("Other material can't be null when itemType is OTHER");
        }
        if (itemType != CustomItemType.OTHER && otherMaterial != null) {
            throw new ProgrammingValidationException("Other material must be null when itemType is not OTHER");
        }

        Validation.safeName(name);
        
        if (alias == null) throw new ProgrammingValidationException("No alias");

        if (displayName == null) throw new ProgrammingValidationException("No display name");
        if (lore == null) throw new ProgrammingValidationException("No lore");
        if (lore.size() > Byte.MAX_VALUE) throw new ValidationException("Too many lines of lore");
        for (String loreLine : lore) {
            if (loreLine == null) throw new ProgrammingValidationException("Missing a lore line");
        }

        if (itemFlags == null) throw new ProgrammingValidationException("No item flags");
        if (itemFlags.size() != 6) throw new ProgrammingValidationException("Number of item flags is not 6");

        if (attributeModifiers == null) throw new ProgrammingValidationException("No attribute modifiers");
        if (attributeModifiers.size() > Byte.MAX_VALUE) throw new ValidationException("Too many attribute modifiers");
        for (AttributeModifierValues attributeModifier : attributeModifiers) {
            if (attributeModifier == null) throw new ProgrammingValidationException("Missing an attribute modifier");
            Validation.scope("Attribute modifier", attributeModifier::validate);
        }

        if (defaultEnchantments == null) throw new ProgrammingValidationException("No default enchantments");
        if (defaultEnchantments.size() > Byte.MAX_VALUE) throw new ValidationException("Too many default enchantments");
        for (EnchantmentValues enchantment : defaultEnchantments) {
            if (enchantment == null) throw new ProgrammingValidationException("Missing a default enchantment");
            Validation.scope("Default enchantment", enchantment::validate);
        }

        if (playerEffects == null) throw new ProgrammingValidationException("No on-hit player effects");
        if (playerEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit player effects");
        for (ChancePotionEffectValues effect : playerEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit player effect");
            Validation.scope("On-hit player effect", effect::validate);
        }

        if (targetEffects == null) throw new ProgrammingValidationException("No on-hit target effects");
        if (targetEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit target effects");
        for (ChancePotionEffectValues effect : targetEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit target effect");
            Validation.scope("On-hit target effect", effect::validate);
        }

        if (equippedEffects == null) throw new ProgrammingValidationException("No equipped effects");
        for (EquippedPotionEffectValues effect : equippedEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an equipped effect");
            Validation.scope("Equipped effect", effect::validate);
        }

        if (commandSystem == null) throw new ProgrammingValidationException("No command system");
        Validation.scope("Command system", commandSystem::validate);

        if (conditionOp == null) throw new ProgrammingValidationException("No condition OP");
        if (replaceConditions == null) throw new ProgrammingValidationException("No replace conditions");
        if (replaceConditions.size() > Byte.MAX_VALUE) throw new ValidationException("Too many replace conditions");
        for (ReplacementConditionValues condition : replaceConditions) {
            if (condition == null) throw new ProgrammingValidationException("Missing a replacement condition");
            Validation.scope("Replace condition", condition::validateIndependent);
        }
        if (conditionOp == ReplacementConditionValues.ConditionOperation.NONE && replaceConditions.size() > 1) {
            throw new ValidationException("There are multiple replace conditions but no operator has been specified");
        }
        if (conditionOp == ReplacementConditionValues.ConditionOperation.AND || conditionOp == ReplacementConditionValues.ConditionOperation.OR) {
            for (ReplacementConditionValues conditionA : replaceConditions) {
                for (ReplacementConditionValues conditionB : replaceConditions) {
                    if (!conditionA.getReplaceItemReference().equals(conditionB.getReplaceItemReference())) {
                        throw new ValidationException("With the OR and AND operators, all replacement items must be the same");
                    }
                }
            }
        }

        if (extraItemNbt == null) throw new ProgrammingValidationException("No extra item NBT");
        Validation.scope("NBT", extraItemNbt::validate);

        if (attackRange < 0f) throw new ValidationException("Attack range can't be negative");
        if (attackRange != attackRange) throw new ValidationException("Attack range can't be NaN");

        if (specialMeleeDamage != null) Validation.scope("Special melee damage", specialMeleeDamage::validate);

        if (attackEffects == null) throw new ProgrammingValidationException("No attack effects");
        for (AttackEffectGroupValues attackEffectGroup : this.attackEffects) {
            Validation.scope("Attack effects", attackEffectGroup::validate);
        }

        if (texture == null) throw new ValidationException("No texture");
        // customModel doesn't have any invalid values
    }

    public void validateComplete(
            ItemSet itemSet, String oldName
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (oldName != null && !oldName.equals(name)) {
            throw new ProgrammingValidationException("Changing the name of a custom item should not be possible");
        }
        if (oldName == null && itemSet.getItem(name).isPresent()) {
            throw new ValidationException("A custom item with name " + name + " already exists");
        }

        if (oldName == null && itemSet.hasItemBeenDeleted(name)) {
            throw new ValidationException("A custom item with name " + name + " was once deleted");
        }

        if (!itemSet.isReferenceValid(texture)) {
            throw new ProgrammingValidationException("The chosen texture is not (or no longer) valid");
        }

        for (ReplacementConditionValues condition : replaceConditions) {
            Validation.scope("Replace condition", () -> condition.validateComplete(itemSet));
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (itemType.firstVersion > version) {
            throw new ValidationException(itemType + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (itemType.lastVersion < version) {
            throw new ValidationException(itemType + " is no longer supported in mc " + MCVersions.createString(version));
        }
        if (otherMaterial != null) {
            if (otherMaterial.firstVersion > version) {
                throw new ValidationException(otherMaterial + " doesn't exist yet in mc " + MCVersions.createString(version));
            }
            if (otherMaterial.lastVersion < version) {
                throw new ValidationException(otherMaterial + " doesn't exist anymore in mc " + MCVersions.createString(version));
            }
        }

        for (EnchantmentValues enchantment : defaultEnchantments) {
            Validation.scope("Default enchantment", () -> enchantment.validateExportVersion(version));
        }

        for (ChancePotionEffectValues effect : playerEffects) {
            Validation.scope("On-hit player effect", () -> effect.validateExportVersion(version));
        }

        for (ChancePotionEffectValues effect : targetEffects) {
            Validation.scope("On-hit target effect", () -> effect.validateExportVersion(version));
        }

        for (EquippedPotionEffectValues effect : equippedEffects) {
            Validation.scope("Equipped effect", () -> effect.validateExportVersion(version));
        }

        if (specialMeleeDamage != null && specialMeleeDamage.getDamageSource() != null) {
            if (specialMeleeDamage.getDamageSource().minVersion > version) {
                throw new ValidationException("Special melee damage: " + specialMeleeDamage.getDamageSource() + " doesn't exist yet");
            }
            if (specialMeleeDamage.getDamageSource().maxVersion < version) {
                throw new ValidationException("Special melee damage: " + specialMeleeDamage.getDamageSource() + " no longer exists");
            }
        }

        for (AttackEffectGroupValues attackEffectGroup : attackEffects) {
            Validation.scope("Attack effects", () -> attackEffectGroup.validateExportVersion(version));
        }
    }
}
