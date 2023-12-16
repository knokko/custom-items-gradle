package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.set.loading.ItemSetLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static nl.knokko.customitems.plugin.command.CommandCustomItems.getOnlinePlayer;

class CommandCustomItemsResourcePack {

    private final ItemSetLoader loader;

    CommandCustomItemsResourcePack(ItemSetLoader loader) {
        this.loader = loader;
    }

    void handle(String[] args, CommandSender sender, boolean enableOutput) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                loader.sendResourcePack((Player) sender);
            } else if (enableOutput) sender.sendMessage(ChatColor.RED + "You should use /kci resourcepack <player>");
        } else if (args.length == 2) {
            if (sender.hasPermission("customitems.resourcepack.otherplayers")) {
                Player player = getOnlinePlayer(args[1]);
                if (player != null) {
                    loader.sendResourcePack(player);
                    if (enableOutput) sender.sendMessage(ChatColor.GREEN + "Done");
                } else if (enableOutput) sender.sendMessage(ChatColor.RED + "Can't find online player " + args[1]);
            } else if (enableOutput) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to refresh someone elses resourcepack");
            }
        } else if (enableOutput) sender.sendMessage(ChatColor.RED + "You should use /kci resourcepack [player]");
    }
}
