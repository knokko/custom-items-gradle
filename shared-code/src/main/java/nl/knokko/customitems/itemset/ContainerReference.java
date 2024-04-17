package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.KciContainer;

import java.util.Collection;

public class ContainerReference extends StringBasedReference<KciContainer> {

    ContainerReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ContainerReference(Model<KciContainer> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "container";
    }

    @Override
    Collection<Model<KciContainer>> getCollection() {
        return itemSet.containers.elements;
    }

    @Override
    String extractIdentity(KciContainer values) {
        return values.getName();
    }
}
