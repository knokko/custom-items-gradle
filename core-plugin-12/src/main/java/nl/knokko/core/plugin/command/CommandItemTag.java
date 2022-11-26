package nl.knokko.core.plugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.ItemHelper;

public class CommandItemTag implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null) {
				sender.sendMessage(ChatColor.DARK_GREEN + ItemHelper.getTagAsString(item));
			} else {
				sender.sendMessage("Hold the item to examine in your main hand");
			}
		} else {
			sender.sendMessage("This command is for players");
		}
		return false;
	}

}
