package nl.knokko.customitems.itemset;

import nl.knokko.customitems.drops.MobDrop;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class MobDropsView extends CollectionView<MobDrop, MobDropValues, MobDropReference> {
    public MobDropsView(Collection<MobDrop> liveCollection) {
        super(liveCollection, MobDropReference::new);
    }
}
