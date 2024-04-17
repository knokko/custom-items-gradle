package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.energy.EnergyType;

import java.util.Collection;
import java.util.UUID;

public class EnergyTypeReference extends UUIDBasedReference<EnergyType> {

    EnergyTypeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    EnergyTypeReference(Model<EnergyType> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "energy type";
    }

    @Override
    Collection<Model<EnergyType>> getCollection() {
        return itemSet.energyTypes.elements;
    }

    @Override
    UUID extractIdentity(EnergyType values) {
        return values.getId();
    }
}
