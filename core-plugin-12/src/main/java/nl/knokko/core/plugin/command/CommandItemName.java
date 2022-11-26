package nl.knokko.core.plugin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import nl.knokko.core.plugin.item.ItemHelper;

public class CommandItemName implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ItemStack item = player.getInventory().getItemInMainHand();
			if (item != null)
				sender.sendMessage(ChatColor.YELLOW + "The name of that item is " + ItemHelper.getStackName(item));
			else
				sender.sendMessage(ChatColor.RED + "You need to hold the item to test in your hand.");
		} else {
			sender.sendMessage("This command is only for players.");
		}
		return false;
	}
}