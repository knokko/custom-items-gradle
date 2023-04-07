package nl.knokko.customitems.itemset;

import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.misc.CombinedResourcepackValues;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CombinedResourcepacksView extends CollectionView<
        CombinedResourcepack, CombinedResourcepackValues, CombinedResourcepackReference
> {
    public CombinedResourcepacksView(Collection<CombinedResourcepack> liveCollection) {
        super(liveCollection, CombinedResourcepackReference::new);
    }
}
