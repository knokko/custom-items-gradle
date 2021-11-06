package nl.knokko.customitems.container.fuel;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class FuelRegistriesView extends CollectionView<SFuelRegistry, FuelRegistryValues> {
    public FuelRegistriesView(Collection<SFuelRegistry> liveCollection) {
        super(liveCollection);
    }
}
