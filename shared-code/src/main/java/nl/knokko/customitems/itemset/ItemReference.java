package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.KciItem;

import java.util.Collection;

public class ItemReference extends StringBasedReference<KciItem> {

    ItemReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ItemReference(Model<KciItem> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "item";
    }

    @Override
    Collection<Model<KciItem>> getCollection() {
        return itemSet.items.elements;
    }

    @Override
    String extractIdentity(KciItem values) {
        return values.getName();
    }
}
