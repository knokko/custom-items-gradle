package nl.knokko.customitems.item;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;
import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.attack.effect.AttackEffectGroup;
import nl.knokko.customitems.damage.KciDamageSource;
import nl.knokko.customitems.damage.SpecialMeleeDamage;
import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.item.model.LegacyCustomItemModel;
import nl.knokko.customitems.itemset.DamageSourceReference;
import nl.knokko.customitems.itemset.FakeItemSet;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitInputTracker;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nl.knokko.customitems.MCVersions.VERSION1_13;
import static nl.knokko.customitems.encoding.ItemEncoding.*;
import static nl.knokko.customitems.item.model.ItemModel.MODEL_TYPE_NONE;
import static nl.knokko.customitems.util.Checks.isClose;

public abstract class KciItem extends ModelValues {

    public static final long UNBREAKABLE_TOOL_DURABILITY = -1;

    private static DefaultItemModel createDefaultItemModel(DefaultModelType modelType) {
        if (modelType != null) {
            return new DefaultItemModel(modelType.recommendedParents.get(0));
        } else {
            return null;
        }
    }

    public static KciItem loadFromBooleanRepresentation(byte[] booleanRepresentation) throws UnknownEncodingException {
        BitInput input = new ByteArrayBitInput(booleanRepresentation);

        return loadRaw(input, new FakeItemSet(), true);
    }

