package nl.knokko.customitems.itemset;

import nl.knokko.customitems.damage.CustomDamageSourceValues;

import java.util.Collection;
import java.util.UUID;

public class CustomDamageSourceReference extends UUIDBasedReference<CustomDamageSourceValues> {
    CustomDamageSourceReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    CustomDamageSourceReference(Model<CustomDamageSourceValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Damage source";
    }

    @Override
    Collection<Model<CustomDamageSourceValues>> getCollection() {
        return itemSet.damageSources.elements;
    }

    @Override
    UUID extractIdentity(CustomDamageSourceValues values) {
        return values.getId();
    }
}
