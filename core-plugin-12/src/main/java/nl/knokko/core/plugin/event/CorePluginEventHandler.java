package nl.knokko.core.plugin.event;

import java.util.function.BiPredicate;

import org.bukkit.inventory.ItemStack;

public class CorePluginEventHandler {

	public static void preventSmithing(BiPredicate<ItemStack, ItemStack> preventIf) {
		// There is no smithing table in this minecraft version
		// So there is nothing to prevent
	}
}
