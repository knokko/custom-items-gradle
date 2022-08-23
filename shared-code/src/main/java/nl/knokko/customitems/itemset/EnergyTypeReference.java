package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.container.energy.EnergyTypeValues;

import java.util.Collection;
import java.util.UUID;

public class EnergyTypeReference extends UUIDBasedReference<EnergyType, EnergyTypeValues> {

    EnergyTypeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    EnergyTypeReference(EnergyType model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "energy type";
    }

    @Override
    Collection<EnergyType> getCollection() {
        return itemSet.energyTypes;
    }

    @Override
    UUID extractIdentity(EnergyTypeValues values) {
        return values.getId();
    }
}
