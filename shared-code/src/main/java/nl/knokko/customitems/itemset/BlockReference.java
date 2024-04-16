package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.CustomBlockValues;

import java.util.Collection;

public class BlockReference extends IntBasedReference<CustomBlockValues> {

    BlockReference(int id, ItemSet itemSet) {
        super(id, itemSet);
    }

    BlockReference(Model<CustomBlockValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "block";
    }

    @Override
    Collection<Model<CustomBlockValues>> getCollection() {
        return itemSet.blocks.elements;
    }

    @Override
    int extractIdentity(CustomBlockValues values) {
        return values.getInternalID();
    }
}
