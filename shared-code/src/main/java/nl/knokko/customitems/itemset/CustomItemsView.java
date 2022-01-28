package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomItemsView extends CollectionView<CustomItem, CustomItemValues, ItemReference> {

    public CustomItemsView(Collection<CustomItem> liveCollection) {
        super(liveCollection, ItemReference::new);
    }
}
