package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.CustomItemValues;

import java.util.Collection;

public class ItemReference extends StringBasedReference<CustomItemValues> {

    ItemReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ItemReference(Model<CustomItemValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "item";
    }

    @Override
    Collection<Model<CustomItemValues>> getCollection() {
        return itemSet.items.elements;
    }

    @Override
    String extractIdentity(CustomItemValues values) {
        return values.getName();
    }
}
