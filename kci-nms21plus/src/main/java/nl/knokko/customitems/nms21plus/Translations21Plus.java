package nl.knokko.customitems.nms21plus;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

class Translations21Plus {

	static ItemStack translate(ItemStack item, String itemName, boolean translateDisplayName, int loreSize) {
		if (!item.editMeta(meta -> {
			if (translateDisplayName) {
				meta.customName(net.kyori.adventure.text.Component.translatable("kci." + itemName + ".name"));
			}
			List<Component> loreComponents = new ArrayList<>(loreSize);
			for (int index = 0; index < loreSize; index++) {
				loreComponents.add(net.kyori.adventure.text.Component.translatable("kci." + itemName + ".lore." + index));
			}
			meta.lore(loreComponents);
		})) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to translate custom item " + itemName);
		}
		return item;
	}
}
