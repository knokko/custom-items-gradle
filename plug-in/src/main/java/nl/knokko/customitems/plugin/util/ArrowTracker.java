package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.KciArrow;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArrowTracker {

    public static int[] countArrows(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        int[] arrows = new int[contents.length];

        for (int index = 0; index < contents.length; index++) {
            if (contents[index] != null) {
                Material itemType = contents[index].getType();
                if (itemType == Material.ARROW || itemType == Material.TIPPED_ARROW || itemType == Material.SPECTRAL_ARROW) {
                    arrows[index] = contents[index].getAmount();
                }
            }
        }

        return arrows;
    }

    public static KciArrow[] getArrowTypes(Inventory inventory, ItemSetWrapper itemSet) {
        ItemStack[] contents = inventory.getContents();
        KciArrow[] arrows = new KciArrow[contents.length];

        for (int index = 0; index < contents.length; index++) {
            KciItem customItem = itemSet.getItem(contents[index]);
            if (customItem instanceof KciArrow) arrows[index] = (KciArrow) customItem;
        }

        return arrows;
    }

    /**
     * @return The inventory index of the arrow that was fired, or -1 if it could not be determined with reasonably
     * certainty
     */
    private static int determineUsedArrowIndexByCounts(int[] oldArrowCounts, int[] newArrowCounts) {
        if (oldArrowCounts.length != newArrowCounts.length) return -1;

        int arrowIndex = -1;
        for (int index = 0; index < oldArrowCounts.length; index++) {
            if (newArrowCounts[index] < oldArrowCounts[index]) {
                if (arrowIndex == -1) arrowIndex = index;

                    // If multiple arrow stacksizes were decremented, we don't know which one is the right one
                else return -1;
            }
        }

        return arrowIndex;
    }

    public static KciArrow determineUsedArrowType(
            int[] oldArrowCounts, KciArrow[] oldArrowTypes, PlayerInventory newInventory
    ) {
        int[] newArrowCounts = countArrows(newInventory);
        int arrowIndex = determineUsedArrowIndexByCounts(oldArrowCounts, newArrowCounts);

        if (arrowIndex != -1) {
            return oldArrowTypes[arrowIndex];
        }

        // If we can't find any decremented arrow stacksizes (or multiple), we guess which arrow was fired
        // When the inventory contains multiple arrow stacks, the game seems to use this priority scheme:
        // 1. Mainhand and offhand (no conflict is possible since at least 1 of them must contain a bow)
        // 2. The arrows in the hotbar, starting from the left
        // 3. The rest of the inventory, starting from the top-left

        // Start with mainhand and offhand (40) is the offhand slot
        if (oldArrowCounts[newInventory.getHeldItemSlot()] > 0) return oldArrowTypes[newInventory.getHeldItemSlot()];
        if (oldArrowCounts[40] > 0) return oldArrowTypes[40];

        // Indices 0 to 8 are mapped to the hotbar and indices 9 to 35 are mapped to the rest of the inventory,
        // so just iterating in order should respect the priority of (2) and (3)
        for (int index = 0; index < oldArrowCounts.length; index++) {
            if (oldArrowCounts[index] > 0) return oldArrowTypes[index];
        }

        return null;
    }
}
