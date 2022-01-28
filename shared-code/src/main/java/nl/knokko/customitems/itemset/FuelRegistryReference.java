package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;

import java.util.Collection;

public class FuelRegistryReference extends StringBasedReference<CustomFuelRegistry, FuelRegistryValues> {

    FuelRegistryReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    FuelRegistryReference(CustomFuelRegistry model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Fuel registry";
    }

    @Override
    Collection<CustomFuelRegistry> getCollection() {
        return itemSet.fuelRegistries;
    }

    @Override
    String extractIdentity(FuelRegistryValues values) {
        return values.getName();
    }
}
