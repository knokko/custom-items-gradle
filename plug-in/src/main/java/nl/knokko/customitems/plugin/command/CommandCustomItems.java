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
package nl.knokko.customitems.plugin.command;

import java.io.File;
import java.io.IOException;
import java.util.*;

import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.knokko.customitems.plugin.LanguageFile;

public class CommandCustomItems implements CommandExecutor {
	
	public static Player getOnlinePlayer(String name) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for(Player player : players)
			if(player.getName().equals(name))
				return player;
		return null;
	}

	private final ItemSetWrapper itemSet;
	private final LanguageFile lang;

	private final String initialResourcePackURL;
	private final String initialResourcePackSHA1;
	
	public CommandCustomItems(ItemSetWrapper itemSet, LanguageFile lang) {
		this.itemSet = itemSet;
		this.lang = lang;

		File serverProperties = new File("server.properties");
		final String RESOURCE_PACK = "resource-pack=";
		final String RESOURCE_PACK_HASH = "resource-pack-sha1=";

		String resourcePackUrl = null;
		String resourcePackHash = null;
		try {
			Scanner scanner = new Scanner(serverProperties);
			while (scanner.hasNextLine()) {
				String currentLine = scanner.nextLine();
				if (currentLine.startsWith(RESOURCE_PACK)) {
					resourcePackUrl = currentLine.substring(RESOURCE_PACK.length()).replace("\\", "");
				} else if (currentLine.startsWith(RESOURCE_PACK_HASH)) {
					resourcePackHash = currentLine.substring(RESOURCE_PACK_HASH.length());
				}
			}
			scanner.close();
		} catch (IOException ioTrouble) {
			Bukkit.getLogger().warning("Can't find server.properties. This will reduce quality of /kci debug");
		}

		this.initialResourcePackURL = resourcePackUrl;
		this.initialResourcePackSHA1 = resourcePackHash;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			return false;
		} else {
			boolean enableOutput = true;
			if (args[0].equals("disableoutput")) {
				enableOutput = false;
				args = Arrays.copyOfRange(args, 1, args.length);
			}

			switch (args[0]) {
				case "give":
				    new CommandCustomItemsGive(itemSet, lang).handle(args, sender, enableOutput);
					break;
				case "take": {
					new CommandCustomItemsTake(itemSet).handle(args, sender, enableOutput);
					break;
				}
				case "list": {
					new CommandCustomItemsList(itemSet).handle(sender);
					break;
				}
				case "damage": {
					// For the sake of code reuse, keep the body of this case empty and intentionally leave out
					// the *break*
				}
				case "repair": {
					new CommandCustomItemsRepair(itemSet).handle(args, sender, enableOutput);
					break;
				}
				case "debug": {
					new CommandCustomItemsDebug(itemSet, initialResourcePackURL, initialResourcePackSHA1).handle(args, sender);
					break;
				}
				case "setblock": {
					new CommandCustomItemsSetBlock(itemSet).handle(args, sender, enableOutput);
					break;
				}
				case "encode": {
					new CommandCustomItemsEncode().handle(sender);
					break;
				}
				case "reload": {
					new CommandCustomItemsReload().handle(sender);
					break;
				}
				case "container": {
					new CommandCustomItemsContainer(itemSet).handle(args, sender, enableOutput);
					break;
				}
				default:
					return false;
			}
		}
		return true;
	}
}
