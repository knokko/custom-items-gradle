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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CustomItemValues extends ModelValues {

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
    protected Collection<CIAttributeModifier> attributeModifiers;
    protected Collection<CIEnchantment> defaultEnchantments;

    // Potion properties
    protected Collection<CIPotionEffect> playerEffects;
    protected Collection<CIPotionEffect> targetEffects;
    protected Collection<SEquippedPotionEffect> equippedEffects;

    // Right-click properties
    protected List<String> commands;
    protected SReplaceCondition.ConditionOperation conditionOp;
    protected List<SReplaceCondition> replaceConditions;

    // Other properties
    protected SExtraItemNbt extraItemNbt;
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
        this.conditionOp = SReplaceCondition.ConditionOperation.NONE;
        this.replaceConditions = new ArrayList<>(0);

        this.extraItemNbt = new SExtraItemNbt(false);
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
            this.attributeModifiers.add(CIAttributeModifier.load1(input, false));
        }
    }

    protected void saveAttributeModifiers2(BitOutput output) {
        output.addByte((byte) attributeModifiers.size());
        for (CIAttributeModifier attributeModifier : attributeModifiers) {
            attributeModifier.save1(output);
        }
    }

    protected void loadDefaultEnchantments4(BitInput input) {
        int numDefaultEnchantments = input.readByte() & 0xFF;
        this.defaultEnchantments = new ArrayList<>(numDefaultEnchantments);
        for (int counter = 0; counter < numDefaultEnchantments; counter++) {
            this.defaultEnchantments.add(CIEnchantment.load1(input, false));
        }
    }

    protected void saveDefaultEnchantments4(BitOutput output) {
        output.addByte((byte) defaultEnchantments.size());
        for (CIEnchantment defaultEnchantment : defaultEnchantments) {
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

    protected Collection<CIPotionEffect> loadPotionEffectList(BitInput input) {
        int numEffects = input.readByte() & 0xFF;
        Collection<CIPotionEffect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(CIPotionEffect.load1(input, false));
        }
        return effects;
    }

    protected void savePotionEffectList(Collection<CIPotionEffect> effects, BitOutput output) {
        output.addByte((byte) effects.size());
        for (CIPotionEffect effect : effects) {
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
            this.equippedEffects.add(SEquippedPotionEffect.load1(input, false));
        }
    }

    protected void saveEquippedPotionEffects10(BitOutput output) {
        output.addInt(equippedEffects.size());
        for (SEquippedPotionEffect effect : equippedEffects) {
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
            this.replaceConditions.add(SReplaceCondition.load1(input, itemSet, false));
        }
        this.conditionOp = SReplaceCondition.ConditionOperation.valueOf(input.readJavaString());
    }

    protected void saveReplacementConditions10(BitOutput output) {
        output.addByte((byte) replaceConditions.size());
        for (SReplaceCondition replaceCondition : replaceConditions) {
            replaceCondition.save1(output);
        }
        output.addJavaString(conditionOp.name());
    }

    protected void loadExtraProperties10(BitInput input) throws UnknownEncodingException {
        this.extraItemNbt = SExtraItemNbt.load(input, false);
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

    public Collection<CIAttributeModifier> getAttributeModifiers() {
        return new ArrayList<>(attributeModifiers);
    }

    public Collection<CIEnchantment> getDefaultEnchantments() {
        return new ArrayList<>(defaultEnchantments);
    }

    public Collection<CIPotionEffect> getOnHitPlayerEffects() {
        return new ArrayList<>(playerEffects);
    }

    public Collection<CIPotionEffect> getOnHitTargetEffects() {
        return new ArrayList<>(targetEffects);
    }

    public Collection<SEquippedPotionEffect> getEquippedEffects() {
        return new ArrayList<>(equippedEffects);
    }

    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }

    public List<SReplaceCondition> getReplacementConditions() {
        return new ArrayList<>(replaceConditions);
    }

    public SReplaceCondition.ConditionOperation getConditionOp() {
        return conditionOp;
    }

    public SExtraItemNbt getExtraNbt() {
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
        return Arrays.copyOf(customModel, customModel.length);
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

    public void setAttributeModifiers(List<CIAttributeModifier> newAttributeModifiers) {
        assertMutable();
        Checks.nonNull(newAttributeModifiers);
        this.attributeModifiers = Mutability.createDeepCopy(newAttributeModifiers, false);
    }

    public void setDefaultEnchantments(List<CIEnchantment> newDefaultEnchantments) {
        assertMutable();
        Checks.nonNull(newDefaultEnchantments);
        this.defaultEnchantments = Mutability.createDeepCopy(newDefaultEnchantments, false);
    }

    public void setPlayerEffects(List<CIPotionEffect> newPlayerEffects) {
        assertMutable();
        Checks.nonNull(newPlayerEffects);
        this.playerEffects = Mutability.createDeepCopy(newPlayerEffects, false);
    }

    public void setTargetEffects(List<CIPotionEffect> newTargetEffects) {
        assertMutable();
        Checks.nonNull(newTargetEffects);
        this.targetEffects = Mutability.createDeepCopy(newTargetEffects, false);
    }

    public void setEquippedEffects(List<SEquippedPotionEffect> newEquippedEffects) {
        assertMutable();
        Checks.nonNull(newEquippedEffects);
        this.equippedEffects = Mutability.createDeepCopy(equippedEffects, false);
    }

    public void setCommands(List<String> newCommands) {
        assertMutable();
        Checks.nonNull(newCommands);
        this.commands = new ArrayList<>(newCommands);
    }

    public void setConditionOp(SReplaceCondition.ConditionOperation newConditionOp) {
        assertMutable();
        Checks.notNull(newConditionOp);
        this.conditionOp = newConditionOp;
    }

    public void setReplaceConditions(List<SReplaceCondition> newReplaceConditions) {
        assertMutable();
        Checks.nonNull(newReplaceConditions);
        this.replaceConditions = Mutability.createDeepCopy(newReplaceConditions, false);
    }

    public void setExtraItemNbt(SExtraItemNbt newExtraNbt) {
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
        this.customModel = newModel;
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
        for (CIAttributeModifier attributeModifier : attributeModifiers) {
            if (attributeModifier == null) throw new ProgrammingValidationException("Missing an attribute modifier");
            Validation.scope("Attribute modifier", attributeModifier::validate);
        }

        if (defaultEnchantments == null) throw new ProgrammingValidationException("No default enchantments");
        if (defaultEnchantments.size() > Byte.MAX_VALUE) throw new ValidationException("Too many default enchantments");
        for (CIEnchantment enchantment : defaultEnchantments) {
            if (enchantment == null) throw new ProgrammingValidationException("Missing a default enchantment");
            Validation.scope("Default enchantment", enchantment::validate);
        }

        if (playerEffects == null) throw new ProgrammingValidationException("No on-hit player effects");
        if (playerEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit player effects");
        for (CIPotionEffect effect : playerEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit player effect");
            Validation.scope("On-hit player effect", effect::validate);
        }

        if (targetEffects == null) throw new ProgrammingValidationException("No on-hit target effects");
        if (targetEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many on-hit target effects");
        for (CIPotionEffect effect : targetEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit target effect");
            Validation.scope("On-hit target effect", effect::validate);
        }

        if (equippedEffects == null) throw new ProgrammingValidationException("No equipped effects");
        for (SEquippedPotionEffect effect : equippedEffects) {
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
        for (SReplaceCondition condition : replaceConditions) {
            if (condition == null) throw new ProgrammingValidationException("Missing a replacement condition");
            Validation.scope("Replace condition", condition::validateIndependent);
        }
        if (conditionOp == SReplaceCondition.ConditionOperation.NONE && replaceConditions.size() > 1) {
            throw new ValidationException("There are multiple replace conditions but no operator has been specified");
        }
        if (conditionOp == SReplaceCondition.ConditionOperation.AND || conditionOp == SReplaceCondition.ConditionOperation.OR) {
            for (SReplaceCondition conditionA : replaceConditions) {
                for (SReplaceCondition conditionB : replaceConditions) {
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

        for (SReplaceCondition condition : replaceConditions) {
            Validation.scope("Replace condition", () -> condition.validateComplete(itemSet));
        }
    }

    // TODO Add a unit test that checks if item set remains equal after saving and loading
}
