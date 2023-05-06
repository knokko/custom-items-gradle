package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.itemset.SoundTypeReference;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.SoundPlayer;
import nl.knokko.customitems.sound.SoundValues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

public class CommandCustomItemsSound {

    final ItemSetWrapper itemSet;

    CommandCustomItemsSound(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    private void sendUsage(CommandSender sender, boolean enableOutput) {
        if (enableOutput) {
            if (sender instanceof Player || sender instanceof CommandBlock) {
                sender.sendMessage(ChatColor.RED + "You should use /kci playsound <custom_sound> [x] [y] [z] [world] [volume] [pitch]");
            } else {
                sender.sendMessage(ChatColor.RED + "You should use /kci playsound <custom_sound> <x> <y> <z> <world> [volume] [pitch]");
            }
        }
    }

    void handle(String[] args, CommandSender sender, boolean enableOutput) {
        if (!sender.hasPermission("customitems.playsound")) {
            if (enableOutput) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have access to this command");
            }
            return;
        }

        if (args.length < 2 || args.length > 8) {
            sendUsage(sender, enableOutput);
            return;
        }
        if (args.length < 6 && !(sender instanceof Player || sender instanceof CommandBlock)) {
            sendUsage(sender, enableOutput);
            return;
        }

        String soundName = args[1];
        SoundTypeReference sound = null;
        for (SoundTypeReference candidate : itemSet.get().getSoundTypes().references()) {
            if (candidate.get().getName().equals(soundName)) {
                sound = candidate;
                break;
            }
        }

        if (sound == null) {
            if (enableOutput) sender.sendMessage(ChatColor.RED + "Can't find sound " + soundName);
            return;
        }

        Location senderLocation = null;
        if (sender instanceof Player) senderLocation = ((Player) sender).getLocation();
        else if (sender instanceof CommandBlock) senderLocation = ((CommandBlock) sender).getLocation();

        double x;
        if (args.length >= 3) {
            try {
                x = parseDouble(args[2]);
            } catch (NumberFormatException invalidX) {
                if (enableOutput) sender.sendMessage(ChatColor.RED + "Invalid x: " + args[2]);
                return;
            }
        } else {
            x = senderLocation.getX();
        }

        double y;
        if (args.length >= 4) {
            try {
                y = parseDouble(args[3]);
            } catch (NumberFormatException invalidY) {
                if (enableOutput) sender.sendMessage(ChatColor.RED + "Invalid y: " + args[3]);
                return;
            }
        } else {
            y = senderLocation.getY();
        }

        double z;
        if (args.length >= 5) {
            try {
                z = parseDouble(args[4]);
            } catch (NumberFormatException invalidZ) {
                if (enableOutput) sender.sendMessage(ChatColor.RED + "Invalid z: " + args[4]);
                return;
            }
        } else {
            z = senderLocation.getZ();
        }

        World world;
        if (args.length >= 6) {
            world = Bukkit.getWorld(args[5]);
            if (world == null) {
                if (enableOutput) sender.sendMessage(ChatColor.RED + "Can't find world: " + args[5]);
                return;
            }
        } else {
            world = senderLocation.getWorld();
        }

        float volume = 1f;
        if (args.length >= 7) {
            try {
                volume = parseFloat(args[6]);
            } catch (NumberFormatException invalidVolume) {
                if (enableOutput) sender.sendMessage(ChatColor.RED + "Invalid volume: " + args[6]);
                return;
            }
        }

        float pitch = 1f;
        if (args.length >= 8) {
            try {
                pitch = parseFloat(args[7]);
            } catch (NumberFormatException invalidPitch) {
                if (enableOutput) sender.sendMessage(ChatColor.RED + "Invalid pitch: " + args[7]);
                return;
            }
        }

        SoundPlayer.playSound(new Location(world, x, y, z), SoundValues.createQuick(sound, volume, pitch));
        if (enableOutput) sender.sendMessage(ChatColor.GREEN + "Playing sound...");
    }
}
