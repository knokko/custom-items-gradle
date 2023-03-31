package nl.knokko.customitems.itemset;

import nl.knokko.customitems.damage.CustomDamageSource;
import nl.knokko.customitems.damage.CustomDamageSourceValues;

import java.util.Collection;
import java.util.UUID;

public class CustomDamageSourceReference extends UUIDBasedReference<CustomDamageSource, CustomDamageSourceValues> {
    CustomDamageSourceReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    CustomDamageSourceReference(CustomDamageSource model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Damage source";
    }

    @Override
    Collection<CustomDamageSource> getCollection() {
        return itemSet.damageSources;
    }

    @Override
    UUID extractIdentity(CustomDamageSourceValues values) {
        return values.getId();
    }
}
