package nl.knokko.customitems.plugin.command;

import com.google.common.collect.Lists;
import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CustomItemsTabCompletions implements TabCompleter {

    private static List<String> filter(List<String> full, String prefix) {
        return full.stream().filter(element -> element.startsWith(prefix)).collect(Collectors.toList());
    }

    private static List<String> getRootCompletions() {
        return Lists.newArrayList("give", "list", "debug", "encode", "reload");
    }

    private final Supplier<ItemSet> getSet;

    public CustomItemsTabCompletions(Supplier<ItemSet> getSet) {
        this.getSet = getSet;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender.isOp()) {
            ItemSet set = getSet.get();
            if (args.length == 0) {
                return getRootCompletions();
            } else if (args.length == 1) {
                String prefix = args[0];
                return filter(getRootCompletions(), prefix);
            } else if (args.length == 2) {
                String first = args[0];
                String prefix = args[1];
                if (first.equals("give")) {
                    List<String> result = new ArrayList<>(set.getNumItems());
                    for (CustomItem item : set.getBackingItems()) {
                        result.add(item.getName());
                        if (!item.getAlias().isEmpty()) {
                            result.add(item.getAlias());
                        }
                    }
                    return filter(result, prefix);
                }
            }
            return Collections.emptyList();
        }
        return null;
    }
}
