package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.worldgen.TreeGenerator;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;

import java.util.Collection;

public class TreeGeneratorsView extends CollectionView<TreeGenerator, TreeGeneratorValues, TreeGeneratorReference> {

    TreeGeneratorsView(Collection<TreeGenerator> liveCollection) {
        super(liveCollection, TreeGeneratorReference::new);
    }
}
