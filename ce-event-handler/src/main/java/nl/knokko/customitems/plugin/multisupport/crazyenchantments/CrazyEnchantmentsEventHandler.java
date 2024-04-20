package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.HellForgedUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciTool;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.plugin.CustomItemsPlugin;

public class CrazyEnchantmentsEventHandler implements Listener {

	public CrazyEnchantmentsEventHandler() {
		CrazyEnchantmentsSupport.crazyEnchantmentsFunctions = new CrazyEnchantmentsFunctions() {

			private CrazyManager crazyManager() {
				return CrazyEnchantments.getPlugin().getStarter().getCrazyManager();
			}

			private CEnchantment fromName(String enchantmentName) {
				return crazyManager().getEnchantmentFromName(enchantmentName);
			}

			@Override
			public int getLevel(ItemStack itemStack, String enchantmentName) {
				return CEnchantments.getFromName(enchantmentName).getLevel(itemStack);
			}

			@Override
			public ItemStack add(ItemStack itemStack, String enchantmentName, int level) {
				return crazyManager().addEnchantment(itemStack, crazyManager().getEnchantmentFromName(enchantmentName), level);
			}

			@Override
			public ItemStack remove(ItemStack itemStack, String enchantmentName) {
				return CrazyEnchantments.getPlugin().getStarter().getEnchantmentBookSettings().removeEnchantment(
						itemStack, fromName(enchantmentName)
				);
			}
		};
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHellForge(HellForgedUseEvent event) {
		if (ItemUtils.isCustom(event.getItem())) {

			// Unfortunately, the HellForgedUseEvent doesn't allow us to replace the item, which is required to change
			// its custom durability. This is bypassed by manually repairing using the PlayerMoveEvent
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void manuallyApplyHellForged(PlayerMoveEvent event) {
		ItemStack[] contents = event.getPlayer().getInventory().getContents();
		boolean didChange = false;

        for (ItemStack itemStack : contents) {
            KciItem customItem = CustomItemsPlugin.getInstance().getSet().getItem(itemStack);
            if (customItem instanceof KciTool) {
                KciTool customTool = (KciTool) customItem;
                int hellForgedLevel = CEnchantments.HELLFORGED.getLevel(itemStack);

                if (hellForgedLevel > 0 && CEnchantments.HELLFORGED.chanceSuccessful()) {
                    long increasedAmount = CustomToolWrapper.wrap(customTool).increaseDurability(itemStack, hellForgedLevel);
                    if (increasedAmount > 0) didChange = true;
                }
            }
        }

		if (didChange) {
			event.getPlayer().getInventory().setContents(contents);
		}
	}
}
