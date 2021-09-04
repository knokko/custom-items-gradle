package nl.knokko.customitems.item;

import nl.knokko.customitems.effect.*;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.EagerSupplier;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

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
    protected Collection<EquippedPotionEffect> equippedEffects;

    // Right-click properties
    protected List<String> commands;
    protected SReplaceCondition.ConditionOperation conditionOp;
    protected List<SReplaceCondition> replaceConditions;

    // Other properties
    protected ExtraItemNbt extraItemNbt;
    protected float attackRange;

    // Editor-only properties
    protected TextureReference texture;
    protected byte[] customModel;

    public CustomItemValues(boolean mutable) {
        super(mutable);
    }

    public CustomItemValues(CustomItemValues toCopy, boolean mutable) {
        super(mutable);

        copyProperties(toCopy);
    }

    protected void copyProperties(CustomItemValues source) {
        this.itemType = source.getItemType();
        this.itemDamage = source.getItemDamage();
        this.name = source.getName();
        this.alias = source.getAlias();
        this.displayName = source.getDisplayName();
        this.lore = source.getLore();
        this.attributeModifiers = source.getAttributeModifiers();
        this.defaultEnchantments = source.getDefaultEnchantments();
        this.itemFlags = source.getItemFlags();
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

    protected void loadEditorOnlyProperties1(BitInput input, boolean checkCustomModel) {
        String textureName = input.readJavaString();
        // TODO Retrieve texture
        if (checkCustomModel && input.readBoolean()) {
            this.customModel = input.readByteArray();
        } else {
            this.customModel = null;
        }
    }

    protected void loadIdentityProperties1(BitInput input) {
        this.itemType = CustomItemType.valueOf(input.readJavaString());
        this.itemDamage = input.readShort();
        this.name = input.readJavaString();
    }

    protected void loadIdentityProperties9(BitInput input) {
        loadIdentityProperties1(input);
        this.alias = input.readString();
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

    protected void loadVanillaBasedPowers4(BitInput input) {
        loadVanillaBasedPowers2(input);
        loadDefaultEnchantments4(input);
    }

    protected void loadAttributeModifiers2(BitInput input) {
        int numAttributeModifiers = input.readByte() & 0xFF;
        this.attributeModifiers = new ArrayList<>(numAttributeModifiers);
        for (int counter = 0; counter < numAttributeModifiers; counter++) {
            this.attributeModifiers.add(CIAttributeModifier.load1(input, false));
        }
    }

    protected void loadDefaultEnchantments4(BitInput input) {
        int numDefaultEnchantments = input.readByte() & 0xFF;
        this.defaultEnchantments = new ArrayList<>(numDefaultEnchantments);
        for (int counter = 0; counter < numDefaultEnchantments; counter++) {
            this.defaultEnchantments.add(CIEnchantment.load1(input, false));
        }
    }

    protected void loadPotionProperties9(BitInput input) {
        loadOnHitPlayerEffects9(input);
        loadOnHitTargetEffects9(input);
    }

    protected void loadOnHitPlayerEffects9(BitInput input) {
        this.playerEffects = loadPotionEffectList(input);
    }

    protected void loadOnHitTargetEffects9(BitInput input) {
        this.targetEffects = loadPotionEffectList(input);
    }

    protected Collection<CIPotionEffect> loadPotionEffectList(BitInput input) {
        int numEffects = input.readByte() & 0xFF;
        Collection<CIPotionEffect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(CIPotionEffect.load1(input, false));
        }
        return effects;
    }

    protected void loadPotionProperties10(BitInput input) {
        loadPotionProperties9(input);
        loadEquippedPotionEffects10(input);
    }

    protected void loadEquippedPotionEffects10(BitInput input) {
        int numEquippedEffects = input.readInt();
        this.equippedEffects = new ArrayList<>(numEquippedEffects);
        for (int counter = 0; counter < numEquippedEffects; counter++) {
            String effectName = input.readString();
            int effectLevel = input.readInt();
            String slotName = input.readString();
            this.equippedEffects.add(new EquippedPotionEffect(
                    new PassivePotionEffect(EffectType.valueOf(effectName), effectLevel),
                    AttributeModifier.Slot.valueOf(slotName)
            ));
        }
    }

    protected void loadRightClickProperties9(BitInput input) {
        loadCommands9(input);
    }

    protected void loadCommands9(BitInput input) {
        int numCommands = input.readByte() & 0xFF;
        this.commands = new ArrayList<>(numCommands);
        for (int counter = 0; counter < numCommands; counter++) {
            this.commands.add(input.readJavaString());
        }
    }

    protected void loadRightClickProperties10(BitInput input, SItemSet itemSet) {
        loadRightClickProperties9(input);
        loadReplacementConditions10(input, itemSet);
    }

    protected void loadReplacementConditions10(BitInput input, SItemSet itemSet) {
        int numReplacementConditions = input.readByte() & 0xFF;
        this.replaceConditions = new ArrayList<>(numReplacementConditions);
        for (int counter = 0; counter < numReplacementConditions; counter++) {
            this.replaceConditions.add(SReplaceCondition.load1(input, itemSet, false));
        }
        this.conditionOp = SReplaceCondition.ConditionOperation.valueOf(input.readJavaString());
    }

    protected void loadExtraProperties10(BitInput input) throws UnknownEncodingException {
        this.extraItemNbt = ExtraItemNbt.load(input);
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

    protected void save1(BitOutput output) {
        output.addJavaString(itemType.name());
        output.addShort(itemDamage);
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

    public Collection<EquippedPotionEffect> getEquippedEffects() {
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

    public ExtraItemNbt getExtraNbt() {
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
        this.itemType = newItemType;
    }

    public void setItemDamage(short newItemDamage) {
        assertMutable();
        this.itemDamage = newItemDamage;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        // TODO Finish this
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (itemDamage < 0) throw new ValidationException("Internal item damage is negative");
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name is empty");
        // TODO Name filter
        if (alias == null) throw new ProgrammingValidationException("No alias");

        if (displayName == null) throw new ProgrammingValidationException("No display name");
        if (lore == null) throw new ProgrammingValidationException("No lore");
        for (String loreLine : lore) {
            if (loreLine == null) throw new ProgrammingValidationException("Missing a lore line");
        }

        if (itemFlags == null) throw new ProgrammingValidationException("No item flags");
        if (itemFlags.size() != 6) throw new ProgrammingValidationException("Number of item flags is not 6");

        if (attributeModifiers == null) throw new ProgrammingValidationException("No attribute modifiers");
        for (CIAttributeModifier attributeModifier : attributeModifiers) {
            if (attributeModifier == null) throw new ProgrammingValidationException("Missing an attribute modifier");
            attributeModifier.validate();
        }

        if (defaultEnchantments == null) throw new ProgrammingValidationException("No default enchantments");
        for (CIEnchantment enchantment : defaultEnchantments) {
            if (enchantment == null) throw new ProgrammingValidationException("Missing a default enchantment");
            enchantment.validate();
        }

        // equipped effects, commands
        if (playerEffects == null) throw new ProgrammingValidationException("No on-hit player effects");
        for (CIPotionEffect effect : playerEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit player effect");
            effect.validate();
        }

        if (targetEffects == null) throw new ProgrammingValidationException("No on-hit target effects");
        for (CIPotionEffect effect : targetEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an on-hit target effect");
            effect.validate();
        }

        // TODO Replacement conditions: check editor/ItemSet.addItem as well as ReplacementCollectionedit!

        // TODO Check that some collection sizes are not larger than Byte.MAX_VALUE

        // TODO Scope validation errors (to make them easier to understand for users)
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

        // TODO Check deleted custom items

        if (!itemSet.isReferenceValid(texture)) {
            throw new ProgrammingValidationException("The chosen texture is not (or no longer) valid");
        }

        for (SReplaceCondition condition : replaceConditions) {
            if (!itemSet.isReferenceValid(condition.getItemReference())) {
                throw new ProgrammingValidationException("The item of a replace condition is not (or no longer) valid");
            }
            if (!itemSet.isReferenceValid(condition.getReplaceItemReference())) {
                throw new ProgrammingValidationException("The replace item of a replacement condition is not (or no longer) valid");
            }
        }
    }
}
