package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.SCustomItem;

import java.util.Collection;

public class ItemReference extends StringBasedReference<SCustomItem, CustomItemValues> {

    ItemReference(String name, SItemSet itemSet) {
        super(name, itemSet);
    }

    ItemReference(SCustomItem model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "item";
    }

    @Override
    Collection<SCustomItem> getCollection() {
        return itemSet.items;
    }

    @Override
    String extractIdentity(CustomItemValues values) {
        return values.getName();
    }
}
