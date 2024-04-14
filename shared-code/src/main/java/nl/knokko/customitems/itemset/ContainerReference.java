package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.CustomContainer;

import java.util.Collection;

public class ContainerReference extends StringBasedReference<CustomContainer, CustomContainerValues> {

    ContainerReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ContainerReference(CustomContainer model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "container";
    }

    @Override
    Collection<CustomContainer> getCollection() {
        return itemSet.containers.elements;
    }

    @Override
    String extractIdentity(CustomContainerValues values) {
        return values.getName();
    }
}
