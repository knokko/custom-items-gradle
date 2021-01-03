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
package nl.knokko.core.plugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.attributes.ItemAttributes;

public class CommandItemAttribute implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null) {
				if (args.length == 0)
					player.sendMessage("Use /knokkocore itemattribute set/get/clear/reset/list ...");
				else {
					if (args[0].equals("set")) {
						if (args.length < 3) {
							player.sendMessage("Use /knokkocore itemattribute set <attributeName> <value> [slot]");
						} else {
							try {
								double value = Double.parseDouble(args[2]);
								String slot = "mainhand";
								if (args.length >= 4)
									slot = args[3];
								player.getInventory()
										.setItemInMainHand(ItemAttributes.setAttribute(item, args[1], value, slot, 0));
							} catch (NumberFormatException nfe) {
								player.sendMessage("'" + args[2] + "' should be a number");
							}
						}
					} else if (args[0].equals("get")) {
						if (args.length < 2) {
							player.sendMessage("Use /knokkocore itemattribute get <attributeName>");
						} else {
							double value = ItemAttributes.getAttribute(item, args[1]);
							if (value == value)
								player.sendMessage("The value is " + value);
							else
								player.sendMessage("The value is not set");
						}
					} else if (args[0].equals("clear")) {
						player.getInventory().setItemInMainHand(ItemAttributes.clearAttributes(item));
					} else if (args[0].equals("list")) {
						String[] attributes = ItemAttributes.listAttributes(item);
						if (attributes != null) {
							player.sendMessage("Attributes: ");
							for (String attribute : attributes)
								player.sendMessage(attribute);
						} else
							player.sendMessage("This item doesn't have the attribute modifiers tag");
					} else if (args[0].equals("reset")) {
						player.getInventory().setItemInMainHand(ItemAttributes.resetAttributes(item));
					} else
						player.sendMessage("Use /knokkocore itemattribute set/get/clear/reset/list ...");
				}
			} else
				player.sendMessage(ChatColor.RED + "You need to hold the item to modify in your main hand.");
		} else
			sender.sendMessage("Only players can use this command.");
		return false;
	}
}