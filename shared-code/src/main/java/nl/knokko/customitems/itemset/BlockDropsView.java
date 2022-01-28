package nl.knokko.customitems.itemset;

import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.BlockDrop;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class BlockDropsView extends CollectionView<BlockDrop, BlockDropValues, BlockDropReference> {
    public BlockDropsView(Collection<BlockDrop> liveCollection) {
        super(liveCollection, BlockDropReference::new);
    }
}
