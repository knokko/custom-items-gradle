package nl.knokko.customitems.plugin.set.item;

import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.CustomItemsEventHandler;
import nl.knokko.customitems.plugin.recipe.ingredient.Ingredient;

public class CustomShears extends CustomTool {
	
	private final int shearDurabilityLoss;

	public CustomShears(
			short itemDamage, String name, String alias, String displayName, 
			String[] lore, AttributeModifier[] attributes, 
			Enchantment[] defaultEnchantments, long maxDurability,
			boolean allowEnchanting, boolean allowAnvil, Ingredient repairItem, 
			boolean[] itemFlags, int entityHitDurabilityLoss, 
			int blockBreakDurabilityLoss, int shearDurabilityLoss, 
			List<PotionEffect> playerEffects, List<PotionEffect> targetEffects, 
			Collection<EquippedPotionEffect> equippedEffects, String[] commands, 
			ReplaceCondition[] conditions, ConditionOperation op,
			ExtraItemNbt extraNbt, float attackRange
	) {
		super(
				CustomItemType.SHEARS, itemDamage, name, alias, displayName, lore, 
				attributes, defaultEnchantments, maxDurability, allowEnchanting,
				allowAnvil, repairItem, itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange
		);
		this.shearDurabilityLoss = shearDurabilityLoss;
	}
	
	public int getShearDurabilityLoss() {
		return shearDurabilityLoss;
	}
	
	@Override
	public void onBlockBreak(Player player, ItemStack tool, boolean wasSolid, boolean wasFakeMainHand) {
		// Only lose durability when breaking non-solid blocks because we shear it
		if (!wasSolid && blockBreakDurabilityLoss != 0) {
			ItemStack newTool = decreaseDurability(tool, blockBreakDurabilityLoss);
			if (tool != newTool) {
				if (newTool == null) {
					CustomItemsEventHandler.playBreakSound(player);
				}
				if (wasFakeMainHand) {
					player.getInventory().setItemInOffHand(newTool);
				} else {
					player.getInventory().setItemInMainHand(newTool);
				}
			}
		}
	}
}
