package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.SFuelRegistry;

import java.util.Collection;

public class FuelRegistryReference extends StringBasedReference<SFuelRegistry, FuelRegistryValues> {

    FuelRegistryReference(String name, SItemSet itemSet) {
        super(name, itemSet);
    }

    FuelRegistryReference(SFuelRegistry model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Fuel registry";
    }

    @Override
    Collection<SFuelRegistry> getCollection() {
        return itemSet.fuelRegistries;
    }

    @Override
    String extractIdentity(FuelRegistryValues values) {
        return values.getName();
    }
}
