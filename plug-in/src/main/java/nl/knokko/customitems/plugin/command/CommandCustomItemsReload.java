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

        CustomItemsPlugin instance = CustomItemsPlugin.getInstance();
        if (args.length == 1) {
            instance.getItemSetLoader().reload(sendMessage);
        } else if (args.length == 2) {
            instance.getItemSetLoader().reload(
                    sendMessage, instance.getSet().get().getExportSettings().getHostAddress(), args[1]
            );
        } else if (args.length == 3) {
            instance.getItemSetLoader().reload(sendMessage, args[2], args[1]);
        } else {
            sendMessage.accept(ChatColor.RED + "You should use /kci reload [hash] [host]");
        }
    }
}
