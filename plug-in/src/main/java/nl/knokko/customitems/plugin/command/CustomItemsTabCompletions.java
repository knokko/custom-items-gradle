package nl.knokko.customitems.plugin.command;

import com.google.common.collect.Lists;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.CommandHelper;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomItemsTabCompletions implements TabCompleter {

    private static List<String> filter(List<String> full, String prefix) {
        return full.stream().filter(element -> element.startsWith(prefix)).collect(Collectors.toList());
    }

    private List<String> getRootCompletions(CommandSender sender, boolean showDisableOutput) {
        List<String> result = Lists.newArrayList(
                "give", "take", "list", "debug", "encode", "reload", "repair",
                "damage", "setblock", "container", "playsound", "tag", "resourcepack"
        ).stream().filter(
                element -> element.equals("container") || element.equals("resourcepack")
                        || sender.hasPermission("customitems." + element) ||
                        (element.equals("give") && itemSet.get().getItems().stream().anyMatch(
                                item -> sender.hasPermission("customitems.give." + item.getName())
                        ))
        ).collect(Collectors.toList());
        if (showDisableOutput) result.add("disableoutput");
        return result;
    }

    private final ItemSetWrapper itemSet;

    public CustomItemsTabCompletions(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        boolean showDisableOutput = true;
        if (args.length > 0 && args[0].equals("disableoutput")) {
            args = Arrays.copyOfRange(args, 1, args.length);
            showDisableOutput = false;
        }
        if (args.length > 0 && args[0].equals("container")) args = CommandHelper.escapeArgs(args);
        if (args.length == 0) {
            return getRootCompletions(sender, showDisableOutput);
        } else if (args.length == 1) {
            String prefix = args[0];
            return filter(getRootCompletions(sender, showDisableOutput), prefix);
        } else if (args.length == 2) {
            String first = args[0];
            String prefix = args[1];

            if (first.equals("give")) {
                List<String> result = new ArrayList<>(itemSet.get().getItems().size());
                for (CustomItemValues item : itemSet.get().getItems()) {
                    if (sender.hasPermission("customitems.give") || sender.hasPermission("customitems.give." + item.getName())) {
                        result.add(item.getName());
                        if (!item.getAlias().isEmpty()) {
                            result.add(item.getAlias());
                        }
                    }
                }
                return filter(result, prefix);
            }

            if (first.equals("take") && sender.hasPermission("customitems.take")) {
                return Lists.newArrayList("1", "2", "3", "4");
            }

            if (
                    (first.equals("repair") && sender.hasPermission("customitems.repair"))
                            || (first.equals("damage") && sender.hasPermission("customitems.damage"))
            ) {
                return Lists.newArrayList("1", "2", "3", "4");
            }

            if (first.equals("setblock") && sender.hasPermission("customitems.setblock")) {
                List<String> result = new ArrayList<>(itemSet.get().getBlocks().size());
                for (CustomBlockValues block : itemSet.get().getBlocks()) {
                    result.add(block.getName());
                }
                return filter(result, prefix);
            }

            if (first.equals("container")) {
                return Lists.newArrayList("open", "destroy");
            }

            if (first.equals("playsound")) {
                List<String> result = new ArrayList<>(itemSet.get().getSoundTypes().size());
                for (CustomSoundTypeValues sound : itemSet.get().getSoundTypes()) {
                    result.add(sound.getName());
                }
                return filter(result, prefix);
            }

            if (first.equals("resourcepack") && sender.hasPermission("customitems.resourcepack.otherplayers")) {
                List<String> names = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                return filter(names, prefix);
            }
        } else if (args.length == 3) {
            String first = args[0];
            String prefix = args[2];
            if (
                    first.equals("take") && sender.hasPermission("customitems.take")
                            || (first.equals("damage") && sender.hasPermission("customitems.damage"))
                            || (first.equals("repair") && sender.hasPermission("customitems.repair"))
            ) {
                List<String> names = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                return filter(names, prefix);
            }

            if (first.equals("give")) {
                if (sender.hasPermission("customitems.give") || (sender.hasPermission("customitems.giveother") && sender.hasPermission("customitems.give." + args[1]))) {
                    List<String> names = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                    return filter(names, prefix);
                } else if (sender.hasPermission("customitems.give." + args[1])) {
                    return Lists.newArrayList(sender.getName());
                }
            }

            if (first.equals("setblock") && sender.hasPermission("customitems.setblock")) {
                return Lists.newArrayList("~");
            }

            if (first.equals("container")) {
                String second = args[1];
                if (second.equals("open") || second.equals("destroy")) {
                    return filter(itemSet.get().getContainers().stream().map(container -> {
                        if (container.getName().contains(" ")) return "'" + container.getName() + "'";
                        else return container.getName();
                    }).collect(Collectors.toList()), prefix);
                }
            }
        } else if (args.length == 4) {
            String first = args[0];
            if (first.equals("give") && sender.hasPermission("customitems.give")) {
                String itemName = args[1];
                CustomItemValues item = itemSet.getItem(itemName);
                if (item != null) {
                    if (item.canStack()) {
                        return Lists.newArrayList("1", item.getMaxStacksize() + "");
                    } else {
                        return Lists.newArrayList("1");
                    }
                }
            }

            if (first.equals("setblock") && sender.hasPermission("customitems.setblock")) {
                return Lists.newArrayList("~");
            }

            if (first.equals("container")) return Lists.newArrayList("1", "2", "3", "whatever");

            // There are no tab completions for containers here because the host string can be any string
        } else if (args.length == 5) {

            String first = args[0];
            if (first.equals("setblock") && sender.hasPermission("customitems.setblock")) {
                return Lists.newArrayList("~");
            }

            if (first.equals("container")) {
                if (args[1].equals("open")) {
                    try {
                        Integer.parseInt(args[3]);
                        return Lists.newArrayList("1", "2", "3");
                    } catch (NumberFormatException noIntegerCoordinate) {
                        return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[4]);
                    }
                } else if (args[1].equals("destroy")) {
                    return filter(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()), args[4]);
                }
            }
        } else if (args.length == 6) {

            String first = args[0];
            String prefix = args[5];

            if ((first.equals("setblock") && sender.hasPermission("customitems.setblock")) || (
                    first.equals("playsound") && sender.hasPermission("customitems.playsound")
                    )) {
                List<String> result = new ArrayList<>(Bukkit.getWorlds().size());
                for (World world : Bukkit.getWorlds()) {
                    result.add(world.getName());
                }
                return filter(result, prefix);
            }

            if (first.equals("container") && args[1].equals("open")) {
                return Lists.newArrayList("1", "2", "3", "4");
            }
        } else if (args.length == 7) {
            if (args[0].equals("container") && args[1].equals("open")) {
                List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                List<String> options = new ArrayList<>(playerNames.size() + 1);
                options.add("force");
                options.addAll(playerNames);
                return filter(options, args[6]);
            }
        } else if (args.length == 8) {
            if (args[0].equals("container") && args[1].equals("open")) {
                if (args[6].equals("force")) {
                    return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[7]);
                } else {
                    List<String> result = new ArrayList<>(Bukkit.getWorlds().size());
                    for (World world : Bukkit.getWorlds()) {
                        result.add(world.getName());
                    }
                    return filter(result, args[7]);
                }
            }
        } else if (args.length == 9) {
            if (args[0].equals("container") && args[1].equals("open") && args[6].equals("force")) {
                List<String> result = new ArrayList<>(Bukkit.getWorlds().size());
                for (World world : Bukkit.getWorlds()) {
                    result.add(world.getName());
                }
                return filter(result, args[8]);
            }
        }

        if (args.length >= 6 && args.length < 9 && args[0].equals("container") && args[1].equals("destroy")) {
            return Lists.newArrayList("1", "2", "3", "4");
        }
        return Collections.emptyList();
    }
}
