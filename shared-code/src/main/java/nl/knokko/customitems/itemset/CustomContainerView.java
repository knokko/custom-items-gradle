package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomContainerView extends CollectionView<CustomContainer, CustomContainerValues, ContainerReference> {
    public CustomContainerView(Collection<CustomContainer> liveCollection) {
        super(liveCollection, ContainerReference::new);
    }
}
