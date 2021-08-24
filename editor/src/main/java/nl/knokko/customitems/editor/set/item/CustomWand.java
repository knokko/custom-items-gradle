package nl.knokko.customitems.editor.set.item;

import java.util.Collection;
import java.util.List;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.util.bits.BitOutput;

public class CustomWand extends CustomItem {
	
	public CIProjectile projectile;
	
	public int cooldown;
	/** If charges is null, the wand doesn't need charges and only has to worry about its cooldown */
	public WandCharges charges;
	/** The amount of projectiles to shoot each time the wand is used */
	public int amountPerShot;

	public CustomWand(
			CustomItemType itemType, String name, String alias, String displayName, 
			String[] lore, AttributeModifier[] attributes, 
			Enchantment[] defaultEnchantments, NamedImage texture,
			boolean[] itemFlags, byte[] customModel, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> equippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op, 
			CIProjectile projectile, int cooldown, WandCharges charges, 
			int amountPerShot, ExtraItemNbt extraNbt, float attackRange
	) {
		super(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, texture, itemFlags, customModel, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op, extraNbt,
				attackRange
		);
		this.projectile = projectile;
		this.cooldown = cooldown;
		this.charges = charges;
		this.amountPerShot = amountPerShot;
	}

	@Override
	public void export(BitOutput output) {
		/* Previous Encoding
		output.addByte(ItemEncoding.ENCODING_WAND_8);
		output.addJavaString(itemType.name());
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
		output.addBooleans(itemFlags);
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
		}
		
		output.addString(projectile.name);
		output.addInt(cooldown);
		output.addBoolean(charges != null);
		if (charges != null) {
			charges.toBits(output);
		}
		output.addInt(amountPerShot);
		*/
		output.addByte(ItemEncoding.ENCODING_WAND_10);
		output.addJavaString(itemType.name());
		output.addShort(itemDamage);
		output.addJavaString(name);
		output.addString(name);
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
		output.addBooleans(itemFlags);
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
		
		output.addString(projectile.name);
		output.addInt(cooldown);
		output.addBoolean(charges != null);
		if (charges != null) {
			charges.toBits(output);
		}
		output.addInt(amountPerShot);
		extraNbt.save(output);
		output.addFloat(attackRange);
	}
}
