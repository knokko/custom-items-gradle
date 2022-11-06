package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.nms.KciNms;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;

public class ItemUtils {

	public static boolean isEmpty(ItemStack stack) {
		if(stack == null ||
				KciNms.instance.items.getMaterialName(stack).equals(CIMaterial.AIR.name()) ||
				stack.getAmount() == 0) {
			return true;
		}
		return KciNms.instance.items.generalReadOnlyNbt(stack).getOrDefault(
				ContainerInstance.PLACEHOLDER_KEY, 0) == 1;
	}
	
	public static boolean isCustom(ItemStack stack) {
		return CustomItemsPlugin.getInstance().getSet().getItem(stack) != null;
	}
	
	public static int getMaxStacksize(ItemStack stack) {
		CustomItemValues customItem = CustomItemsPlugin.getInstance().getSet().getItem(stack);
		if (customItem != null) {
			return customItem.getMaxStacksize();
		}
		
		return stack.getMaxStackSize();
	}
}
