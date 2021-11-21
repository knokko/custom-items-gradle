package nl.knokko.customitems.item;

import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static nl.knokko.customitems.encoding.ItemEncoding.*;

public abstract class CustomItemValues extends ModelValues {

    public static CustomItemValues load(
            BitInput input, SItemSet itemSet, boolean checkCustomModel
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (
                encoding == ENCODING_ARMOR_4 || encoding == ENCODING_ARMOR_6 || encoding == ENCODING_ARMOR_7
                || encoding == ENCODING_ARMOR_8 || encoding == ENCODING_ARMOR_9 || encoding == ENCODING_ARMOR_10
                || encoding == ENCODING_ARMOR_11
        ) {
            return CustomArmorValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_BLOCK_ITEM_10) {
            return CustomBlockItemValues.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_BOW_3 || encoding == ENCODING_BOW_4 || encoding == ENCODING_BOW_6
                || encoding == ENCODING_BOW_9 || encoding == ENCODING_BOW_10
        ) {
            return CustomBowValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_CROSSBOW_10) {
            return CustomCrossbowValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_FOOD_10) {
            return CustomFoodValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_GUN_10) {
            return CustomGunValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_HELMET3D_10 || encoding == ENCODING_HELMET3D_11) {
            return CustomGunValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_HOE_6 || encoding == ENCODING_HOE_9 || encoding == ENCODING_HOE_10) {
            return CustomHoeValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_POCKET_CONTAINER_10) {
            return CustomPocketContainerValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_SHEAR_6 || encoding == ENCODING_SHEAR_9 || encoding == ENCODING_SHEAR_10) {
            return CustomShearsValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_SHIELD_7 || encoding == ENCODING_SHIELD_9 || encoding == ENCODING_SHIELD_10) {
            return CustomShieldValues.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_TOOL_2 || encoding == ENCODING_TOOL_3 || encoding == ENCODING_TOOL_4
                || encoding == ENCODING_TOOL_6 || encoding == ENCODING_TOOL_9 || encoding == ENCODING_TOOL_10
        ) {
            return CustomToolValues.load(input, encoding, itemSet, checkCustomModel);
        } else if (encoding == ENCODING_TRIDENT_8 || encoding == ENCODING_TRIDENT_9 || encoding == ENCODING_TRIDENT_10) {
            return CustomTridentValues.load(input, encoding, itemSet);
        } else if (encoding == ENCODING_WAND_9 || encoding == ENCODING_WAND_10) {
            return CustomWandValues.load(input, encoding, itemSet);
        } else if (
                encoding == ENCODING_SIMPLE_1 || encoding == ENCODING_SIMPLE_2 || encoding == ENCODING_SIMPLE_4
                || encoding == ENCODING_SIMPLE_5 || encoding == ENCODING_SIMPLE_6 || encoding == ENCODING_SIMPLE_9
                || encoding == ENCODING_SIMPLE_10
        ) {
            return SimpleCustomItemValues.load(input, encoding, itemSet, checkCustomModel);
        } else {
            throw new UnknownEncodingException("CustomItem", encoding);
        }
    }

    // Identity properties
    protected CustomItemType itemType;
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
    protected Collection<PotionEffectValues> playerEffects;
    protected Collection<PotionEffectValues> targetEffects;
    protected Collection<EquippedPotionEffectValues> equippedEffects;

    // Right-click properties
    protected List<String> commands;
    protected ReplacementConditionValues.ConditionOperation conditionOp;
    protected List<ReplacementConditionValues> replaceConditions;

    // Other properties
    protected ExtraItemNbtValues extraItemNbt;
    protected float attackRange;

    // Editor-only properties
    protected TextureReference texture;
    protected byte[] customModel;

