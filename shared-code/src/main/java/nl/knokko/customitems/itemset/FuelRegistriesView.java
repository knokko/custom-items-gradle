package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.SFuelRegistry;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class FuelRegistriesView extends CollectionView<SFuelRegistry, FuelRegistryValues, FuelRegistryReference> {
    public FuelRegistriesView(Collection<SFuelRegistry> liveCollection) {
        super(liveCollection, FuelRegistryReference::new);
    }
}
