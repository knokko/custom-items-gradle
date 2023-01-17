package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.command.CommandCustomItemsGive;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

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

	public static void giveCustomItem(ItemSetWrapper itemSet, Player player, CustomItemValues item) {
		if (wrap(item).needsStackingHelp()) {
			if (!CommandCustomItemsGive.giveCustomItemToInventory(itemSet, player.getInventory(), item, 1)) {
				player.getWorld().dropItem(player.getLocation(), wrap(item).create(1));
			}
		} else {
			for (ItemStack didNotFit : player.getInventory().addItem(wrap(item).create(1)).values()) {
				player.getWorld().dropItem(player.getLocation(), didNotFit);
			}
		}
	}
}