    public static KciItem load(
            BitInput input, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {

        BitInputTracker inputTracker = new BitInputTracker(input, 100);
        KciItem loadedItem = loadRaw(inputTracker, itemSet, checkCustomModel);
        loadedItem.setBooleanRepresentation(inputTracker.getReadBytes());
        return loadedItem;
    }

    private static KciItem loadRaw(
            BitInput input, ItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (
                encoding == ENCODING_ARMOR_4 || encoding == ENCODING_ARMOR_6 || encoding == ENCODING_ARMOR_7
                || encoding == ENCODING_ARMOR_8 || encoding == ENCODING_ARMOR_9 || encoding == ENCODING_ARMOR_10
                || encoding == ENCODING_ARMOR_11 || encoding == ENCODING_ARMOR_12
        ) {
            return KciArmor.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_BLOCK_ITEM_10 || encoding == ENCODING_BLOCK_ITEM_12) {
            return KciBlockItem.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_BOW_3 || encoding == ENCODING_BOW_4 || encoding == ENCODING_BOW_6
                || encoding == ENCODING_BOW_9 || encoding == ENCODING_BOW_10 || encoding == ENCODING_BOW_12
        ) {
            return KciBow.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_CROSSBOW_10 || encoding == ENCODING_CROSSBOW_12) {
            return KciCrossbow.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_FOOD_10 || encoding == ENCODING_FOOD_12) {
            return KciFood.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_GUN_10 || encoding == ENCODING_GUN_12) {
            return KciGun.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_HELMET3D_10 || encoding == ENCODING_HELMET3D_11 ||
                encoding == ENCODING_HELMET3D_12) {
            return Kci3dHelmet.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_HOE_6 || encoding == ENCODING_HOE_9 ||
                encoding == ENCODING_HOE_10 || encoding == ENCODING_HOE_12) {
            return KciHoe.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_POCKET_CONTAINER_10 || encoding == ENCODING_POCKET_CONTAINER_12) {
            return KciPocketContainer.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_SHEAR_6 || encoding == ENCODING_SHEAR_9 ||
                encoding == ENCODING_SHEAR_10 || encoding == ENCODING_SHEARS_12) {
            return KciShears.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_SHIELD_7 || encoding == ENCODING_SHIELD_9 ||
                encoding == ENCODING_SHIELD_10 || encoding == ENCODING_SHIELD_12) {
            return KciShield.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_TOOL_2 || encoding == ENCODING_TOOL_3 || encoding == ENCODING_TOOL_4
                || encoding == ENCODING_TOOL_6 || encoding == ENCODING_TOOL_9 || encoding == ENCODING_TOOL_10
                || encoding == ENCODING_TOOL_12
        ) {
            return KciTool.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_TRIDENT_8 || encoding == ENCODING_TRIDENT_9 ||
                encoding == ENCODING_TRIDENT_10 || encoding == ENCODING_TRIDENT_12) {
            return KciTrident.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_WAND_9 || encoding == ENCODING_WAND_10 || encoding == ENCODING_WAND_12) {
            return KciWand.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_SIMPLE_1 || encoding == ENCODING_SIMPLE_2 || encoding == ENCODING_SIMPLE_4
                || encoding == ENCODING_SIMPLE_5 || encoding == ENCODING_SIMPLE_6 || encoding == ENCODING_SIMPLE_9
                || encoding == ENCODING_SIMPLE_10 || encoding == ENCODING_SIMPLE_12
        ) {
            return KciSimpleItem.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_ELYTRA_12) {
            return KciElytra.load(input, false, itemSet);
        } else if (encoding == ENCODING_MUSIC_DISC) {
            return KciMusicDisc.load(input, itemSet);
        } else if (encoding == ENCODING_ARROW) {
            return KciArrow.load(input, itemSet);
        } else if (encoding == ENCODING_THROWABLE) {
            return KciThrowable.load(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomItem", encoding);
        }
    }

    // Identity properties
    protected KciItemType itemType;
    protected VMaterial otherMaterial;
    protected short itemDamage;
    protected String name;
    protected String alias;

    // Text display properties
    protected String displayName;
    protected List<String> lore;
    protected Collection<TranslationEntry> translations;

    // Item flags (they are not in a group)
    protected List<Boolean> itemFlags;

    // Vanilla based power properties
    protected Collection<KciAttributeModifier> attributeModifiers;
    protected Collection<LeveledEnchantment> defaultEnchantments;

    // Potion properties
    protected Collection<ChancePotionEffect> playerEffects;
    protected Collection<ChancePotionEffect> targetEffects;
    protected Collection<EquippedPotionEffect> equippedEffects;

    // Right-click properties
    protected ReplacementConditionEntry.ConditionOperation conditionOp;
    protected List<ReplacementConditionEntry> replaceConditions;

    // Other properties
    protected ItemCommandSystem commandSystem;
    protected List<String> extraItemNbt;
    protected float attackRange;
    protected SpecialMeleeDamage specialMeleeDamage;
    protected DamageSourceReference customMeleeDamageSource;
    protected Collection<AttackEffectGroup> attackEffects;
    protected boolean updateAutomatically;
    protected boolean keepOnDeath;
    protected MultiBlockBreak multiBlockBreak;
    protected boolean isTwoHanded;
    protected boolean indestructible;

    // Editor-only properties
    protected TextureReference texture;
    protected ItemModel model;
    protected WikiVisibility wikiVisibility;

    // Plugin-only properties
    private byte[] booleanRepresentation;

    public KciItem(boolean mutable, KciItemType initialItemType) {
        super(mutable);

        this.itemType = initialItemType;
        if (initialItemType == KciItemType.OTHER) {
            this.otherMaterial = VMaterial.FLINT;
        } else {
            this.otherMaterial = null;
        }
        this.itemDamage = 0; // This will be taken care of later
        this.name = "";
        this.alias = "";

        this.displayName = "&f";
        this.lore = new ArrayList<>(0);
        this.translations = Collections.emptyList();

        this.itemFlags = VItemFlag.getDefaultValuesList();

        this.attributeModifiers = new ArrayList<>(0);
        this.defaultEnchantments = new ArrayList<>(0);

        this.playerEffects = new ArrayList<>(0);
        this.targetEffects = new ArrayList<>(0);
        this.equippedEffects = new ArrayList<>(0);

        this.conditionOp = ReplacementConditionEntry.ConditionOperation.NONE;
        this.replaceConditions = new ArrayList<>(0);

        this.commandSystem = new ItemCommandSystem(false);
        this.extraItemNbt = Collections.emptyList();
        this.attackRange = 1f;
        this.specialMeleeDamage = null;
        this.customMeleeDamageSource = null;
        this.attackEffects = new ArrayList<>();
        this.updateAutomatically = true;
        this.keepOnDeath = false;
        this.multiBlockBreak = new MultiBlockBreak(false);
        this.isTwoHanded = false;
        this.indestructible = false;

        this.texture = null;
        this.model = createDefaultItemModel(getDefaultModelType());
        this.wikiVisibility = WikiVisibility.VISIBLE;
    }

    public KciItem(KciItem source, boolean mutable) {
        super(mutable);

        this.itemType = source.getItemType();
        this.otherMaterial = source.getOtherMaterial();
        this.itemDamage = source.getItemDamage();
        this.name = source.getName();
        this.alias = source.getAlias();
        this.displayName = source.getDisplayName();
        this.lore = source.getLore();
        this.translations = source.getTranslations();
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
        this.customMeleeDamageSource = source.getCustomMeleeDamageSourceReference();
        this.attackEffects = source.getAttackEffects();
        this.updateAutomatically = source.shouldUpdateAutomatically();
        this.keepOnDeath = source.shouldKeepOnDeath();
        this.multiBlockBreak = source.getMultiBlockBreak();
        this.isTwoHanded = source.isTwoHanded();
        this.indestructible = source.isIndestructible();
        this.texture = source.getTextureReference();
        this.model = source.getModel();
        this.wikiVisibility = source.getWikiVisibility();
        this.booleanRepresentation = source.getBooleanRepresentation();
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    protected boolean areBaseItemPropertiesEqual(KciItem other) {
        return this.itemType == other.itemType && this.otherMaterial == other.otherMaterial
                && this.name.equals(other.name) && this.alias.equals(other.alias)
                && this.displayName.equals(other.displayName) && this.lore.equals(other.lore)
                && this.translations.equals(other.translations)
                && this.itemFlags.equals(other.itemFlags) && this.attributeModifiers.equals(other.attributeModifiers)
                && this.defaultEnchantments.equals(other.defaultEnchantments) && this.playerEffects.equals(other.playerEffects)
                && this.targetEffects.equals(other.targetEffects) && this.equippedEffects.equals(other.equippedEffects)
                && this.commandSystem.equals(other.commandSystem) && this.replaceConditions.equals(other.replaceConditions)
                && this.conditionOp == other.conditionOp && this.extraItemNbt.equals(other.extraItemNbt)
                && isClose(this.attackRange, other.attackRange) && Objects.equals(this.specialMeleeDamage, other.specialMeleeDamage)
                && Objects.equals(this.customMeleeDamageSource, other.customMeleeDamageSource)
                && this.attackEffects.equals(other.attackEffects) && this.updateAutomatically == other.updateAutomatically
                && this.keepOnDeath == other.keepOnDeath && this.multiBlockBreak.equals(other.multiBlockBreak)
                && this.isTwoHanded == other.isTwoHanded && this.indestructible == other.indestructible;
    }

    public DefaultModelType getDefaultModelType() {
        return DefaultModelType.BASIC;
    }

    @Override
    public abstract KciItem copy(boolean mutable);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + name + ")";
    }

    public abstract void save(BitOutput output, ItemSet.Side side);

    protected void loadEditorOnlyProperties1(BitInput input, ItemSet itemSet, boolean checkCustomModel) {
        String textureName = input.readJavaString();
        this.texture = itemSet.textures.getReference(textureName);

        if (checkCustomModel && input.readBoolean()) {
            this.model = new LegacyCustomItemModel(input.readByteArray());
        } else {
            this.model = createDefaultItemModel(getDefaultModelType());
        }
    }

    protected void loadSharedPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 5) throw new UnknownEncodingException("CustomItemBaseNew", encoding);

        this.loadIdentityProperties10(input);
        if (this.itemType == KciItemType.OTHER) {
            this.otherMaterial = VMaterial.valueOf(input.readString());
        } else this.otherMaterial = null;
        this.loadTextDisplayProperties1(input);
        int numItemFlags = input.readInt();
        this.itemFlags = new ArrayList<>(numItemFlags);
        for (int counter = 0; counter < numItemFlags; counter++) {
            this.itemFlags.add(input.readBoolean());
        }
        while (itemFlags.size() < VItemFlag.values().length) itemFlags.add(false);

        this.loadVanillaBasedPowers4(input);
        this.playerEffects = this.loadChanceEffects(input);
        this.targetEffects = this.loadChanceEffects(input);
        this.loadEquippedPotionEffects10(input);
        this.loadReplacementConditions10(input, itemSet);
        this.commandSystem = ItemCommandSystem.load(input);
        if (encoding >= 5) this.attackRange = input.readFloat();
        else this.loadExtraProperties10(input);
        if (encoding >= 2) {
            if (input.readBoolean()) {
                this.specialMeleeDamage = SpecialMeleeDamage.load(input);
            } else {
                this.specialMeleeDamage = null;
            }
            int numAttackEffectGroups = input.readInt();
            this.attackEffects = new ArrayList<>(numAttackEffectGroups);
            for (int counter = 0; counter < numAttackEffectGroups; counter++) {
                this.attackEffects.add(AttackEffectGroup.load(input, itemSet));
            }
            this.updateAutomatically = input.readBoolean();
            this.keepOnDeath = input.readBoolean();
            this.multiBlockBreak = MultiBlockBreak.load(input);
        } else {
            this.specialMeleeDamage = null;
            this.attackEffects = new ArrayList<>();
            this.updateAutomatically = true;
            this.keepOnDeath = false;
            this.multiBlockBreak = new MultiBlockBreak(false);
        }

        if (encoding >= 4) {
            this.isTwoHanded = input.readBoolean();
            this.indestructible = input.readBoolean();
            if (input.readBoolean()) this.customMeleeDamageSource = itemSet.damageSources.getReference(new UUID(
                    input.readLong(), input.readLong())
            ); else this.customMeleeDamageSource = null;
        } else {
            this.isTwoHanded = false;
            this.indestructible = false;
            this.customMeleeDamageSource = null;
        }

        if (encoding >= 5) {
            this.extraItemNbt = CollectionHelper.load(input, input2 -> input2.readString());
            this.translations = CollectionHelper.load(input, input2 -> TranslationEntry.load(input2, false));
        } else {
            this.translations = Collections.emptyList();
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            if (encoding >= 3) {
                String textureName = input.readString();
                this.texture = itemSet.textures.getReference(textureName);
                this.model = ItemModel.load(input);
                if (encoding >= 4) {
                    this.wikiVisibility = WikiVisibility.valueOf(input.readString());
                } else {
                    this.wikiVisibility = WikiVisibility.VISIBLE;
                }
            } else {
                this.loadEditorOnlyProperties1(input, itemSet, true);
            }
        }
    }

