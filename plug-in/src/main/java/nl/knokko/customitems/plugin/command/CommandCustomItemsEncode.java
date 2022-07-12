package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

class CommandCustomItemsEncode {

    void handle(CommandSender sender) {
        if (!sender.hasPermission("customitems.encode")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command.");
            return;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            if (!ItemUtils.isEmpty(mainItem)) {
                YamlConfiguration helperConfig = new YamlConfiguration();
                helperConfig.set("TheItemStack", mainItem);
                String serialized = helperConfig.saveToString();

                // Encode the string to avoid indentation errors when copying
                Bukkit.getLogger().log(Level.INFO, "Encoded: " + StringEncoder.encode(serialized));
                sender.sendMessage(ChatColor.GREEN + "The encoding of the item in your main hand has been printed to the server console");
            } else {
                sender.sendMessage(ChatColor.RED + "You need to hold an item in your main hand when executing this command");
            }
        } else {
            sender.sendMessage("Only players can use this command");
        }
    }
}
