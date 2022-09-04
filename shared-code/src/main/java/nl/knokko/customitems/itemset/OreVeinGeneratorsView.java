package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.worldgen.OreVeinGenerator;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;

import java.util.Collection;

public class OreVeinGeneratorsView extends CollectionView<OreVeinGenerator, OreVeinGeneratorValues, OreVeinGeneratorReference> {

    OreVeinGeneratorsView(Collection<OreVeinGenerator> liveCollection) {
        super(liveCollection, OreVeinGeneratorReference::new);
    }
}
