package nl.knokko.customitems.item;

import nl.knokko.customitems.effect.EffectType;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PassivePotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
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
    protected Collection<AttributeModifier> attributeModifiers;
    protected Collection<Enchantment> defaultEnchantments;

    // Potion properties
    protected Collection<PotionEffect> playerEffects;
    protected Collection<PotionEffect> targetEffects;
    protected Collection<EquippedPotionEffect> equippedEffects;

    // Right-click properties
    protected List<String> commands;
    protected ReplaceCondition.ConditionOperation conditionOp;
    protected List<ReplaceCondition> replaceConditions;

    // Other properties
    protected ExtraItemNbt extraItemNbt;
    protected float attackRange;

    // Editor-only properties
    protected NamedImage texture;
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
        this.texture = source.getTexture();
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
            String attributeName = input.readJavaString();
            String slotName = input.readJavaString();
            int operationOrdinal = (int) input.readNumber((byte) 2, false);
            double attributeValue = input.readDouble();
            this.attributeModifiers.add(new AttributeModifier(
                    AttributeModifier.Attribute.valueOf(attributeName),
                    AttributeModifier.Slot.valueOf(slotName),
                    AttributeModifier.Operation.values()[operationOrdinal],
                    attributeValue
            ));
        }
    }

    protected void loadDefaultEnchantments4(BitInput input) {
        int numDefaultEnchantments = input.readByte() & 0xFF;
        this.defaultEnchantments = new ArrayList<>(numDefaultEnchantments);
        for (int counter = 0; counter < numDefaultEnchantments; counter++) {
            String enchantmentName = input.readString();
            int enchantmentLevel = input.readInt();
            this.defaultEnchantments.add(new Enchantment(EnchantmentType.valueOf(enchantmentName), enchantmentLevel));
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

    protected Collection<PotionEffect> loadPotionEffectList(BitInput input) {
        int numEffects = input.readByte() & 0xFF;
        Collection<PotionEffect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            String effectName = input.readJavaString();
            int effectDuration = input.readInt();
            int effectLevel = input.readInt();
            effects.add(new PotionEffect(EffectType.valueOf(effectName), effectDuration, effectLevel));
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

    protected void loadRightClickProperties10(BitInput input) {
        loadRightClickProperties9(input);
        loadReplacementConditions10(input);
    }

    protected void loadReplacementConditions10(BitInput input) {
        int numReplacementConditions = input.readByte() & 0xFF;
        this.replaceConditions = new ArrayList<>(numReplacementConditions);
        for (int counter = 0; counter < numReplacementConditions; counter++) {
            String conditionName = input.readJavaString();
            String itemName = input.readJavaString();
            String operationName = input.readJavaString();
            int conditionValue = input.readInt();
            String replacementItemName = input.readJavaString();
            this.replaceConditions.add(new ReplaceCondition(
                    ReplaceCondition.ReplacementCondition.valueOf(conditionName),
                    itemName,
                    ReplaceCondition.ReplacementOperation.valueOf(operationName),
                    conditionValue, replacementItemName
            ));
        }
        this.conditionOp = ReplaceCondition.ConditionOperation.valueOf(input.readJavaString());
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

    public Collection<AttributeModifier> getAttributeModifiers() {
        return new ArrayList<>(attributeModifiers);
    }

    public Collection<Enchantment> getDefaultEnchantments() {
        return new ArrayList<>(defaultEnchantments);
    }

    public Collection<PotionEffect> getOnHitPlayerEffects() {
        return new ArrayList<>(playerEffects);
    }

    public Collection<PotionEffect> getOnHitTargetEffects() {
        return new ArrayList<>(targetEffects);
    }

    public Collection<EquippedPotionEffect> getEquippedEffects() {
        return new ArrayList<>(equippedEffects);
    }

    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }

    public List<ReplaceCondition> getReplacementConditions() {
        return new ArrayList<>(replaceConditions);
    }

    public ReplaceCondition.ConditionOperation getConditionOp() {
        return conditionOp;
    }

    public ExtraItemNbt getExtraNbt() {
        return extraItemNbt;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public NamedImage getTexture() {
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
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (itemDamage < 0) throw new ValidationException("Internal item damage is negative");
    }

    public void validateComplete() throws ValidationException, ProgrammingValidationException {
        validateIndependent();
    }
}
