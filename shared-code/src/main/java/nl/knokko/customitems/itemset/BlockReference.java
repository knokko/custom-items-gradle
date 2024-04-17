package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.KciBlock;

import java.util.Collection;

public class BlockReference extends IntBasedReference<KciBlock> {

    BlockReference(int id, ItemSet itemSet) {
        super(id, itemSet);
    }

    BlockReference(Model<KciBlock> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "block";
    }

    @Override
    Collection<Model<KciBlock>> getCollection() {
        return itemSet.blocks.elements;
    }

    @Override
    int extractIdentity(KciBlock values) {
        return values.getInternalID();
    }
}
