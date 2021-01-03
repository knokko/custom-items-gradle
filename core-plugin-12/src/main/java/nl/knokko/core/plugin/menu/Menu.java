/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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