package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collection;

class CommandCustomItemsList {

    final ItemSetWrapper itemSet;

    CommandCustomItemsList(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    void handle(CommandSender sender) {
        if (!sender.hasPermission("customitems.list")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command");
            return;
        }

        Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();

        if (!errors.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "The following errors occurred while enabling " +
                    "this plug-in. These errors will likely cause this command to fail:");
            for (String error : errors) {
                sender.sendMessage(ChatColor.DARK_RED + error);
            }
        }

        if (itemSet.get().getItems().size() > 0) {
            sender.sendMessage(ChatColor.AQUA + "All custom items:");
            for (CustomItemValues item : itemSet.get().getItems()) {
                if (item.getAlias().isEmpty()) {
                    sender.sendMessage(item.getName());
                } else {
                    sender.sendMessage(item.getName() + " (" + item.getAlias() + ")");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "There are 0 custom items");
        }

        if (itemSet.get().getBlocks().size() > 0) {
            sender.sendMessage(ChatColor.AQUA + "All custom blocks:");
            for (CustomBlockValues block : itemSet.get().getBlocks()) {
                sender.sendMessage(block.getName());
            }
        } else {
            sender.sendMessage(ChatColor.AQUA + "There are 0 custom blocks");
        }
    }
}
