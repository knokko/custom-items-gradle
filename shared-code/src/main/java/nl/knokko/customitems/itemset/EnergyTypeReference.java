package nl.knokko.customitems.itemset;

import nl.knokko.customitems.container.energy.EnergyTypeValues;

import java.util.Collection;
import java.util.UUID;

public class EnergyTypeReference extends UUIDBasedReference<EnergyTypeValues> {

    EnergyTypeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    EnergyTypeReference(Model<EnergyTypeValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "energy type";
    }

    @Override
    Collection<Model<EnergyTypeValues>> getCollection() {
        return itemSet.energyTypes.elements;
    }

    @Override
    UUID extractIdentity(EnergyTypeValues values) {
        return values.getId();
    }
}
