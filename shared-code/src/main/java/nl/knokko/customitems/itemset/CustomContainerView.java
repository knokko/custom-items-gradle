package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.SCustomContainer;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomContainerView extends CollectionView<SCustomContainer, CustomContainerValues, ContainerReference> {
    public CustomContainerView(Collection<SCustomContainer> liveCollection) {
        super(liveCollection, ContainerReference::new);
    }
}
