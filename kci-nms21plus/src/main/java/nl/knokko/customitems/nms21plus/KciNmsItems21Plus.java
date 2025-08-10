package nl.knokko.customitems.nms21plus;

import net.kyori.adventure.text.Component;
import nl.knokko.customitems.nms18plus.KciNmsItems18Plus;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class KciNmsItems21Plus extends KciNmsItems18Plus {

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void setEquippableAssetID(ItemMeta meta, EquipmentSlot slot, String id) {
		EquippableComponent component = meta.getEquippable();
		component.setModel(new NamespacedKey("minecraft", id));
		component.setSlot(slot);
		meta.setEquippable(component);
	}

	@Override
	public ItemStack translate(ItemStack item, String itemName, boolean translateDisplayName, int loreSize) {
		if (!item.editMeta(meta -> {
			if (translateDisplayName) {
				meta.customName(Component.translatable("kci." + itemName + ".name"));
			}
			List<Component> loreComponents = new ArrayList<>(loreSize);
			for (int index = 0; index < loreSize; index++) {
				loreComponents.add(Component.translatable("kci." + itemName + ".lore." + index));
			}
			meta.lore(loreComponents);
		})) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to translate custom item " + itemName);
		}
		return item;
	}
}
