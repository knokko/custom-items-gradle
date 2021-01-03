package nl.knokko.customitems.plugin.set.item;

import java.util.Collection;
import java.util.List;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomTrident extends CustomTool {
	
	public final int throwDurabilityLoss;
	
	public final double throwDamageMultiplier;
	public final double throwSpeedMultiplier;

	public CustomTrident(
			short itemDamage, String name, String alias, String displayName, 
			String[] lore, AttributeModifier[] attributes, 
			Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, double throwDamageMultiplier, 
			double throwSpeedMultiplier, Ingredient repairItem, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			int throwDurabilityLoss,  List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> equippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op,
			ExtraItemNbt extraNbt, float attackRange
	) {
		super(
				CustomItemType.TRIDENT, itemDamage, name, alias, displayName, lore, 
				attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
		this.throwDurabilityLoss = throwDurabilityLoss;
		this.throwSpeedMultiplier = throwSpeedMultiplier;
		this.throwDamageMultiplier = throwDamageMultiplier;
	}

}
