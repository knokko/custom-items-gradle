package nl.knokko.customitems.itemset;

import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.SBlockDrop;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class BlockDropsView extends CollectionView<SBlockDrop, BlockDropValues, BlockDropReference> {
    public BlockDropsView(Collection<SBlockDrop> liveCollection) {
        super(liveCollection, BlockDropReference::new);
    }
}
