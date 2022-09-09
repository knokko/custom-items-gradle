package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static java.lang.Integer.parseInt;
import static nl.knokko.customitems.plugin.command.CommandCustomItems.getOnlinePlayer;

public class CommandCustomItemsContainer {

    final ItemSetWrapper itemSet;

    CommandCustomItemsContainer(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You should use /kci container open/destroy");
    }

    void handle(String[] args, CommandSender sender, boolean enableOutput) {
        if (args.length > 1) {
            if (args[1].equals("open")) {
                if (args.length == 4 || args.length == 5) {

                    String containerName = args[2];
                    ContainerInfo container = itemSet.getContainerInfo(containerName);
                    if (container == null) {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + "Can't find a custom container with name " + containerName);
                        }
                        return;
                    }

                    String hostString = args[3];

                    Player player;
                    if (args.length == 5) {
                        String playerName = args[4];
                        player = getOnlinePlayer(playerName);
                        if (player == null) {
                            if (enableOutput) {
                                sender.sendMessage(ChatColor.RED + "Can't find player with name " + playerName);
                            }
                            return;
                        }
                    } else if (sender instanceof Player) {
                        player = (Player) sender;
                    } else {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + "You should use /kci container open <container name> <host string> <player name>");
                        }
                        return;
                    }

                    String[] requiredPermissions;
                    if (sender.equals(player)) {
                        requiredPermissions = new String[] {
                                "customitems.containercommand.openself.*.*",
                                "customitems.containercommand.openself." + containerName + ".*",
                                "customitems.containercommand.openself.*." + hostString,
                                "customitems.containercommand.openself." + containerName + "." + hostString
                        };
                    } else {
                        requiredPermissions = new String[] {
                                "customitems.containercommand.openother"
                        };
                    }

                    if (Arrays.stream(requiredPermissions).anyMatch(sender::hasPermission)) {
                        player.openInventory(
                                CustomItemsPlugin.getInstance().getData().getCustomContainer(
                                        null, hostString, player, container.getContainer()
                                ).getInventory()
                        );
                    } else {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this! You need at least 1 of the following permissions:");
                            for (String permission : requiredPermissions) {
                                sender.sendMessage(ChatColor.DARK_RED + permission);
                            }
                        }
                    }
                } else {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.RED + "You should use /kci container open <container name> <host string> [player name]");
                    }
                }
            } else if (args[1].equals("destroy")) {
                if (args.length != 4 && args.length != 8) {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.RED + "You should use /kci container destroy <container name> <host string> [drop world name] [drop x] [drop y] [drop z]");
                    }
                    return;
                }

                String containerName = args[2];
                ContainerInfo container = itemSet.getContainerInfo(containerName);
                if (container == null) {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.RED + "There is no container with name " + containerName);
                    }
                    return;
                }

                String hostString = args[3];
                Location dropLocation = null;

                if (args.length == 8) {
                    String worldName = args[4];
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + "There is no world with name " + worldName);
                        }
                        return;
                    }

                    try {
                        dropLocation = new Location(world, parseInt(args[5]), parseInt(args[6]), parseInt(args[7]));
                    } catch (NumberFormatException invalidCoordinates) {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + "x, y, and z should be integers, but are " + args[5] + ", " + args[6] + ", " + args[7]);
                        }
                        return;
                    }
                }

                String[] requiredPermissions = {
                        "customitems.containercommand.destroy.*.*",
                        "customitems.containercommand.destroy." + containerName + ".*",
                        "customitems.containercommand.destroy.*." + hostString,
                        "customitems.containercommand.destroy." + containerName + "." + hostString
                };

                if (Arrays.stream(requiredPermissions).anyMatch(sender::hasPermission)) {
                    int numDestroyedInstances = CustomItemsPlugin.getInstance().getData().destroyCustomContainer(
                            container.getContainer(), hostString, dropLocation
                    );
                    if (enableOutput) {
                        if (numDestroyedInstances > 0) {
                            sender.sendMessage(ChatColor.GREEN + "Destroyed " + numDestroyedInstances + " custom container instances");
                        } else {
                            sender.sendMessage(ChatColor.RED + "No container was stored at the given host string");
                        }
                    }
                } else {
                    if (enableOutput) {
                        sender.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this! You need at least 1 of the following permissions:");
                        for (String permission : requiredPermissions) {
                            sender.sendMessage(ChatColor.DARK_RED + permission);
                        }
                    }
                }
            } else {
                if (enableOutput) {
                    sendUsage(sender);
                }
            }
        } else {
            if (enableOutput) {
                sendUsage(sender);
            }
        }
    }
}