    protected void saveSharedPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        output.addByte((byte) 5);

        this.saveIdentityProperties10(output);
        if (this.itemType == KciItemType.OTHER) {
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
        output.addFloat(attackRange);
        output.addBoolean(this.specialMeleeDamage != null);
        if (this.specialMeleeDamage != null) {
            this.specialMeleeDamage.save(output);
        }
        output.addInt(this.attackEffects.size());
        for (AttackEffectGroup attackEffectGroup : this.attackEffects) {
            attackEffectGroup.save(output);
        }
        output.addBoolean(this.updateAutomatically);
        output.addBoolean(this.keepOnDeath);
        this.multiBlockBreak.save(output);
        output.addBoolean(this.isTwoHanded);
        output.addBoolean(this.indestructible);
        output.addBoolean(this.customMeleeDamageSource != null);
        if (this.customMeleeDamageSource != null) {
            output.addLong(this.customMeleeDamageSource.get().getId().getMostSignificantBits());
            output.addLong(this.customMeleeDamageSource.get().getId().getLeastSignificantBits());
        }

        CollectionHelper.save(extraItemNbt, output::addString, output);
        CollectionHelper.save(translations, translationEntry -> translationEntry.save(output), output);

        if (targetSide == ItemSet.Side.EDITOR) {
            output.addString(texture.get().getName());
            if (model != null) model.save(output);
            else output.addByte(MODEL_TYPE_NONE);
            output.addString(wikiVisibility.name());
        }
    }

