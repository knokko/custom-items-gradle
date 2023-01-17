package nl.knokko.customitems.plugin.command;

import java.util.*;

import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.loading.ItemSetLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.knokko.customitems.plugin.config.LanguageFile;

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

	public CommandCustomItems(ItemSetWrapper itemSet, LanguageFile lang) {
		this.itemSet = itemSet;
		this.lang = lang;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (KciNms.instance == null) {
			sender.sendMessage(ChatColor.DARK_RED + "This plug-in is not active because this minecraft version is not supported");
			return true;
		}

		if(args.length == 0) {
			return false;
		} else {
			boolean enableOutput = true;
			if (args[0].equals("disableoutput")) {
				enableOutput = false;
				args = Arrays.copyOfRange(args, 1, args.length);
			}

			if (enableOutput && sender.hasPermission("customitems.debug") && !args[0].equals("reload")) {
				ItemSetLoader loader = CustomItemsPlugin.getInstance().getItemSetLoader();
				if (loader.didLoseResourcePack()) {
					sender.sendMessage(ChatColor.YELLOW + "Warning: the resource pack is no longer hosted. " +
							"You can try to fix it with /kci reload");
				}
				if (loader.getLastLoadError() != null) {
					sender.sendMessage(ChatColor.YELLOW + "Warning: an error occurred during start-up:");
					sender.sendMessage(loader.getLastLoadError());
				}
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
					new CommandCustomItemsDebug(itemSet).handle(sender);
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
					new CommandCustomItemsReload().handle(args, sender, enableOutput);
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
