/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 *  This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package nl.knokko.core.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.menu.Menu;

public class CommandTest implements CommandExecutor {
	
	private Menu testMenu;
	
	public CommandTest() {
		testMenu = new Menu("Test Menu", 7);
		testMenu.setItem(0, Material.BARRIER, (Player player) -> {
			player.closeInventory();
		});
		testMenu.setItem(2, Material.STONE, "Broadcast stone", (Player player) -> {
			Bukkit.broadcastMessage("STONE");
			player.closeInventory();
		});
		testMenu.setItem(8, Material.BLAZE_ROD, "Fire", (Player player) -> {
			player.launchProjectile(Fireball.class);
			player.closeInventory();
		}, "Shoot a fireball", "in the direction", "you are facing");
	}
	
	private static final String[] KEY1 = {"single"};
	private static final String[] KEY2 = {"parent", "child"};
	private static final String[] KEY3 = {"root", "node", "leaf"};

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			//CorePlugin.getInstance().getMenuHandler().openMenu(((Player) sender), testMenu);
			//sender.sendMessage("Open the test menu");
			
			Player player = (Player) sender;
			ItemStack mainItem = player.getInventory().getItemInMainHand();
			GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(mainItem);
			sender.sendMessage("This should be 5: " + nbt.getOrDefault(KEY1, 5));
			sender.sendMessage("This should be hello: " + nbt.getOrDefault(KEY2, "hello"));
			sender.sendMessage("This should be world: " + nbt.getOrDefault(KEY3, "world"));
			
			nbt.set(KEY1, "key1");
			nbt.set(KEY2, 2);
			nbt.set(KEY3, 3);
			
			player.getInventory().setItemInMainHand(nbt.backToBukkit());
			mainItem = player.getInventory().getItemInMainHand();
			nbt = GeneralItemNBT.readOnlyInstance(mainItem);
			
			sender.sendMessage("This should be key1: " + nbt.getOrDefault(KEY1, "whoops"));
			sender.sendMessage("This should be 2: " + nbt.getOrDefault(KEY2, 8));
			sender.sendMessage("This should be 3: " + nbt.getOrDefault(KEY3, 9));
		} else {
			sender.sendMessage("This command is for players");
		}
		return false;
	}
}