    public CustomItemValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable);

        this.itemType = initialItemType;
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

        this.commands = new ArrayList<>(0);
        this.conditionOp = ReplacementConditionValues.ConditionOperation.NONE;
        this.replaceConditions = new ArrayList<>(0);

        this.extraItemNbt = new ExtraItemNbtValues(false);
        this.attackRange = 1f;

        this.texture = null;
        this.customModel = null;
    }

    public CustomItemValues(CustomItemValues source, boolean mutable) {
        super(mutable);

        this.itemType = source.getItemType();
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
        this.commands = source.getCommands();
        this.replaceConditions = source.getReplacementConditions();
        this.conditionOp = source.getConditionOp();
        this.extraItemNbt = source.getExtraNbt();
        this.attackRange = source.getAttackRange();
        this.texture = source.getTextureReference();
        this.customModel = source.getCustomModel();
    }

    public abstract void save(BitOutput output, SItemSet.Side side);

    protected void loadEditorOnlyProperties1(BitInput input, SItemSet itemSet, boolean checkCustomModel) {
        String textureName = input.readJavaString();
        this.texture = itemSet.getTextureReference(textureName);

        if (checkCustomModel && input.readBoolean()) {
            this.customModel = input.readByteArray();
        } else {
            this.customModel = null;
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

    protected void saveItemFlags6(BitOutput output) {
        for (boolean itemFlag : itemFlags) {
            output.addBooleans(itemFlag);
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

    protected void loadPotionProperties9(BitInput input) {
        loadOnHitPlayerEffects9(input);
        loadOnHitTargetEffects9(input);
    }

    protected void savePotionProperties9(BitOutput output) {
        saveOnHitPlayerEffects9(output);
        saveOnHitTargetEffects9(output);
    }

    protected void loadOnHitPlayerEffects9(BitInput input) {
        this.playerEffects = loadPotionEffectList(input);
    }

    protected void saveOnHitPlayerEffects9(BitOutput output) {
        savePotionEffectList(playerEffects, output);
    }

    protected void loadOnHitTargetEffects9(BitInput input) {
        this.targetEffects = loadPotionEffectList(input);
    }

    protected void saveOnHitTargetEffects9(BitOutput output) {
        savePotionEffectList(targetEffects, output);
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

    protected void savePotionProperties10(BitOutput output) {
        savePotionProperties9(output);
        saveEquippedPotionEffects10(output);
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

    protected void saveRightClickProperties9(BitOutput output) {
        saveCommands9(output);
    }

    protected void loadCommands9(BitInput input) {
        int numCommands = input.readByte() & 0xFF;
        this.commands = new ArrayList<>(numCommands);
        for (int counter = 0; counter < numCommands; counter++) {
            this.commands.add(input.readJavaString());
        }
    }

    protected void saveCommands9(BitOutput output) {
        output.addByte((byte) commands.size());
        for (String command : commands) {
            output.addJavaString(command);
        }
    }

    protected void loadRightClickProperties10(BitInput input, SItemSet itemSet) {
        loadRightClickProperties9(input);
        loadReplacementConditions10(input, itemSet);
    }

    protected void saveRightClickProperties10(BitOutput output) {
        saveRightClickProperties9(output);
        saveReplacementConditions10(output);
    }

    protected void loadReplacementConditions10(BitInput input, SItemSet itemSet) {
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

    protected void loadBase10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
    }

    protected void saveBase10(BitOutput output) {
        saveIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveItemFlags6(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveExtraProperties10(output);
    }

    protected void initBaseDefaults10() {
        // Nothing to be done until the next encoding is known
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

        this.commands = new ArrayList<>(0);
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

    public Collection<PotionEffectValues> getOnHitPlayerEffects() {
        return new ArrayList<>(playerEffects);
    }

    public Collection<PotionEffectValues> getOnHitTargetEffects() {
        return new ArrayList<>(targetEffects);
    }

    public Collection<EquippedPotionEffectValues> getEquippedEffects() {
        return new ArrayList<>(equippedEffects);
    }

    public List<String> getCommands() {
        return new ArrayList<>(commands);
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

    public BaseTextureValues getTexture() {
        return texture.get();
    }

    public TextureReference getTextureReference() {
        return texture;
    }

    public byte[] getCustomModel() {
        return CollectionHelper.arrayCopy(customModel);
    }

    public void setItemType(CustomItemType newItemType) {
        assertMutable();
        Checks.nonNull(newItemType);
        this.itemType = newItemType;
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

    public void setDisplayName(String newDisplayName) {
        assertMutable();
        Checks.notNull(newDisplayName);
        this.displayName = newDisplayName;
    }

    public void setLore(List<String> newLore) {
        assertMutable();
        Checks.nonNull(newLore);
        this.lore = new ArrayList<>(newLore);
    }

    public void setItemFlags(List<Boolean> newItemFlags) {
        assertMutable();
        Checks.nonNull(newItemFlags);
        this.itemFlags = new ArrayList<>(newItemFlags);
    }

    public void setAttributeModifiers(List<AttributeModifierValues> newAttributeModifiers) {
        assertMutable();
        Checks.nonNull(newAttributeModifiers);
        this.attributeModifiers = Mutability.createDeepCopy(newAttributeModifiers, false);
    }

    public void setDefaultEnchantments(List<EnchantmentValues> newDefaultEnchantments) {
        assertMutable();
        Checks.nonNull(newDefaultEnchantments);
        this.defaultEnchantments = Mutability.createDeepCopy(newDefaultEnchantments, false);
    }

    public void setPlayerEffects(List<PotionEffectValues> newPlayerEffects) {
        assertMutable();
        Checks.nonNull(newPlayerEffects);
        this.playerEffects = Mutability.createDeepCopy(newPlayerEffects, false);
    }

    public void setTargetEffects(List<PotionEffectValues> newTargetEffects) {
        assertMutable();
        Checks.nonNull(newTargetEffects);
        this.targetEffects = Mutability.createDeepCopy(newTargetEffects, false);
    }

    public void setEquippedEffects(List<EquippedPotionEffectValues> newEquippedEffects) {
        assertMutable();
        Checks.nonNull(newEquippedEffects);
        this.equippedEffects = Mutability.createDeepCopy(equippedEffects, false);
    }

    public void setCommands(List<String> newCommands) {
        assertMutable();
        Checks.nonNull(newCommands);
        this.commands = new ArrayList<>(newCommands);
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

    public void setTexture(TextureReference newTexture) {
        assertMutable();
        Checks.notNull(newTexture);
        this.texture = newTexture;
    }

    public void setCustomModel(byte[] newModel) {
        assertMutable();
        this.customModel = CollectionHelper.arrayCopy(newModel);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (itemDamage < 0) throw new ValidationException("Internal item damage is negative");

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
        for (PotionEffectValues effect : playerEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit player effect");
            Validation.scope("On-hit player effect", effect::validate);
        }

        if (targetEffects == null) throw new ProgrammingValidationException("No on-hit target effects");
        if (targetEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit target effects");
        for (PotionEffectValues effect : targetEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit target effect");
            Validation.scope("On-hit target effect", effect::validate);
        }

        if (equippedEffects == null) throw new ProgrammingValidationException("No equipped effects");
        for (EquippedPotionEffectValues effect : equippedEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an equipped effect");
            Validation.scope("Equipped effect", effect::validate);
        }

        if (commands == null) throw new ProgrammingValidationException("No commands");
        if (commands.size() > Byte.MAX_VALUE) throw new ValidationException("Too many commands");
        for (String command : commands) {
            if (command == null) throw new ProgrammingValidationException("Missing a command");
        }

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

        if (texture == null) throw new ProgrammingValidationException("No texture");
        // customModel doesn't have any invalid values
    }

    public void validateComplete(
            SItemSet itemSet, String oldName
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

    // TODO Add a unit test that checks if item set remains equal after saving and loading
}
