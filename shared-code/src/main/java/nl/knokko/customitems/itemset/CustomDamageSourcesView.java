package nl.knokko.customitems.itemset;

import nl.knokko.customitems.damage.CustomDamageSource;
import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomDamageSourcesView extends CollectionView<CustomDamageSource, CustomDamageSourceValues, CustomDamageSourceReference> {
    public CustomDamageSourcesView(Collection<CustomDamageSource> liveCollection) {
        super(liveCollection, CustomDamageSourceReference::new);
    }
}
