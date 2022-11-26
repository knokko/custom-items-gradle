package nl.knokko.core.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Menu {
	
	private final String title;
	
	private final MenuItem[] contents;
	
	private Inventory inventory;
	
	public Menu(String title, int size) {
		this.title = title;
		int remainder = size % 9;
		if (remainder != 0)
			size += 9 - remainder;
		contents = new MenuItem[size];
	}
	
	public void setItem(int slot, MenuItem item) {
		contents[slot] = item;
	}
	
	public void setItem(int slot, ItemStack stack, MenuAction action) {
		contents[slot] = new MenuItem(stack, action);
	}
	
	public void setItem(int slot, Material item, MenuAction action) {
		contents[slot] = new MenuItem(item, action);
	}
	
	public void setItem(int slot, Material item, String name, MenuAction action) {
		contents[slot] = new MenuItem(item, name, action);
	}
	
	public void setItem(int slot, Material item, String name, MenuAction action, String...lore) {
		contents[slot] = new MenuItem(item, name, action, lore);
	}
	
	public void onClick(Player player, int slot) {
		if (contents[slot] != null)
			contents[slot].getAction().execute(player);
	}
	
	public int getSize() {
		return contents.length;
	}
	
	public Inventory createInventory() {
		Inventory inv = Bukkit.createInventory(null, contents.length, title);
		for (int slot = 0; slot < contents.length; slot++) {
			if (contents[slot] != null) {
				inv.setItem(slot, contents[slot].getItem());
			}
		}
		return inv;
	}
	
	public Inventory getInventory() {
		if (inventory == null)
			inventory = createInventory();
		return inventory;
	}
}