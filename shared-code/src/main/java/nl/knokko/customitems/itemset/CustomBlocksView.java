package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.CustomBlock;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomBlocksView extends CollectionView<CustomBlock, CustomBlockValues, BlockReference> {

    public CustomBlocksView(Collection<CustomBlock> liveCollection) {
        super(liveCollection, BlockReference::new);
    }
}
