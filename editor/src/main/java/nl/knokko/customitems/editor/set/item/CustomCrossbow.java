package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.editor.set.item.texture.CrossbowTextures;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.util.bits.BitOutput;

import java.util.Collection;
import java.util.List;

public class CustomCrossbow extends CustomTool {

    private int arrowDurabilityLoss;
    private int fireworkDurabilityLoss;

    private float arrowDamageMultiplier;
    private float fireworkDamageMultiplier;

    private float arrowSpeedMultiplier;
    private float fireworkSpeedMultiplier;

    private int arrowKnockbackStrength;
    private boolean arrowGravity;

    public CustomCrossbow(
            String name, String alias, String displayName, String[] lore,
            AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long durability,
            boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, CrossbowTextures texture,
            boolean[] itemFlags, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
            byte[] customModel, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands,
            ReplaceCondition[] conditions, ReplaceCondition.ConditionOperation op, ExtraItemNbt extraNbt,
            float attackRange, int arrowDurabilityLoss, int fireworkDurabilityLoss,
            float arrowDamageMultiplier, float fireworkDamageMultiplier,
            float arrowSpeedMultiplier, float fireworkSpeedMultiplier,
            int arrowKnockbackStrength, boolean arrowGravity
    ) {
        super(
                CustomItemType.CROSSBOW, name, alias, displayName, lore, attributes, defaultEnchantments,
                durability, allowEnchanting, allowAnvil, repairItem, texture, itemFlags,
                entityHitDurabilityLoss, blockBreakDurabilityLoss, customModel, playerEffects, targetEffects,
                equippedEffects, commands, conditions, op, extraNbt, attackRange
        );

        this.arrowDurabilityLoss = arrowDurabilityLoss;
        this.fireworkDurabilityLoss = fireworkDurabilityLoss;
        this.arrowDamageMultiplier = arrowDamageMultiplier;
        this.fireworkDamageMultiplier = fireworkDamageMultiplier;
        this.arrowSpeedMultiplier = arrowSpeedMultiplier;
        this.fireworkSpeedMultiplier = fireworkSpeedMultiplier;
        this.arrowKnockbackStrength = arrowKnockbackStrength;
        this.arrowGravity = arrowGravity;
    }


    public int getArrowDurabilityLoss() {
        return arrowDurabilityLoss;
    }

    public void setArrowDurabilityLoss(int arrowDurabilityLoss) {
        this.arrowDurabilityLoss = arrowDurabilityLoss;
    }

    public int getFireworkDurabilityLoss() {
        return fireworkDurabilityLoss;
    }

    public void setFireworkDurabilityLoss(int fireworkDurabilityLoss) {
        this.fireworkDurabilityLoss = fireworkDurabilityLoss;
    }

    public float getArrowDamageMultiplier() {
        return arrowDamageMultiplier;
    }

    public void setArrowDamageMultiplier(float arrowDamageMultiplier) {
        this.arrowDamageMultiplier = arrowDamageMultiplier;
    }

    public float getFireworkDamageMultiplier() {
        return fireworkDamageMultiplier;
    }

    public void setFireworkDamageMultiplier(float fireworkDamageMultiplier) {
        this.fireworkDamageMultiplier = fireworkDamageMultiplier;
    }

    public float getArrowSpeedMultiplier() {
        return arrowSpeedMultiplier;
    }

    public void setArrowSpeedMultiplier(float arrowSpeedMultiplier) {
        this.arrowSpeedMultiplier = arrowSpeedMultiplier;
    }

    public float getFireworkSpeedMultiplier() {
        return fireworkSpeedMultiplier;
    }

    public void setFireworkSpeedMultiplier(float fireworkSpeedMultiplier) {
        this.fireworkSpeedMultiplier = fireworkSpeedMultiplier;
    }

    public int getArrowKnockbackStrength() {
        return arrowKnockbackStrength;
    }

    public void setArrowKnockbackStrength(int arrowKnockbackStrength) {
        this.arrowKnockbackStrength = arrowKnockbackStrength;
    }

    public boolean hasArrowGravity() {
        return arrowGravity;
    }

    public void setArrowGravity(boolean arrowGravity) {
        this.arrowGravity = arrowGravity;
    }

    @Override
    public CrossbowTextures getTexture() {
        return (CrossbowTextures) super.getTexture();
    }

    @Override
    public void export(BitOutput output) {
        output.addByte(ItemEncoding.ENCODING_CROSSBOW_10);
        output.addShort(itemDamage);
        output.addJavaString(name);
        output.addString(alias);
        output.addJavaString(displayName);
        output.addByte((byte) lore.length);
        for(String line : lore)
            output.addJavaString(line);
        output.addByte((byte) attributes.length);
        for (AttributeModifier attribute : attributes) {
            output.addJavaString(attribute.getAttribute().name());
            output.addJavaString(attribute.getSlot().name());
            output.addNumber(attribute.getOperation().ordinal(), (byte) 2, false);
            output.addDouble(attribute.getValue());
        }
        output.addByte((byte) defaultEnchantments.length);
        for (Enchantment enchantment : defaultEnchantments) {
            output.addString(enchantment.getType().name());
            output.addInt(enchantment.getLevel());
        }
        output.addLong(durability);
        output.addBoolean(allowEnchanting);
        output.addBoolean(allowAnvil);
        repairItem.save(output);
        output.addBooleans(itemFlags);
        output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
        output.addByte((byte) playerEffects.size());
        for (PotionEffect effect : playerEffects) {
            output.addJavaString(effect.getEffect().name());
            output.addInt(effect.getDuration());
            output.addInt(effect.getLevel());
        }
        output.addByte((byte) targetEffects.size());
        for (PotionEffect effect : targetEffects) {
            output.addJavaString(effect.getEffect().name());
            output.addInt(effect.getDuration());
            output.addInt(effect.getLevel());
        }
        writeEquippedEffects(output);
        output.addByte((byte) commands.length);
        for (String command : commands) {
            output.addJavaString(command);
        }
        output.addByte((byte) conditions.length);
        for (ReplaceCondition condition : conditions) {
            output.addJavaString(condition.getCondition().name());
            output.addJavaString(condition.getItemName());
            output.addJavaString(condition.getOp().name());
            output.addInt(condition.getValue());
            output.addJavaString(condition.getReplacingItemName());
        }
        output.addJavaString(op.name());
        extraNbt.save(output);
        output.addFloat(attackRange);

        output.addInt(arrowDurabilityLoss);
        output.addInt(fireworkDurabilityLoss);

        output.addFloat(arrowDamageMultiplier);
        output.addFloat(fireworkDamageMultiplier);

        output.addFloat(arrowSpeedMultiplier);
        output.addFloat(fireworkSpeedMultiplier);

        output.addInt(arrowKnockbackStrength);
        output.addBoolean(arrowGravity);
    }
}
