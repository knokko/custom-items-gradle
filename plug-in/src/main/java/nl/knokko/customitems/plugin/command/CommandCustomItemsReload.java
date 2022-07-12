package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class CommandCustomItemsReload {

    void handle(CommandSender sender) {
        if (!sender.hasPermission("customitems.reload")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            return;
        }
        CustomItemsPlugin.getInstance().reload();
        sender.sendMessage("The item set and config should have been reloaded");
    }
}
