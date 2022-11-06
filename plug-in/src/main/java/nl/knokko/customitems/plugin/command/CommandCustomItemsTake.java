package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

class CommandCustomItemsTake {

    final ItemSetWrapper itemSet;

    CommandCustomItemsTake(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    void handle(String[] args, CommandSender sender, boolean enableOutput) {
        if (!sender.hasPermission("customitems.take")) {
            if (enableOutput) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            }
            return;
        }
        if (args.length == 1 || args.length == 2 || args.length == 3) {

            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page < 1) {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + "The page (" + page + ") must be at least 1");
                        }
                        return;
                    }
                } catch (NumberFormatException badPageNumber) {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.RED + "The page number (" + args[1] + ") should be an integer");
                    }
                    return;
                }
            }

            Player target;
            if (args.length > 2) {
                String targetName = args[2];
                Optional<? extends Player> maybeTarget = Bukkit.getServer().getOnlinePlayers().stream().filter(
                        candidate -> candidate.getName().equals(targetName)
                ).findFirst();
                if (maybeTarget.isPresent()) {
                    target = maybeTarget.get();
                } else {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.RED + "Can't find player " + targetName);
                    }
                    return;
                }
            } else {
                if (sender instanceof Player) {
                    target = (Player) sender;
                } else {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.RED + "You should use /kci take <page number> <player name>");
                    }
                    return;
                }
            }

            List<CustomItemValues> itemList = itemSet.get().getItems().stream().collect(Collectors.toList());
            int numItemsPerPage = 6 * 9;
            int firstItemIndex = numItemsPerPage * (page - 1);

            if (firstItemIndex >= itemList.size()) {
                if (enableOutput) {
                    sender.sendMessage(ChatColor.RED + "Page " + page + " shows custom item " + (firstItemIndex + 1) + " and later, but you only have " + itemList.size() + " custom items");
                }
                return;
            }
            int lastItemIndex = Math.min(firstItemIndex + numItemsPerPage - 1, itemList.size() - 1);

            int desiredSize = 1 + lastItemIndex - firstItemIndex;
            int actualSize;
            if (desiredSize % 9 == 0) actualSize = desiredSize;
            else actualSize = 9 * (1 + desiredSize / 9);
            Inventory takeInventory = Bukkit.createInventory(null, actualSize);

            for (int itemIndex = firstItemIndex; itemIndex <= lastItemIndex; itemIndex++) {
                int inventoryIndex = itemIndex - firstItemIndex;
                CustomItemValues customItem = itemList.get(itemIndex);
                takeInventory.setItem(inventoryIndex, wrap(customItem).create(customItem.getMaxStacksize()));
            }
            target.openInventory(takeInventory);
        } else {
            if (enableOutput) {
                sender.sendMessage(ChatColor.RED + "Use /kci take [page number] [player name]");
            }
        }
    }
}