    protected void loadIdentityProperties1(BitInput input) {
        this.itemType = KciItemType.valueOf(input.readJavaString());
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
        while (itemFlags.size() < VItemFlag.values().length) itemFlags.add(false);
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
            this.attributeModifiers.add(KciAttributeModifier.load1(input, false));
        }
    }

    protected void saveAttributeModifiers2(BitOutput output) {
        output.addByte((byte) attributeModifiers.size());
        for (KciAttributeModifier attributeModifier : attributeModifiers) {
            attributeModifier.save1(output);
        }
    }

    protected void loadDefaultEnchantments4(BitInput input) {
        int numDefaultEnchantments = input.readByte() & 0xFF;
        this.defaultEnchantments = new ArrayList<>(numDefaultEnchantments);
        for (int counter = 0; counter < numDefaultEnchantments; counter++) {
            this.defaultEnchantments.add(LeveledEnchantment.load1(input, false));
        }
    }

    protected void saveDefaultEnchantments4(BitOutput output) {
        output.addByte((byte) defaultEnchantments.size());
        for (LeveledEnchantment defaultEnchantment : defaultEnchantments) {
            defaultEnchantment.save1(output);
        }
    }

    protected void saveChanceEffects(BitOutput output, Collection<ChancePotionEffect> effects) {
        output.addInt(effects.size());
        for (ChancePotionEffect effect : effects) {
            effect.save(output);
        }
    }

    protected Collection<ChancePotionEffect> loadChanceEffects(BitInput input) throws UnknownEncodingException {
        int numEffects = input.readInt();
        Collection<ChancePotionEffect> result = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            result.add(ChancePotionEffect.load(input));
        }
        return result;
    }

    protected void loadPotionProperties9(BitInput input) {
        loadOnHitPlayerEffects9(input);
        loadOnHitTargetEffects9(input);
    }

    protected void loadOnHitPlayerEffects9(BitInput input) {
        Collection<KciPotionEffect> rawEffectList = loadPotionEffectList(input);
        this.playerEffects = rawEffectList.stream().map(
                rawEffect -> ChancePotionEffect.createQuick(rawEffect, Chance.percentage(100))
        ).collect(Collectors.toList());
    }

    protected void loadOnHitTargetEffects9(BitInput input) {
        Collection<KciPotionEffect> rawEffectList = loadPotionEffectList(input);
        this.targetEffects = rawEffectList.stream().map(
                rawEffect -> ChancePotionEffect.createQuick(rawEffect, Chance.percentage(100))
        ).collect(Collectors.toList());
    }

    protected Collection<KciPotionEffect> loadPotionEffectList(BitInput input) {
        int numEffects = input.readByte() & 0xFF;
        Collection<KciPotionEffect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(KciPotionEffect.load1(input, false));
        }
        return effects;
    }

    protected void savePotionEffectList(Collection<KciPotionEffect> effects, BitOutput output) {
        output.addByte((byte) effects.size());
        for (KciPotionEffect effect : effects) {
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
            this.equippedEffects.add(EquippedPotionEffect.load1(input, false));
        }
    }

    protected void saveEquippedPotionEffects10(BitOutput output) {
        output.addInt(equippedEffects.size());
        for (EquippedPotionEffect effect : equippedEffects) {
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
            this.replaceConditions.add(ReplacementConditionEntry.load1(input, itemSet, false));
        }
        this.conditionOp = ReplacementConditionEntry.ConditionOperation.valueOf(input.readJavaString());
    }

    protected void saveReplacementConditions10(BitOutput output) {
        output.addByte((byte) replaceConditions.size());
        for (ReplacementConditionEntry replaceCondition : replaceConditions) {
            replaceCondition.save1(output);
        }
        output.addJavaString(conditionOp.name());
    }

    protected void loadExtraProperties10(BitInput input) throws UnknownEncodingException {
        this.extraItemNbt = LegacyItemNbt.load(input);
        this.attackRange = input.readFloat();
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

    private void initBaseDefaults12() {
        this.translations = Collections.emptyList();
    }

    protected void initBaseDefaults11() {
        initBaseDefaults12();

        this.isTwoHanded = false;
        this.indestructible = false;
        this.customMeleeDamageSource = null;
    }

    protected void initBaseDefaults10() {
        initBaseDefaults11();

        this.specialMeleeDamage = null;
        this.attackEffects = new ArrayList<>(0);
        this.updateAutomatically = true;
        this.keepOnDeath = false;
        this.multiBlockBreak = new MultiBlockBreak(false);
        this.otherMaterial = null;
    }

    protected void initBaseDefaults9() {
        initBaseDefaults10();

        this.alias = "";

        this.equippedEffects = new ArrayList<>(0);

        this.conditionOp = ReplacementConditionEntry.ConditionOperation.NONE;
        this.replaceConditions = new ArrayList<>(0);

        this.extraItemNbt = Collections.emptyList();
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

        this.itemFlags = VItemFlag.getDefaultValuesList();
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

    public KciItemType getItemType() {
        return itemType;
    }

    public VMaterial getOtherMaterial() {
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

    public Collection<TranslationEntry> getTranslations() {
        return translations;
    }

    public List<Boolean> getItemFlags() {
        return new ArrayList<>(itemFlags);
    }

    public Collection<KciAttributeModifier> getAttributeModifiers() {
        return new ArrayList<>(attributeModifiers);
    }

    public Collection<LeveledEnchantment> getDefaultEnchantments() {
        return new ArrayList<>(defaultEnchantments);
    }

    public Collection<ChancePotionEffect> getOnHitPlayerEffects() {
        return new ArrayList<>(playerEffects);
    }

    public Collection<ChancePotionEffect> getOnHitTargetEffects() {
        return new ArrayList<>(targetEffects);
    }

    public Collection<EquippedPotionEffect> getEquippedEffects() {
        return new ArrayList<>(equippedEffects);
    }

    public ItemCommandSystem getCommandSystem() {
        return this.commandSystem;
    }

    public List<ReplacementConditionEntry> getReplacementConditions() {
        return new ArrayList<>(replaceConditions);
    }

    public ReplacementConditionEntry.ConditionOperation getConditionOp() {
        return conditionOp;
    }

    public List<String> getExtraNbt() {
        return extraItemNbt;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public SpecialMeleeDamage getSpecialMeleeDamage() {
        return specialMeleeDamage;
    }

    public DamageSourceReference getCustomMeleeDamageSourceReference() {
        return customMeleeDamageSource;
    }

    public KciDamageSource getCustomMeleeDamageSource() {
        return customMeleeDamageSource != null ? customMeleeDamageSource.get() : null;
    }

    public Collection<AttackEffectGroup> getAttackEffects() {
        return new ArrayList<>(attackEffects);
    }

    public boolean shouldUpdateAutomatically() {
        return updateAutomatically;
    }

    public boolean shouldKeepOnDeath() {
        return keepOnDeath;
    }

    public MultiBlockBreak getMultiBlockBreak() {
        return multiBlockBreak;
    }

    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public boolean isIndestructible() {
        return indestructible;
    }

    public KciTexture getTexture() {
        return texture.get();
    }

    public TextureReference getTextureReference() {
        return texture;
    }

    public ItemModel getModel() {
        return model;
    }

    public WikiVisibility getWikiVisibility() {
        return wikiVisibility;
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

    public void setItemType(KciItemType newItemType) {
        assertMutable();
        Checks.nonNull(newItemType);
        if (newItemType != this.itemType) {
            if (newItemType == KciItemType.OTHER) {
                this.otherMaterial = VMaterial.FLINT;
            } else {
                this.otherMaterial = null;
            }
            this.itemType = newItemType;
        }
    }

    public void setOtherMaterial(VMaterial newOtherMaterial) {
        assertMutable();
        if (newOtherMaterial == null) {
            if (this.itemType == KciItemType.OTHER) {
                throw new UnsupportedOperationException("newOtherMaterial can't be null when itemType is OTHER");
            }
        } else {
            if (this.itemType != KciItemType.OTHER) {
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

    public void setTranslations(Collection<TranslationEntry> newTranslations) {
        assertMutable();
        this.translations = Collections.unmodifiableCollection(newTranslations);
    }

    public void setItemFlags(List<Boolean> newItemFlags) {
        assertMutable();
        Checks.nonNull(newItemFlags);
        this.itemFlags = new ArrayList<>(newItemFlags);
    }

    public void setAttributeModifiers(Collection<KciAttributeModifier> newAttributeModifiers) {
        assertMutable();
        Checks.nonNull(newAttributeModifiers);
        this.attributeModifiers = Mutability.createDeepCopy(newAttributeModifiers, false);
    }

    public void setDefaultEnchantments(Collection<LeveledEnchantment> newDefaultEnchantments) {
        assertMutable();
        Checks.nonNull(newDefaultEnchantments);
        this.defaultEnchantments = Mutability.createDeepCopy(newDefaultEnchantments, false);
    }

    public void setPlayerEffects(Collection<ChancePotionEffect> newPlayerEffects) {
        assertMutable();
        Checks.nonNull(newPlayerEffects);
        this.playerEffects = Mutability.createDeepCopy(newPlayerEffects, false);
    }

    public void setTargetEffects(Collection<ChancePotionEffect> newTargetEffects) {
        assertMutable();
        Checks.nonNull(newTargetEffects);
        this.targetEffects = Mutability.createDeepCopy(newTargetEffects, false);
    }

    public void setEquippedEffects(Collection<EquippedPotionEffect> newEquippedEffects) {
        assertMutable();
        Checks.nonNull(newEquippedEffects);
        this.equippedEffects = Mutability.createDeepCopy(newEquippedEffects, false);
    }

    public void setCommandSystem(ItemCommandSystem newCommandSystem) {
        assertMutable();
        Checks.nonNull(newCommandSystem);
        this.commandSystem = newCommandSystem;
    }

    public void setConditionOp(ReplacementConditionEntry.ConditionOperation newConditionOp) {
        assertMutable();
        Checks.notNull(newConditionOp);
        this.conditionOp = newConditionOp;
    }

    public void setReplaceConditions(List<ReplacementConditionEntry> newReplaceConditions) {
        assertMutable();
        Checks.nonNull(newReplaceConditions);
        this.replaceConditions = Mutability.createDeepCopy(newReplaceConditions, false);
    }

    public void setExtraItemNbt(List<String> newExtraNbt) {
        assertMutable();
        Checks.nonNull(newExtraNbt);
        this.extraItemNbt = Collections.unmodifiableList(newExtraNbt);
    }

    public void setAttackRange(float newAttackRange) {
        assertMutable();
        this.attackRange = newAttackRange;
    }

    public void setSpecialMeleeDamage(SpecialMeleeDamage newSpecialDamage) {
        assertMutable();
        this.specialMeleeDamage = newSpecialDamage != null ? newSpecialDamage.copy(false) : null;
    }

    public void setCustomMeleeDamageSource(DamageSourceReference newDamageSource) {
        assertMutable();
        this.customMeleeDamageSource = newDamageSource;
    }

    public void setAttackEffects(Collection<AttackEffectGroup> attackEffects) {
        assertMutable();
        Checks.nonNull(attackEffects);
        this.attackEffects = Mutability.createDeepCopy(attackEffects, false);
    }

    public void setUpdateAutomatically(boolean updateAutomatically) {
        assertMutable();
        this.updateAutomatically = updateAutomatically;
    }

    public void setKeepOnDeath(boolean keepOnDeath) {
        assertMutable();
        this.keepOnDeath = keepOnDeath;
    }

    public void setMultiBlockBreak(MultiBlockBreak newBlockBreak) {
        assertMutable();
        Checks.notNull(newBlockBreak);
        this.multiBlockBreak = newBlockBreak.copy(false);
    }

    public void setTwoHanded(boolean shouldBecomeTwoHanded) {
        assertMutable();
        this.isTwoHanded = shouldBecomeTwoHanded;
    }

    public void setIndestructible(boolean indestructible) {
        assertMutable();
        this.indestructible = indestructible;
    }

    public void setTexture(TextureReference newTexture) {
        assertMutable();
        Checks.notNull(newTexture);
        this.texture = newTexture;
    }

    public void setModel(ItemModel newModel) {
        assertMutable();
        this.model = newModel;
    }

    public void setWikiVisibility(WikiVisibility wikiVisibility) {
        assertMutable();
        this.wikiVisibility = Objects.requireNonNull(wikiVisibility);
    }

    private void setBooleanRepresentation(byte[] newRepresentation) {
        this.booleanRepresentation = CollectionHelper.arrayCopy(newRepresentation);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (itemType == KciItemType.OTHER && otherMaterial == null) {
            throw new ProgrammingValidationException("Other material can't be null when itemType is OTHER");
        }
        if (itemType != KciItemType.OTHER && otherMaterial != null) {
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
        if (translations == null) throw new ProgrammingValidationException("No translations");
        int translationLoreSize = -1;
        Set<String> allTranslations = new HashSet<>();
        for (TranslationEntry translation : translations) {
            Validation.scope("Translation " + translation.getLanguage(), translation::validate);
            if (allTranslations.contains(translation.getLanguage())) {
                throw new ValidationException("Multiple translations for " + translation.getLanguage());
            }
            allTranslations.add(translation.getLanguage());
            if (translationLoreSize == -1) translationLoreSize = translation.getLore().size();
            else if (translationLoreSize != translation.getLore().size()) {
                throw new ValidationException("All translation lore must have the same number of lines");
            }
        }

        if (itemFlags == null) throw new ProgrammingValidationException("No item flags");
        if (itemFlags.size() != 8) throw new ProgrammingValidationException("Number of item flags is not 6");

        if (attributeModifiers == null) throw new ProgrammingValidationException("No attribute modifiers");
        if (attributeModifiers.size() > Byte.MAX_VALUE) throw new ValidationException("Too many attribute modifiers");
        for (KciAttributeModifier attributeModifier : attributeModifiers) {
            if (attributeModifier == null) throw new ProgrammingValidationException("Missing an attribute modifier");
            Validation.scope("Attribute modifier", attributeModifier::validate);
        }

        if (defaultEnchantments == null) throw new ProgrammingValidationException("No default enchantments");
        if (defaultEnchantments.size() > Byte.MAX_VALUE) throw new ValidationException("Too many default enchantments");
        for (LeveledEnchantment enchantment : defaultEnchantments) {
            if (enchantment == null) throw new ProgrammingValidationException("Missing a default enchantment");
            Validation.scope("Default enchantment", enchantment::validate);
        }

        if (playerEffects == null) throw new ProgrammingValidationException("No on-hit player effects");
        if (playerEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit player effects");
        for (ChancePotionEffect effect : playerEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit player effect");
            Validation.scope("On-hit player effect", effect::validate);
        }

        if (targetEffects == null) throw new ProgrammingValidationException("No on-hit target effects");
        if (targetEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit target effects");
        for (ChancePotionEffect effect : targetEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit target effect");
            Validation.scope("On-hit target effect", effect::validate);
        }

        if (equippedEffects == null) throw new ProgrammingValidationException("No equipped effects");
        for (EquippedPotionEffect effect : equippedEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an equipped effect");
            Validation.scope("Equipped effect", effect::validate);
        }

        if (commandSystem == null) throw new ProgrammingValidationException("No command system");
        Validation.scope("Command system", commandSystem::validate);

        if (conditionOp == null) throw new ProgrammingValidationException("No condition OP");
        if (replaceConditions == null) throw new ProgrammingValidationException("No replace conditions");
        if (replaceConditions.size() > Byte.MAX_VALUE) throw new ValidationException("Too many replace conditions");
        for (ReplacementConditionEntry condition : replaceConditions) {
            if (condition == null) throw new ProgrammingValidationException("Missing a replacement condition");
            Validation.scope("Replace condition", condition::validateIndependent);
        }
        if (conditionOp == ReplacementConditionEntry.ConditionOperation.NONE && replaceConditions.size() > 1) {
            throw new ValidationException("There are multiple replace conditions but no operator has been specified");
        }
        if (conditionOp == ReplacementConditionEntry.ConditionOperation.AND || conditionOp == ReplacementConditionEntry.ConditionOperation.OR) {
            for (ReplacementConditionEntry conditionA : replaceConditions) {
                for (ReplacementConditionEntry conditionB : replaceConditions) {
                    if (!conditionA.getReplaceItemReference().equals(conditionB.getReplaceItemReference())) {
                        throw new ValidationException("With the OR and AND operators, all replacement items must be the same");
                    }
                }
            }
        }

        if (extraItemNbt == null) throw new ProgrammingValidationException("No extra item NBT");
        for (String nbt : extraItemNbt) {
            if (nbt == null) throw new ProgrammingValidationException("Missing extra item NBT entry");
            if (nbt.isEmpty()) throw new ValidationException("Extra NBT can't have empty entries");
            try {
                Jsoner.deserialize(nbt);
            } catch (JsonException e) {
                throw new ValidationException("NBT entry is invalid JSON: " + nbt);
            }
        }

        if (attackRange < 0f) throw new ValidationException("Attack range can't be negative");
        if (attackRange != attackRange) throw new ValidationException("Attack range can't be NaN");

        if (specialMeleeDamage != null) Validation.scope("Special melee damage", specialMeleeDamage::validate);

        if (attackEffects == null) throw new ProgrammingValidationException("No attack effects");
        for (AttackEffectGroup attackEffectGroup : this.attackEffects) {
            if (attackEffectGroup == null) throw new ProgrammingValidationException("Missing an attack effect");
        }

        if (multiBlockBreak == null) throw new ProgrammingValidationException("No multi block break");
        Validation.scope("Multi block break", multiBlockBreak::validate);

        if (texture == null) throw new ValidationException("No texture");
        if (getDefaultModelType() != null && model == null) throw new ProgrammingValidationException("No model");
        if (wikiVisibility == null) throw new ProgrammingValidationException("No wiki visibility");
    }

    public void validateComplete(
            ItemSet itemSet, String oldName
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (oldName != null && !oldName.equals(name)) {
            throw new ProgrammingValidationException("Changing the name of a custom item should not be possible");
        }
        if (oldName == null && itemSet.items.get(name).isPresent()) {
            throw new ValidationException("A custom item with name " + name + " already exists");
        }

        if (oldName == null && itemSet.hasItemBeenDeleted(name)) {
            throw new ValidationException("A custom item with name " + name + " was once deleted");
        }

        if (!itemSet.textures.isValid(texture)) {
            throw new ProgrammingValidationException("The chosen texture is not (or no longer) valid");
        }

        for (ReplacementConditionEntry condition : replaceConditions) {
            Validation.scope("Replace condition", () -> condition.validateComplete(itemSet));
        }

        for (AttackEffectGroup attackEffectGroup : attackEffects) {
            Validation.scope("Attack effects", attackEffectGroup::validate, itemSet);
        }

        if (customMeleeDamageSource != null && !itemSet.damageSources.isValid(customMeleeDamageSource)) {
            throw new ProgrammingValidationException("Custom melee damage source is invalid");
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

        if (version < MCVersions.VERSION1_14 && !translations.isEmpty()) {
            throw new ValidationException("Translations require MC 1.14+");
        }

        for (LeveledEnchantment enchantment : defaultEnchantments) {
            Validation.scope("Default enchantment", () -> enchantment.validateExportVersion(version));
        }

        for (ChancePotionEffect effect : playerEffects) {
            Validation.scope("On-hit player effect", () -> effect.validateExportVersion(version));
        }

        for (ChancePotionEffect effect : targetEffects) {
            Validation.scope("On-hit target effect", () -> effect.validateExportVersion(version));
        }

        for (EquippedPotionEffect effect : equippedEffects) {
            Validation.scope("Equipped effect", () -> effect.validateExportVersion(version));
        }

        if (specialMeleeDamage != null && version >= MCVersions.VERSION1_19) {
            throw new ValidationException("Special melee damage is only supported in MC 1.18 and earlier");
        }

        if (specialMeleeDamage != null && specialMeleeDamage.getDamageSource() != null) {
            if (specialMeleeDamage.getDamageSource().minVersion > version) {
                throw new ValidationException("Special melee damage: " + specialMeleeDamage.getDamageSource() + " doesn't exist yet");
            }
            if (specialMeleeDamage.getDamageSource().maxVersion < version) {
                throw new ValidationException("Special melee damage: " + specialMeleeDamage.getDamageSource() + " no longer exists");
            }
        }

        for (AttackEffectGroup attackEffectGroup : attackEffects) {
            Validation.scope("Attack effects", () -> attackEffectGroup.validateExportVersion(version));
        }

        for (int index = 0; index < itemFlags.size(); index++) {
            if (itemFlags.get(index)) {
                VItemFlag flag = VItemFlag.values()[index];
                if (version < flag.firstVersion || version > flag.lastVersion) {
                    throw new ValidationException("Flag " + flag + " doesn't exist in MC " + MCVersions.createString(version));
                }
            }
        }
    }

    public VMaterial getVMaterial(int mcVersion) {
        if (itemType == KciItemType.OTHER) return otherMaterial;

        String materialName = itemType.name();

        // Bukkit renamed all WOOD_* tools to WOODEN_* tools between MC 1.12 and 1.13
        if (mcVersion >= VERSION1_13) {
            materialName = materialName.replace("WOOD", "WOODEN").replace("GOLD", "GOLDEN");
        } else {
            materialName = materialName.replace("SHOVEL", "SPADE");
        }

        return VMaterial.valueOf(materialName);
    }
}
