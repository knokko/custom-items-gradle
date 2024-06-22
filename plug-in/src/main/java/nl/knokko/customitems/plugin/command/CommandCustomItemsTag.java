package nl.knokko.customitems.plugin.command;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandCustomItemsTag {

    public void handle(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!ItemUtils.isEmpty(item)) {
                sender.sendMessage(NBT.readNbt(item).toString());
            } else {
                sender.sendMessage("Hold the item to check in your main hand");
            }
        } else {
            sender.sendMessage("Only players can use this command");
        }
    }
}
