package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.fuel.ContainerFuelRegistry;

import java.util.Collection;

public class FuelRegistryReference extends StringBasedReference<ContainerFuelRegistry> {

    FuelRegistryReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    FuelRegistryReference(Model<ContainerFuelRegistry> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Fuel registry";
    }

    @Override
    Collection<Model<ContainerFuelRegistry>> getCollection() {
        return itemSet.fuelRegistries.elements;
    }

    @Override
    String extractIdentity(ContainerFuelRegistry values) {
        return values.getName();
    }
}
