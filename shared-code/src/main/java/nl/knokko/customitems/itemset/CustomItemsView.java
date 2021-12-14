package nl.knokko.customitems.itemset;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.SCustomItem;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomItemsView extends CollectionView<SCustomItem, CustomItemValues, ItemReference> {

    public CustomItemsView(Collection<SCustomItem> liveCollection) {
        super(liveCollection, ItemReference::new);
    }
}
