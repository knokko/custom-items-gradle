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
package nl.knokko.customitems.plugin.multisupport.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.HellForgedUseEvent;
import nl.knokko.customitems.plugin.CustomItemsPlugin;

import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;

public class CrazyEnchantmentsEventHandler implements Listener {

	private static CEnchantment fromName(String enchantmentName) {
		return CEnchantment.getCEnchantmentFromName(enchantmentName);
	}

	public CrazyEnchantmentsEventHandler() {
		CrazyEnchantmentsSupport.crazyEnchantmentsFunctions = new CrazyEnchantmentsFunctions() {

			@Override
			public int getLevel(ItemStack itemStack, String enchantmentName) {
				return CrazyEnchantments.getInstance().getLevel(itemStack, fromName(enchantmentName));
			}

			@Override
			public void add(ItemStack itemStack, String enchantmentName, int level) {
				CrazyEnchantments.getInstance().addEnchantment(itemStack, fromName(enchantmentName), level);
			}

			@Override
			public void remove(ItemStack itemStack, String enchantmentName) {
				CrazyEnchantments.getInstance().removeEnchantment(itemStack, fromName(enchantmentName));
			}
		};
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHellForge(HellForgedUseEvent event) {
		ItemStack item = event.getItem();
		CustomItemValues custom = CustomItemsPlugin.getInstance().getSet().getItem(item);
		if (custom != null) {
			event.setCancelled(true);
			if (custom instanceof CustomToolValues) {
				CustomToolValues tool = (CustomToolValues) custom;
				wrap(tool).increaseDurability(item, CEnchantments.HELLFORGED.getEnchantment().getPower(item));
			}
		}
	}
}