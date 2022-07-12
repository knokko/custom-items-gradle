package nl.knokko.customitems.plugin.command;

import nl.knokko.core.plugin.block.MushroomBlocks;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandCustomItemsSetBlock {

    final ItemSetWrapper itemSet;

    CommandCustomItemsSetBlock(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    void handle(String[] args, CommandSender sender) {
        if (!sender.hasPermission("customitems.setblock")) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command");
            return;
        }

        if (args.length < 2 || args.length > 6) {
            sender.sendMessage(ChatColor.RED + "You should use /kci setblock <block> [x] [y] [z] [world]");
            return;
        }

        if (!MushroomBlocks.areEnabled()) {
            sender.sendMessage(ChatColor.RED + "Custom blocks are not possible in this minecraft version");
            return;
        }

        Optional<CustomBlockValues> block = itemSet.get().getBlock(args[1]);

        if (!block.isPresent()) {
            sender.sendMessage(ChatColor.RED + "There is no custom block with name '" + args[1] + "'");
            return;
        }

        Location senderLocation;
        if (sender instanceof Player) {
            senderLocation = ((Player) sender).getLocation();
        } else if (sender instanceof CommandBlock) {
            senderLocation = ((CommandBlock) sender).getLocation();
        } else {
            senderLocation = null;
        }

        if (args.length < 6 && senderLocation == null) {
            sender.sendMessage("You should use /kci setblock <block> <x> <y> <z> <world>");
            return;
        }

        int x, y, z;
        World world;

        Integer parsedX = getCoordinate(
                senderLocation == null ? null : senderLocation.getBlockX(), args,
                2, "x", sender
        );
        if (parsedX != null) {
            x = parsedX;
        } else {
            return;
        }

        Integer parsedY = getCoordinate(
                senderLocation == null ? null : senderLocation.getBlockY(), args,
                3, "y", sender
        );
        if (parsedY != null) {
            y = parsedY;
        } else {
            return;
        }

        Integer parsedZ = getCoordinate(
                senderLocation == null ? null : senderLocation.getBlockZ(), args,
                4, "z", sender
        );
        if (parsedZ != null) {
            z = parsedZ;
        } else {
            return;
        }

        if (args.length >= 6) {
            world = Bukkit.getWorld(args[5]);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "There is no world with name '" + args[5] + "'");
                return;
            }
        } else {
            world = senderLocation.getWorld();
        }

        MushroomBlockHelper.place(world.getBlockAt(x, y, z), block.get());
    }

    private static Integer getCoordinate(
            Integer current, String[] arguments, int argIndex,
            String description, CommandSender sender
    ) {
        if (argIndex >= arguments.length) {
            return current;
        }

        String coordinateString = arguments[argIndex];
        int offset = 0;
        if (coordinateString.startsWith("~") && current != null) {
            offset = current;
            coordinateString = coordinateString.substring(1);
            if (coordinateString.isEmpty()) {
                return offset;
            }
        }

        try {
            return Integer.parseInt(coordinateString) + offset;
        } catch (NumberFormatException invalid) {
            sender.sendMessage(ChatColor.RED + "The <" + description + "> (" + coordinateString + ") is not an integer");
            return null;
        }
    }
}
