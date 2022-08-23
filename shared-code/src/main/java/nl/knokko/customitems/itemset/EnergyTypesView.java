package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class EnergyTypesView extends CollectionView<EnergyType, EnergyTypeValues, EnergyTypeReference> {
    public EnergyTypesView(Collection<EnergyType> liveCollection) {
        super(liveCollection, EnergyTypeReference::new);
    }
}
