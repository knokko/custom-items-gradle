package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class FuelRegistriesView extends CollectionView<CustomFuelRegistry, FuelRegistryValues, FuelRegistryReference> {
    public FuelRegistriesView(Collection<CustomFuelRegistry> liveCollection) {
        super(liveCollection, FuelRegistryReference::new);
    }
}
