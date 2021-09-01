package nl.knokko.customitems.item;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomItemsView extends CollectionView<SCustomItem, CustomItemValues> {

    public CustomItemsView(Collection<SCustomItem> liveCollection) {
        super(liveCollection);
    }
}
