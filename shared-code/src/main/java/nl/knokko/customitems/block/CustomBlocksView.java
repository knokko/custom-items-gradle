package nl.knokko.customitems.block;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomBlocksView extends CollectionView<CustomBlock, CustomBlockValues> {

    public CustomBlocksView(Collection<CustomBlock> liveCollection) {
        super(liveCollection);
    }
}
