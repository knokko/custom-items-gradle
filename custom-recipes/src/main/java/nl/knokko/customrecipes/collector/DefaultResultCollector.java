package nl.knokko.customrecipes.collector;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

import static java.lang.Math.min;

public class DefaultResultCollector implements Consumer<ResultCollectorEvent> {

    @Override
    public void accept(ResultCollectorEvent event) {
        if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack[] contents = event.destination.getStorageContents();

            int availableSpace = 0;
            for (ItemStack stack : contents) {
                if (stack == null || stack.getType() == Material.AIR) availableSpace += event.result.getMaxStackSize();
                else if (stack.isSimilar(event.result) && stack.getAmount() < event.result.getType().getMaxStackSize()) {
                    availableSpace += event.result.getType().getMaxStackSize() - stack.getAmount();
                }
            }

            int productionCount = min(availableSpace / event.result.getAmount(), event.maximumProductionCount);
            int remaining = event.result.getAmount() * productionCount;
            for (ItemStack stack : contents) {
                if (remaining <= 0) break;

                if (stack != null && stack.isSimilar(event.result) && stack.getAmount() < event.result.getType().getMaxStackSize()) {
                    int extraAmount = min(remaining, event.result.getType().getMaxStackSize() - stack.getAmount());
                    stack.setAmount(stack.getAmount() + extraAmount);
                    remaining -= extraAmount;
                }
            }

            for (int index = 0; index < contents.length; index++) {
                if (remaining <= 0) break;

                ItemStack stack = contents[index];
                if (stack == null || stack.getType() == Material.AIR) {
                    ItemStack newStack = event.result.clone();
                    newStack.setAmount(min(remaining, event.result.getType().getMaxStackSize()));
                    contents[index] = newStack;
                    remaining -= newStack.getAmount();
                }
            }

            // The stupid PlayerInventoryMock class doesn't support setStorageContents
            if (!event.destination.getClass().getSimpleName().equals("PlayerInventoryMock")) {
                event.destination.setStorageContents(contents);
            }
            event.actualProductionCount = productionCount;
            return;
        }

        event.actualProductionCount = 0;
    }
}
