package nl.knokko.customitems.plugin.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class CustomItemPickups {

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomItemsPlugin.getInstance(), () -> {
			
			List<Player> interestingPlayers = new ArrayList<>();
			List<Item> interestingItems = new ArrayList<>();
			List<Item> closeItems = new ArrayList<>();
			List<Integer> customSlots = new ArrayList<>();
			
			ItemSetWrapper set = CustomItemsPlugin.getInstance().getSet();
			
			worldLoop:
			for (World world : Bukkit.getWorlds()) {

				playerLoop:
				for (Player player : world.getPlayers()) {
					
					PlayerInventory inv = player.getInventory();
					
					// Only players without empty slots are interesting
					if (inv.firstEmpty() == -1) {
						
						// That have at least 1 custom item that is not fully stacked
						for (ItemStack stack : inv.getContents()) {
								
							CustomItemValues custom = set.getItem(stack);
							if (custom != null) {
								if (wrap(custom).needsStackingHelp() && stack.getAmount() < custom.getMaxStacksize()) {
									interestingPlayers.add(player);
									continue playerLoop;
								}
							}
						}
					}
				}
			
				// If there are no interesting players, skip this world
				if (interestingPlayers.isEmpty()) continue worldLoop;
				
				// Gather all dropped custom items
				for (Item item : world.getEntitiesByClass(Item.class)) {
					ItemStack stack = item.getItemStack();
					if (set.getItem(stack) != null) {
						interestingItems.add(item);
					}
				}
				
				if (!interestingItems.isEmpty()) {
					for (Player player : interestingPlayers) {
						
						for (Item item : interestingItems) {
							
							double distanceSq = player.getLocation().distanceSquared(item.getLocation());
							if (distanceSq < 1.0) {
								closeItems.add(item);
							}
						}
						
						if (!closeItems.isEmpty()) {
							
							// Gather all slots with custom items that can stack
							ItemStack[] inv = player.getInventory().getContents();
							for (int index = 0; index < inv.length; index++) {
								ItemStack stack = inv[index];
									
								CustomItemValues custom = set.getItem(stack);
								if (custom != null && wrap(custom).needsStackingHelp() && stack.getAmount() < custom.getMaxStacksize()) {
									customSlots.add(index);
								}
							}
							
							// For each close dropped custom item, check if we should pick it up
							Iterator<Item> itemIt = closeItems.iterator();
							closeItemsLoop:
							while(itemIt.hasNext()) {
								
								Item item = itemIt.next();
								ItemStack droppedStack = item.getItemStack();
								
								// Shouldn't be null
								CustomItemValues customDropped = set.getItem(droppedStack);
								
								// Check if there is a suitable slot
								Iterator<Integer> slotIt = customSlots.iterator();
								while (slotIt.hasNext()) {
									
									int slot = slotIt.next();
									ItemStack slotStack = inv[slot];
									
									// Shouldn't be null now
									CustomItemValues customSlot = set.getItem(slotStack);
									
									if (customSlot == customDropped) {
										
										int remaining = customSlot.getMaxStacksize() - slotStack.getAmount();
										if (remaining < droppedStack.getAmount()) {
											droppedStack.setAmount(droppedStack.getAmount() - remaining);
											slotStack.setAmount(customSlot.getMaxStacksize());
											slotIt.remove();
											
											// Just to be sure this really updates
											item.setItemStack(droppedStack);
										} else {
											
											slotStack.setAmount(slotStack.getAmount() + droppedStack.getAmount());
											
											// Make sure this item disappears completely
											droppedStack.setAmount(0);
											item.remove();
											itemIt.remove();
											continue closeItemsLoop;
										}
									}
								}
							}
							
							// Just to be sure this really updates
							player.getInventory().setContents(inv);
							
							// Clear before going to the next player
							customSlots.clear();
						}
						
						// Clear before going to the next player
						closeItems.clear();
					}
				}
				
				// Clear before moving on to the next world
				interestingPlayers.clear();
				interestingItems.clear();
			}
		}, 100, 20);
	}
}
