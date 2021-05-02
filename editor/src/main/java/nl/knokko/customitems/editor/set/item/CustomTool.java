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
import nl.knokko.util.bits.BitOutput;

public class CustomTool extends CustomItem {
	
	protected long durability;
	
	protected boolean allowEnchanting;
	protected boolean allowAnvil;
	
	protected Ingredient repairItem;
	
	protected int entityHitDurabilityLoss;
	protected int blockBreakDurabilityLoss;

	public CustomTool(
            CustomItemType itemType, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes,
            Enchantment[] defaultEnchantments, long durability,
            boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem,
            NamedImage texture, boolean[] itemFlags, int entityHitDurabilityLoss,
            int blockBreakDurabilityLoss, byte[] customModel,
            List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands,
            ReplaceCondition[] conditions, ConditionOperation op,
            ExtraItemNbt extraNbt, float attackRange
	) {
		super(
				itemType, name, alias, displayName, lore, attributes, 
				defaultEnchantments, texture, itemFlags, customModel, 
				playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange
		);
		this.durability = durability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
		this.repairItem = repairItem;
		this.entityHitDurabilityLoss = entityHitDurabilityLoss;
		this.blockBreakDurabilityLoss = blockBreakDurabilityLoss;
	}
	
	@Override
	public void export(BitOutput output) {
		/*
		 * First encoding
		output.addByte(ItemEncoding.ENCODING_TOOL_2);
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
		output.addInt(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		*/
		
		/*
		 * Second encoding
		output.addByte(ItemEncoding.ENCODING_TOOL_3);
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
		output.addInt(durability);
		output.addBoolean(allowEnchanting);
		output.addBoolean(allowAnvil);
		repairItem.save(output);
		*/
		
		/* Previous encoding
		output.addByte(ItemEncoding.ENCODING_TOOL_4);
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
		repairItem.save(output);*/
		
		/* Previous Encoding
		output.addByte(ItemEncoding.ENCODING_TOOL_5);
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
		output.addBooleans(itemFlags);
		output.addInts(entityHitDurabilityLoss, blockBreakDurabilityLoss);*/
		
		/* Previous Encoding
		output.addByte(ItemEncoding.ENCODING_TOOL_6);
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
		output.addByte((byte) commands.length);
		for (String command : commands) {
			output.addJavaString(command);
		} */
		
		output.addByte(ItemEncoding.ENCODING_TOOL_9);
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
	}
	
	public boolean allowEnchanting() {
		return allowEnchanting;
	}
	
	public boolean allowAnvilActions() {
		return allowAnvil;
	}
	
	public Ingredient getRepairItem() {
		return repairItem;
	}
	
	public long getDurability() {
		return durability;
	}
	
	public int getEntityHitDurabilityLoss() {
		return entityHitDurabilityLoss;
	}
	
	public int getBlockBreakDurabilityLoss() {
		return blockBreakDurabilityLoss;
	}
	
	public void setAllowEnchanting(boolean allow) {
		allowEnchanting = allow;
	}
	
	public void setAllowAnvilActions(boolean allow) {
		allowAnvil = allow;
	}
	
	public void setRepairItem(Ingredient item) {
		repairItem = item;
	}
	
	public void setDurability(long durability) {
		this.durability = durability;
	}
	
	public void setEntityHitDurabilityLoss(int durabilityLoss) {
		this.entityHitDurabilityLoss = durabilityLoss;
	}
	
	public void setBlockBreakDurabilityLoss(int durabilityLoss) {
		this.blockBreakDurabilityLoss = durabilityLoss;
	}
}