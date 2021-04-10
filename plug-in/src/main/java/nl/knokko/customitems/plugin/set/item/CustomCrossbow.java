package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

import java.util.Collection;
import java.util.List;

public class CustomCrossbow extends CustomTool {

    private final int arrowDurabilityLoss;
    private final int fireworkDurabilityLoss;

    private final float arrowDamageMultiplier;
    private final float fireworkDamageMultiplier;

    private final float arrowSpeedMultiplier;
    private final float fireworkSpeedMultiplier;

    private final int arrowKnockbackStrength;
    private final boolean arrowGravity;

    public CustomCrossbow(
            short itemDamage, String name, String alias, String displayName, String[] lore,
            AttributeModifier[] attributes, Enchantment[] defaultEnchantments, long maxDurability,
            boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, boolean[] itemFlags,
            int entityHitDurabilityLoss, int blockBreakDurabilityLoss, List<PotionEffect> playerEffects,
            List<PotionEffect> targetEffects, Collection<EquippedPotionEffect> equippedEffects,
            String[] commands, ReplaceCondition[] conditions, ReplaceCondition.ConditionOperation op,
            ExtraItemNbt extraNbt, float attackRange, int arrowDurabilityLoss, int fireworkDurabilityLoss,
            float arrowDamageMultiplier, float fireworkDamageMultiplier, float arrowSpeedMultiplier,
            float fireworkSpeedMultiplier, int arrowKnockbackStrength, boolean arrowGravity
    ) {
        super(
                CustomItemType.CROSSBOW, itemDamage, name, alias, displayName, lore, attributes,
                defaultEnchantments, maxDurability, allowEnchanting, allowAnvil, repairItem, itemFlags,
                entityHitDurabilityLoss, blockBreakDurabilityLoss, playerEffects, targetEffects,
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

    public int getFireworkDurabilityLoss() {
        return fireworkDurabilityLoss;
    }

    public float getArrowDamageMultiplier() {
        return arrowDamageMultiplier;
    }

    public float getFireworkDamageMultiplier() {
        return fireworkDamageMultiplier;
    }

    public float getArrowSpeedMultiplier() {
        return arrowSpeedMultiplier;
    }

    public float getFireworkSpeedMultiplier() {
        return fireworkSpeedMultiplier;
    }

    public int getArrowKnockbackStrength() {
        return arrowKnockbackStrength;
    }

    public boolean hasArrowGravity() {
        return arrowGravity;
    }
}
