package nl.knokko.customitems.editor.set.item;

import java.util.Collection;
import java.util.List;

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.texture.NamedImage;

public class CustomHelmet3D extends CustomArmor {

	public CustomHelmet3D(
            CustomItemType itemType, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes,
            Enchantment[] defaultEnchantments, long durability,
            boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem,
            NamedImage texture, boolean[] itemFlags, int entityHitDurabilityLoss,
            int blockBreakDurabilityLoss, DamageResistances damageResistances,
            byte[] customModel, List<PotionEffect> playerEffects,
            List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands,
            ReplaceCondition[] conditions, ConditionOperation op,
            ExtraItemNbt extraNbt, float attackRange
	) {
		// The null is because 3d custom helmets don't need a worn texture
		super(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, 0, 0, 0, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, damageResistances, customModel, 
				playerEffects, targetEffects, equippedEffects, commands, 
				conditions, op, extraNbt, null, attackRange
		);
	}
	
	@Override
	protected byte getEncoding9() {
		return ItemEncoding.ENCODING_HELMET3D_9;
	}
}
