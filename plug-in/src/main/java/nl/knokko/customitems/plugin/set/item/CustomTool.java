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
package nl.knokko.customitems.plugin.set.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.CustomItemsEventHandler;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomTool extends CustomItem {
	
	private static final String DURABILITY_SPLIT = " / ";
	
	private static String prefix() {
		return CustomItemsPlugin.getInstance().getLanguageFile().getDurabilityPrefix();
	}
	
	protected static String createDurabilityLine(long current, long max) {
		return prefix() + " " + current + DURABILITY_SPLIT + max;
	}
	
	protected final long maxDurability;
	
	protected final boolean allowEnchanting;
	protected final boolean allowAnvil;
	
	protected final Ingredient repairItem;
	
	protected final int entityHitDurabilityLoss;
	protected final int blockBreakDurabilityLoss;

	public CustomTool(
			CustomItemType itemType, short itemDamage, String name, String alias,
			String displayName, String[] lore, AttributeModifier[] attributes, 
			Enchantment[] defaultEnchantments, long maxDurability, 
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, 
			boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, List<PotionEffect> playerEffects, 
			List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> equippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op,
			ExtraItemNbt extraNbt, float attackRange
	) {
		super(
				itemType, itemDamage, name, alias, displayName, lore, attributes, 
				defaultEnchantments, itemFlags, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
		this.maxDurability = maxDurability;
		this.allowEnchanting = allowEnchanting;
		this.allowAnvil = allowAnvil;
		this.repairItem = repairItem;
		this.entityHitDurabilityLoss = entityHitDurabilityLoss;
		this.blockBreakDurabilityLoss = blockBreakDurabilityLoss;
	}
	
	@Override
	public int getMaxStacksize() {
		return 1;
	}
	
	@Override
	public boolean allowVanillaEnchanting() {
		return allowEnchanting;
	}
	
	@Override
	public boolean allowAnvilActions() {
		return allowAnvil;
	}
	
	public Ingredient getRepairItem() {
		return repairItem;
	}
	
	@Override
	public Long getMaxDurabilityNew() {
		if (maxDurability == UNBREAKABLE_TOOL_DURABILITY) {
			return null;
		} else {
			return maxDurability;
		}
	}
	
	@Override
	protected List<String> createLore(){
		return createLore(getMaxDurabilityNew());
	}
	
	@Override
	public List<String> createLore(Long currentDurability){
		List<String> itemLore = new ArrayList<String>(lore.length + 2);
        if (!isUnbreakable()) {
        	if (currentDurability == null) {
        		currentDurability = maxDurability;
        	}
        	itemLore.add(createDurabilityLine(currentDurability, maxDurability));
        	itemLore.add("");
        }
		Collections.addAll(itemLore, lore);
        
        return itemLore;
	}
	
	public ItemStack create(int amount, long durability) {
		if (amount != 1) throw new IllegalArgumentException("Amount must be 1, but is " + amount);
		ItemStack partialResult = super.create(amount, createLore(durability));
		ItemStack[] pResult = {partialResult};

		if (!this.isUnbreakable()) {
			CustomItemNBT.readWrite(partialResult, nbt -> {
				nbt.setDurability(durability);
			}, result -> pResult[0] = result);
		}

		return pResult[0];
	}
	
	@Override
	public void onBlockBreak(Player player, ItemStack tool, boolean wasSolid, boolean wasFakeMainHand) {
		if (wasSolid && blockBreakDurabilityLoss != 0) {
			
			ItemStack decreased = decreaseDurability(tool, blockBreakDurabilityLoss);
			if (decreased == null) {
				for (ReplaceCondition cond : conditions) {
					if (cond.getCondition() == ReplacementCondition.ISBROKEN) {
						ItemStack replace = CustomItemsPlugin.getInstance().getSet().getCustomItemByName(cond.getReplacingItemName()).create(1);
						player.getInventory().addItem(replace);
					}
				}
				CustomItemsEventHandler.playBreakSound(player);
			}
			if (decreased != tool) {
				if (wasFakeMainHand) {
					player.getInventory().setItemInOffHand(DualWieldSupport.purge(decreased));
				} else {
					player.getInventory().setItemInMainHand(decreased);
				}
			}
		}
	}
	
	@Override
	public void onEntityHit(LivingEntity attacker, ItemStack tool, Entity target) {
		super.onEntityHit(attacker, tool, target);
		if (entityHitDurabilityLoss != 0) {
			ItemStack decreased = decreaseDurability(tool, entityHitDurabilityLoss);
			if (decreased == null && attacker instanceof Player) {
				CustomItemsEventHandler.playBreakSound((Player) attacker);
			}
			if (decreased != tool) {
				attacker.getEquipment().setItemInMainHand(decreased);
			}
		}
	}
	
	public boolean forbidDefaultUse(ItemStack item) {
    	return false;
    }
	
	/**
	 * @param stack The (custom) item stack to decrease the durability of
	 * @return The same item stack if nothing changed, or a new ItemStack that should
	 * replace the old one (null if the stack should break). 
	 * 
	 * It is the task of the caller to ensure that the old one really gets replaced!
	 */
	public ItemStack decreaseDurability(ItemStack stack, int damage) {
		if (isUnbreakable() || !stack.hasItemMeta()) {
			return stack;
		}
		if (Math.random() <= 1.0 / (1 + stack.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY))) {
			
			// Don't to anything if this tool is unbreakable
			if (maxDurability != UNBREAKABLE_TOOL_DURABILITY) {
				
				ItemStack[] pResult = {stack};
				Long[] pNewDurability = {null};
				
				CustomItemNBT.readWrite(stack, nbt -> {
					Long durability = nbt.getDurability();
					if (durability != null) {
						if (durability > damage) {
							durability -= damage;
							nbt.setDurability(durability);
							pNewDurability[0] = durability;
						} else {
							
							// If this block is reached, the item will break
							pNewDurability[0] = 0L;
						}
					} else {
						/*
						 * If this happens, the item stack doesn't have durability
						 * stored in its lore, even though it should be breakable.
						 * This probably means that the custom item used to be
						 * unbreakable in the previous version of the item set, but
						 * became breakable in the current version of the item set.
						 * We have a repeating task to frequently check for these
						 * problems, so we will just do nothing and wait for the
						 * repeating task to fix this.
						 */
					}
				}, newStack -> pResult[0] = newStack);
				stack = pResult[0];
				
				if (pNewDurability[0] != null) {
					long newDurability = pNewDurability[0];
					if (newDurability == 0) {
						return null;
					}
					ItemMeta meta = stack.getItemMeta();
					meta.setLore(createLore(newDurability));
					stack.setItemMeta(meta);
				}
				
				return stack;
			} else {
				
				// The item is unbreakable, so just return the same item right away
				return stack;
			}
		} else {
			
			// The item has durability enchantment, and shouldn't receive damage now
			return stack;
		}
	}
	
	public static class IncreaseDurabilityResult {
		
		public final ItemStack stack;
		public final long increasedAmount;
		
		IncreaseDurabilityResult(ItemStack stack, long increasedAmount) {
			this.stack = stack;
			this.increasedAmount = increasedAmount;
		}
	}
	
	public IncreaseDurabilityResult increaseDurability(ItemStack stack, int amount) {
		if (isUnbreakable() || !stack.hasItemMeta()) {
			return new IncreaseDurabilityResult(stack, 0);
		}
		
		ItemStack[] pStack = {stack};
		long[] pIncreasedAmount = {0L};
		long[] pNewDurability = {-1L};
		
		CustomItemNBT.readWrite(stack, nbt -> {
			Long oldDurability = nbt.getDurability();
			if (oldDurability != null) {
				long newDurability;
				if (oldDurability + amount <= maxDurability) {
					newDurability = oldDurability + amount;
				} else {
					newDurability = maxDurability;
				}
				pIncreasedAmount[0] = newDurability - oldDurability;
				pNewDurability[0] = newDurability;
				nbt.setDurability(newDurability);
			} else {
				/*
				 * If this happens, the item stack doesn't have durability
				 * stored in its lore, even though it should be breakable.
				 * This probably means that the custom item used to be
				 * unbreakable in the previous version of the item set, but
				 * became breakable in the current version of the item set.
				 * We have a repeating task to frequently check for these
				 * problems, so we will just do nothing and wait for the
				 * repeating task to fix this.
				 */
			}
		}, newStack -> pStack[0] = newStack);
		stack = pStack[0];
		long increasedAmount = pIncreasedAmount[0];
		
		if (increasedAmount > 0) {
			long newDurability = pNewDurability[0];
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(createLore(newDurability));
			stack.setItemMeta(meta);
		}
		
		return new IncreaseDurabilityResult(stack, increasedAmount);
	}
	
	protected boolean isUnbreakable() {
		return maxDurability == UNBREAKABLE_TOOL_DURABILITY;
	}
	
	public long getMaxDurability() {
		return maxDurability;
	}
	
	public long getDurability(ItemStack stack) {
		long[] pResult = {0};
		CustomItemNBT.readOnly(stack, nbt -> {
			Long durability = nbt.getDurability();
			if (durability != null) {
				pResult[0] = durability;
			} else {
				pResult[0] = UNBREAKABLE_TOOL_DURABILITY;
			}
		});
		return pResult[0];
	}
	
	@Override
	protected void initNBT(CustomItemNBT nbt) {
		if (maxDurability != UNBREAKABLE_TOOL_DURABILITY) {
			nbt.setDurability(maxDurability);
		}
	}
}