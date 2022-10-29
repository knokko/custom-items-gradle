package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.LanguageFile;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

import static nl.knokko.customitems.plugin.command.CommandCustomItems.getOnlinePlayer;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

class CommandCustomItemsGive {

    final ItemSetWrapper itemSet;
    final LanguageFile lang;

    CommandCustomItemsGive(ItemSetWrapper itemSet, LanguageFile lang) {
        this.itemSet = itemSet;
        this.lang = lang;
    }

    private void sendGiveUseage(CommandSender sender) {
        sender.sendMessage(lang.getCommandGiveUseage());
    }

    void handle(String[] args, CommandSender sender, boolean enableOutput) {
        if (
                !sender.hasPermission("customitems.give") && itemSet.get().getItems().stream().noneMatch(
                        item -> sender.hasPermission("customitems.give." + item.getName())
                )
        ) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            return;
        }

        if (args.length == 2 || args.length == 3 || args.length == 4) {

            Collection<String> errors = CustomItemsPlugin.getInstance().getLoadErrors();
            if (!errors.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "The following errors occurred while enabling " +
                        "this plug-in. These errors will likely cause this command to fail:");
                for (String error : errors) {
                    sender.sendMessage(ChatColor.DARK_RED + error);
                }
            }

            // Try to find a custom item with the give name
            CustomItemValues item = itemSet.getItem(args[1]);

            // If no such item is found, try to find one with the given alias
            if (item == null) {
                for (CustomItemValues candidate : itemSet.get().getItems()) {
                    if (candidate.getAlias().equals(args[1])) {
                        item = candidate;
                        break;
                    }
                }
            }

            if (item != null) {
                if (!sender.hasPermission("customitems.give") && !sender.hasPermission("customitems.give." + item.getName())) {
                    sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to give this item to yourself.");
                    return;
                }

                Player receiver = null;
                int amount = 1;
                if (args.length == 2) {
                    if (sender instanceof Player) {
                        receiver = (Player) sender;
                    } else {
                        sender.sendMessage(lang.getCommandNoPlayerSpecified());
                    }
                }
                if (args.length >= 3) {
                    receiver = getOnlinePlayer(args[2]);
                    if (receiver == null) {
                        sender.sendMessage(lang.getCommandPlayerNotFound(args[2]));
                    }
                }
                if (args.length == 4) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChatColor.RED + "The amount (" + args[3] + ") should be an integer.");
                        return;
                    }
                }
                if (amount > item.getMaxStacksize()) {
                    sender.sendMessage(ChatColor.RED + "The amount can be at most " + item.getMaxStacksize());
                    return;
                }
                if (amount < 1) {
                    sender.sendMessage(ChatColor.RED + "The amount must be positive");
                    return;
                }
                if (receiver != null && !CustomItemsPlugin.getInstance().getEnabledAreas().isEnabled(receiver.getLocation())) {
                    receiver = null;
                    sender.sendMessage(lang.getCommandWorldDisabled());
                }
                if (receiver != null) {
                    if (receiver == sender || sender.hasPermission("customitems.give") || sender.hasPermission("customitems.giveother")) {
                        giveTheItem(sender, receiver, item, amount, enableOutput);
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to give custom items to other players");
                    }
                }
            } else {
                sender.sendMessage(lang.getCommandNoSuchItem(args[1]));
            }
        } else {
            sendGiveUseage(sender);
        }
    }

    private void giveTheItem(CommandSender sender, Player receiver, CustomItemValues item, int amount, boolean enableOutput) {
        boolean wasGiven = false;

        if (wrap(item).needsStackingHelp()) {
            ItemStack[] contents = receiver.getInventory().getStorageContents();
            int freeSlotIndex = -1;
            for (int index = 0; index < contents.length; index++) {
                if (ItemUtils.isEmpty(contents[index])) {
                    if (freeSlotIndex == -1) freeSlotIndex = index;
                } else {
                    ItemStack existingStack = contents[index];
                    CustomItemValues existingItem = itemSet.getItem(existingStack);
                    if (existingItem == item && item.getMaxStacksize() >= existingStack.getAmount() + amount) {
                        existingStack.setAmount(existingStack.getAmount() + amount);
                        receiver.getInventory().setStorageContents(contents);
                        wasGiven = true;
                        break;
                    }
                }
            }

            if (freeSlotIndex != -1 && !wasGiven) {
                contents[freeSlotIndex] = wrap(item).create(amount);
                receiver.getInventory().setStorageContents(contents);
                wasGiven = true;
            }
        } else {
            wasGiven = receiver.getInventory().addItem(wrap(item).create(amount)).isEmpty();
        }
        if (enableOutput) {
            if (wasGiven) sender.sendMessage(lang.getCommandItemGiven());
            else sender.sendMessage(ChatColor.RED + "No available inventory slot was found");
        }
    }
}
