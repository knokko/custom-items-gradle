package nl.knokko.core.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

public class MenuItem {
	
	private static final MenuAction DO_NOTHING = (Player player) -> {};
	
	private static ItemStack create(Material type, String name) {
		ItemStack item = new ItemStack(type);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(type);
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	private static ItemStack create(Material type, String name, String... lore) {
		ItemStack item = new ItemStack(type);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(type);
		meta.setDisplayName(name);
		meta.setLore(Lists.newArrayList(lore));
		item.setItemMeta(meta);
		return item;
	}
	
	private final ItemStack item;
	private final MenuAction action;
	
	public MenuItem(ItemStack item, MenuAction action) {
		this.item = item;
		this.action = action != null ? action : DO_NOTHING;
	}
	
	public MenuItem(Material item, MenuAction action) {
		this(new ItemStack(item), action);
	}
	
	public MenuItem(Material item, String name, MenuAction action) {
		this(create(item, name), action);
	}
	
	public MenuItem(Material item, String name, MenuAction action, String...lore) {
		this(create(item, name, lore), action);
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public MenuAction getAction() {
		return action;
	}
}