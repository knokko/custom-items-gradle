package nl.knokko.customitems.container;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomContainerView extends CollectionView<SCustomContainer, CustomContainerValues> {
    public CustomContainerView(Collection<SCustomContainer> liveCollection) {
        super(liveCollection);
    }
}
