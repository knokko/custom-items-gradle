package nl.knokko.customitems.drops;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class BlockDropsView extends CollectionView<SBlockDrop, BlockDropValues> {
    public BlockDropsView(Collection<SBlockDrop> liveCollection) {
        super(liveCollection);
    }
}
