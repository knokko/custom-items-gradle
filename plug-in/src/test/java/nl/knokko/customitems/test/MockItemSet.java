package nl.knokko.customitems.test;

import nl.knokko.customitems.plugin.set.ItemSet;
import nl.knokko.customitems.plugin.set.item.CustomItem;

public class MockItemSet extends ItemSet {
	
	private final CustomItem[] items;

	public MockItemSet(CustomItem...items) {
		this.items = items;
	}

	public CustomItem getItem(String name) {
		for (CustomItem item : items)
			if (item.getName().equals(name))
				return item;
		return null;
	}
}
