package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

class CommandCustomItemsReload {

    void handle(String[] args, CommandSender sender, boolean enableOutput) {
        if (!sender.hasPermission("customitems.reload")) {
            if (enableOutput) sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            return;
        }

        Consumer<String> sendMessage = message -> {
            if (enableOutput) sender.sendMessage(message);
        };

        if (args.length == 1) {
            CustomItemsPlugin.getInstance().getItemSetLoader().reload(sendMessage);
        } else {
            CustomItemsPlugin.getInstance().getItemSetLoader().reload(sendMessage, args[1]);
        }
    }
}
