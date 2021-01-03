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