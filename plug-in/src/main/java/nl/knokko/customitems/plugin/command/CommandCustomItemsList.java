package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
