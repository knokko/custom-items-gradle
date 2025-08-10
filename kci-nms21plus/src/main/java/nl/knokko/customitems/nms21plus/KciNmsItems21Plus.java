package nl.knokko.customitems.nms21plus;

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

	private static final boolean HAS_PAPER;

	static {
		boolean hasPaper;
		try {
			Class.forName("net.kyori.adventure.text.Component");
			hasPaper = true;
		} catch (ClassNotFoundException noPaper) {
			hasPaper = false;
			Bukkit.getLogger().warning("CustomItems translations in MC 1.20+ requires papermc");
		}
		HAS_PAPER = hasPaper;
	}

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
		if (!HAS_PAPER) return item;
		if (!item.editMeta(meta -> {
			if (translateDisplayName) {
				meta.customName(net.kyori.adventure.text.Component.translatable("kci." + itemName + ".name"));
			}
			List<net.kyori.adventure.text.Component> loreComponents = new ArrayList<>(loreSize);
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
