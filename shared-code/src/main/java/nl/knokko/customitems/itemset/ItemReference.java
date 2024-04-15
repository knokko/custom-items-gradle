package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomItem;

import java.util.Collection;

public class ItemReference extends StringBasedReference<CustomItem, CustomItemValues> {

    ItemReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ItemReference(CustomItem model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "item";
    }

    @Override
    Collection<CustomItem> getCollection() {
        return itemSet.items.elements;
    }

    @Override
    String extractIdentity(CustomItemValues values) {
        return values.getName();
    }
}
