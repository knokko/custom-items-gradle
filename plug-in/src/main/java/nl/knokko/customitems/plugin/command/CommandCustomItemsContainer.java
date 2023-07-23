package nl.knokko.customitems.plugin.command;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.data.PluginData;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.CommandHelper;
import nl.knokko.customitems.plugin.util.ContainerHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
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
        args = CommandHelper.escapeArgs(args);
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
                } else if (args.length >= 6 && args.length <= 9) {
                    String containerName = args[2];

                    ContainerInfo container = itemSet.getContainerInfo(containerName);
                    if (container == null) {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + "There is no container with name " + containerName);
                        }
                        return;
                    }

                    int x, y, z;
                    try {
                        x = parseInt(args[3]);
                        y = parseInt(args[4]);
                        z = parseInt(args[5]);
                    } catch (NumberFormatException invalidCoordinates) {
                        if (enableOutput) {
                            sender.sendMessage(ChatColor.RED + String.format(
                                    "x, y, and z (%s, %s, and %s) must be integers", args[3], args[4], args[5]
                            ));
                        }
                        return;
                    }

                    boolean force = false;
                    if (args.length >= 7 && args[6].equals("force")) {
                        if (!sender.hasPermission("customitems.containercommand.force")) {
                            sender.sendMessage(ChatColor.DARK_RED + "You are not allowed to force this command");
                            return;
                        }
                        force = true;
                        if (args.length >= 8) args[6] = args[7];
                        if (args.length >= 9) args[7] = args[8];
                        args = Arrays.copyOf(args, args.length - 1);
                    }

                    Player player;
                    if (args.length >= 7) {
                        player = getOnlinePlayer(args[6]);
                        if (player == null) {
                            if (enableOutput) sender.sendMessage(ChatColor.RED + "Can't find online player " + args[6]);
                            return;
                        }
                    } else if (sender instanceof Player){
                        player = (Player) sender;
                    } else {
                        if (enableOutput) sender.sendMessage(ChatColor.RED + "You need to specify the player name");
                        return;
                    }

                    World world;
                    if (args.length >= 8) {
                        String worldName = args[7];
                        world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            if (enableOutput) sender.sendMessage(ChatColor.RED + "Can't find world with name " + worldName);
                            return;
                        }
                    } else world = player.getWorld();

                    String[] requiredPermissions;
                    if (sender.equals(player)) {
                        requiredPermissions = new String[] {
                                "customitems.containercommand.openself.*",
                                "customitems.containercommand.openself." + containerName
                        };
                    } else {
                        requiredPermissions = new String[] {
                                "customitems.containercommand.openother"
                        };
                    }

                    if (Arrays.stream(requiredPermissions).anyMatch(sender::hasPermission)) {
                        PluginData data = CustomItemsPlugin.getInstance().getData();
                        Location location = new Location(world, x, y, z);
                        if (!force) {
                            Block block = location.getBlock();
                            if (!ContainerHelper.shouldHostAcceptBlock(
                                    containerName, container.getContainer().getHost(), block
                            )) {
                                if (enableOutput) sender.sendMessage(ChatColor.RED + "Block " + block + " can't host container " + containerName);
                                return;
                            }
                        }
                        ContainerInstance containerInstance = data.getCustomContainer(
                                location, null, player, container.getContainer()
                        );
                        player.openInventory(containerInstance.getInventory());
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
                        sender.sendMessage(ChatColor.RED + "or /kci container open '<container name>' <x> <y> <z> [force] [player name] [world name]");
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
