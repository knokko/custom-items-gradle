package nl.knokko.customitems.plugin.util;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.command.CommandCustomItemsGive;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class ItemUtils {

	public static boolean isEmpty(ItemStack stack) {
		if(stack == null ||
				KciNms.instance.items.getMaterialName(stack).equals(VMaterial.AIR.name()) ||
				stack.getAmount() == 0) {
			return true;
		}

		return NBT.get(stack, nbt -> NbtHelper.getNested(nbt, ContainerInstance.PLACEHOLDER_KEY, 0) == 1);
	}
	
	public static boolean isCustom(ItemStack stack) {
		return CustomItemsPlugin.getInstance().getSet().getItem(stack) != null;
	}
	
	public static int getMaxStacksize(ItemStack stack) {
		KciItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(stack);
		if (customItem != null) {
			return customItem.getMaxStacksize();
		}
		
		return stack.getMaxStackSize();
	}

	/**
	 * @return A list of ItemStacks that didn't fit in the inventory
	 */
	public static Collection<ItemStack> giveItems(ItemSetWrapper itemSet, Inventory destination, Collection<ItemStack> items) {
		Collection<ItemStack> didNotFit = new ArrayList<>();
		for (ItemStack item : items) {
			KciItem customItem = itemSet.getItem(item);
			if (customItem != null && wrap(customItem).needsStackingHelp()) {
				if (!CommandCustomItemsGive.giveCustomItemToInventory(itemSet, destination, customItem, item.getAmount())) {
					didNotFit.add(item);
				}
			} else {
				didNotFit.addAll(destination.addItem(item).values());
			}
		}

		return didNotFit;
	}

	public static void giveCustomItem(ItemSetWrapper itemSet, Player player, KciItem item) {
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
