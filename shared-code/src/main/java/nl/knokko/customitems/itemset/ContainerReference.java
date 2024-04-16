package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.CustomContainerValues;

import java.util.Collection;

public class ContainerReference extends StringBasedReference<CustomContainerValues> {

    ContainerReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ContainerReference(Model<CustomContainerValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "container";
    }

    @Override
    Collection<Model<CustomContainerValues>> getCollection() {
        return itemSet.containers.elements;
    }

    @Override
    String extractIdentity(CustomContainerValues values) {
        return values.getName();
    }
}
