package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.CustomBlock;
import nl.knokko.customitems.block.CustomBlockValues;

import java.util.Collection;

public class BlockReference extends IntBasedReference<CustomBlock, CustomBlockValues> {

    BlockReference(int id, ItemSet itemSet) {
        super(id, itemSet);
    }

    BlockReference(CustomBlock model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "block";
    }

    @Override
    Collection<CustomBlock> getCollection() {
        return itemSet.blocks;
    }

    @Override
    int extractIdentity(CustomBlockValues values) {
        return values.getInternalID();
    }
}
