package nl.knokko.customitems.plugin.util;

import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class ItemUtils {

	public static boolean isEmpty(ItemStack stack) {
		if(stack == null || 
				ItemHelper.getMaterialName(stack) == CIMaterial.AIR.name() ||
				stack.getAmount() == 0) {
			return true;
		}
		if (GeneralItemNBT.readOnlyInstance(stack).getOrDefault(
				ContainerInstance.PLACEHOLDER_KEY, 0) == 1
		) {
			return true;
		}
		return false;
	}
	
	public static boolean isCustom(ItemStack stack) {
		return CustomItemsPlugin.getInstance().getSet().getItem(stack) != null;
	}
	
	public static int getMaxStacksize(ItemStack stack) {
		CustomItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(stack);
		if (customItem != null) {
			return customItem.getMaxStacksize();
		}
		
		return stack.getMaxStackSize();
	}
}
