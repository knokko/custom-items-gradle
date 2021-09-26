package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.SCustomContainer;

import java.util.Collection;

public class ContainerReference extends StringBasedReference<SCustomContainer, CustomContainerValues> {

    ContainerReference(String name, SItemSet itemSet) {
        super(name, itemSet);
    }

    ContainerReference(SCustomContainer model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "container";
    }

    @Override
    Collection<SCustomContainer> getCollection() {
        return itemSet.containers;
    }

    @Override
    String extractIdentity(CustomContainerValues values) {
        return values.getName();
    }
}
