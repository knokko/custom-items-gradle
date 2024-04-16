package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;

import java.util.Collection;

public class FuelRegistryReference extends StringBasedReference<FuelRegistryValues> {

    FuelRegistryReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    FuelRegistryReference(Model<FuelRegistryValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Fuel registry";
    }

    @Override
    Collection<Model<FuelRegistryValues>> getCollection() {
        return itemSet.fuelRegistries.elements;
    }

    @Override
    String extractIdentity(FuelRegistryValues values) {
        return values.getName();
    }
}
