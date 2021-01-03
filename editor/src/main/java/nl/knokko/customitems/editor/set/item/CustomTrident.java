package nl.knokko.customitems.editor.set.item;

import java.util.Collection;
import java.util.List;

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
import nl.knokko.util.bits.BitOutput;

public class CustomTrident extends CustomTool {
	
	public int throwDurabilityLoss;
	
	public double throwDamageMultiplier;
	public double speedMultiplier;
	
	public byte[] customInHandModel;
	public byte[] customThrowingModel;

	public CustomTrident(
			String name, String alias, String displayName, String[] lore,
			AttributeModifier[] attributes, Enchantment[] defaultEnchantments, 
			long durability, boolean allowEnchanting, boolean allowAnvil, 
			double throwDamageMultiplier, double speedMultiplier, 
			Ingredient repairItem, NamedImage texture, boolean[] itemFlags, 
			int entityHitDurabilityLoss, int blockBreakDurabilityLoss, 
			int throwDurabilityLoss, byte[] customModel, byte[] customInHandModel, 
			byte[] customThrowingModel, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> equippedEffects,
			String[] commands, ReplaceCondition[] conditions, ConditionOperation op,
			ExtraItemNbt extraNbt, float attackRange
	) {
		super(
				CustomItemType.TRIDENT, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, customModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
		this.throwDamageMultiplier = throwDamageMultiplier;
		this.speedMultiplier = speedMultiplier;
		this.throwDurabilityLoss = throwDurabilityLoss;
		this.customInHandModel = customInHandModel;
		this.customThrowingModel = customThrowingModel;
	}
	
	@Override
	public void export(BitOutput output) {
		/* Previous Encoding
		output.addByte(ItemEncoding.ENCODING_TRIDENT_7);
		output.addShort(itemDamage);
		output.addJavaString(name);
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
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss);
		output.addDoubles(throwDamageMultiplier, speedMultiplier);*/
		
		/* Previous Encoding
		output.addByte(ItemEncoding.ENCODING_TRIDENT_8);
		output.addShort(itemDamage);
		output.addJavaString(name);
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
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss);
		output.addDoubles(throwDamageMultiplier, speedMultiplier);
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
		output.addByte((byte) commands.length);
		for (String command : commands) {
			output.addJavaString(command);
		} */
		
		output.addByte(ItemEncoding.ENCODING_TRIDENT_9);
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
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss, throwDurabilityLoss);
		output.addDoubles(throwDamageMultiplier, speedMultiplier);
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
	}
}
