package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customrecipes.collector.ResultCollectorEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

import static java.lang.Math.min;
import static nl.knokko.customitems.plugin.set.item.CustomItemWrapper.wrap;

public class CustomStackingResultCollector implements Consumer<ResultCollectorEvent> {

    private final JavaPlugin plugin;
    private final ItemSetWrapper itemSet;

    public CustomStackingResultCollector(JavaPlugin plugin, ItemSetWrapper itemSet) {
        this.plugin = plugin;
        this.itemSet = itemSet;
    }

    @Override
    public void accept(ResultCollectorEvent event) {
        KciItem customResult = itemSet.getItem(event.result);
        if (customResult == null || !wrap(customResult).needsStackingHelp()) return;
        if (event.action == InventoryAction.DROP_ONE_SLOT) return;

        /*
         * Normally, when you click on an item in the result slot while holding that item (e.g. crafting a torch
         * while you have another torch at your cursor), the stacksize of your cursor will be increased.
         *
         * Consider the case of a stackable custom item "sapphire" that is internally a diamond hoe. When a player
         * crafts a sapphire while holding a sapphire in his cursor, he would expect the stacksize of his cursor
         * to be increased. However, Spigot will generate an InventoryAction.NOTHING because it doesn't expect
         * sapphires (diamond hoes) to stack. To work around this problem, we need to fix this manually.
         */
        if (event.action == InventoryAction.NOTHING) {
            if (itemSet.getItem(event.oldCursor) == customResult && event.oldCursor.getAmount() + event.result.getAmount() <= customResult.getMaxStacksize()) {
                ItemStack newCursor = event.oldCursor.clone();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    newCursor.setAmount(newCursor.getAmount() + event.result.getAmount());
                    event.changeCursor.accept(newCursor);
                });
                event.actualProductionCount = 1;
                return;
            }
        }

        if (event.action == InventoryAction.PICKUP_ALL) {
            if (itemSet.getItem(event.oldCursor) == customResult &&
                    event.oldCursor.getAmount() + event.result.getAmount() <= customResult.getMaxStacksize()) {

                ItemStack newCursor = event.oldCursor.clone();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    newCursor.setAmount(newCursor.getAmount() + event.result.getAmount());
                    event.changeCursor.accept(newCursor);
                });
                event.actualProductionCount = 1;
                return;
            } else if (ItemUtils.isEmpty(event.oldCursor)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> event.changeCursor.accept(event.result));
                event.actualProductionCount = 1;
                return;
            }
        }

        if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack[] contents = event.destination.getStorageContents();

            int availableSpace = 0;
            for (ItemStack stack : contents) {
                if (ItemUtils.isEmpty(stack)) availableSpace += customResult.getMaxStacksize();
                else if (itemSet.getItem(stack) == customResult && stack.getAmount() < customResult.getMaxStacksize()) {
                    availableSpace += customResult.getMaxStacksize() - stack.getAmount();
                }
            }

            int productionCount = min(availableSpace / event.result.getAmount(), event.maximumProductionCount);
            int remaining = event.result.getAmount() * productionCount;

            for (ItemStack stack : contents) {
                if (remaining <= 0) break;

                if (stack != null && itemSet.getItem(stack) == customResult && stack.getAmount() < customResult.getMaxStacksize()) {
                    int extraAmount = min(remaining, customResult.getMaxStacksize() - stack.getAmount());
                    stack.setAmount(stack.getAmount() + extraAmount);
                    remaining -= extraAmount;
                }
            }

            for (int index = 0; index < contents.length; index++) {
                if (remaining <= 0) break;

                ItemStack stack = contents[index];
                if (ItemUtils.isEmpty(stack)) {
                    ItemStack newStack = event.result.clone();
                    newStack.setAmount(min(remaining, customResult.getMaxStacksize()));
                    contents[index] = newStack;
                    remaining -= newStack.getAmount();
                }
            }

            event.destination.setStorageContents(contents);
            event.actualProductionCount = productionCount;
            return;
        }

        event.actualProductionCount = 0;
    }
}
