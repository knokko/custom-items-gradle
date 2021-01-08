package nl.knokko.core.plugin.item;

import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class SmithingBlocker {

    public static void blockSmithingTableUpgrades(Predicate<ItemStack> shouldBeBlocked) {
        // There is no smithing table in this minecraft version, so this method doesn't need to do anything
    }
}
