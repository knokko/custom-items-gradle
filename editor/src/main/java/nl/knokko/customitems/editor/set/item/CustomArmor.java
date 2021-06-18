/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.set.item;

import java.util.Collection;
import java.util.List;

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.editor.set.item.texture.ArmorTextures;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.util.ReadOnlyReference;
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
import nl.knokko.util.bits.BitOutput;

public class CustomArmor extends CustomTool {
	
	private int red;
	private int green;
	private int blue;
	
	private DamageResistances damageResistances;
	private ReadOnlyReference<ArmorTextures> wornTexture;
	
	public CustomArmor(
            CustomItemType itemType, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes,
            Enchantment[] defaultEnchantments, long durability,
            boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem,
            NamedImage texture, int red, int green, int blue, boolean[] itemFlags,
            int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
            DamageResistances damageResistances, byte[] customModel,
            List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands,
            ReplaceCondition[] conditions, ConditionOperation op,
            ExtraItemNbt extraNbt, ReadOnlyReference<ArmorTextures> wornTexture,
            float attackRange
	) {
		super(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, durability, allowEnchanting, allowAnvil, 
				repairItem, texture, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, customModel, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op,
				extraNbt, attackRange
		);
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.damageResistances = damageResistances;
		this.wornTexture = wornTexture;
	}
	
	@Override
	public void export(BitOutput output) {
		
		/* Oldest encoding:
		output.addByte(ItemEncoding.ENCODING_ARMOR_4);
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
		output.addLong(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}*/
		
		/* Old encoding
		output.addByte(ItemEncoding.ENCODING_ARMOR_5);
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
		output.addLong(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
		*/
		
		/* Previous encoding
		output.addByte(ItemEncoding.ENCODING_ARMOR_6);
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
		output.addLong(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
		damageResistances.save12(output);*/
		
		/* Previous encoding
		output.addByte(ItemEncoding.ENCODING_ARMOR_7);
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
		output.addLong(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
		damageResistances.save14(output); */
		
		/* Previous Encoding
		output.addByte(ItemEncoding.ENCODING_ARMOR_8);
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
		output.addLong(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
		damageResistances.save14(output);
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

		/*
		output.addByte(getEncoding9());
		output.addJavaString(itemType.name());
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
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
		damageResistances.save14(output);
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
		output.addBoolean(wornTexture != null);
		if (wornTexture != null) {
			output.addString(wornTexture.get().getName());
		}
		output.addFloat(attackRange);
		*/

		output.addByte(getEncoding10());
		output.addJavaString(itemType.name());
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
		if (isLeather()) {
			output.addBytes((byte) red, (byte) green, (byte) blue);
		}
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);
		damageResistances.save17(output);
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
		output.addBoolean(wornTexture != null);
		if (wornTexture != null) {
			output.addString(wornTexture.get().getName());
		}
		output.addFloat(attackRange);
	}
	
	protected byte getEncoding10() {
		return ItemEncoding.ENCODING_ARMOR_10;
	}
	
	private boolean isLeather() {
		return itemType.isLeatherArmor();
	}
	
	public int getRed() {
		return red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getBlue() {
		return blue;
	}
	
	public void setRed(int newRed) {
		red = newRed;
	}
	
	public void setGreen(int newGreen) {
		green = newGreen;
	}
	
	public void setBlue(int newBlue) {
		blue = newBlue;
	}
	
	public DamageResistances getDamageResistances() {
		return damageResistances;
	}
	
	public void setDamageResistances(DamageResistances newResistances) {
		damageResistances = newResistances;
	}
	
	public ReadOnlyReference<ArmorTextures> getWornTexture() {
		return wornTexture;
	}
	
	public void setWornTexture(ReadOnlyReference<ArmorTextures> newWornTexture) {
		wornTexture = newWornTexture;
	}
}