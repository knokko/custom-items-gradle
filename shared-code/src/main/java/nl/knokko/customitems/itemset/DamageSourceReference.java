package nl.knokko.customitems.itemset;

import nl.knokko.customitems.damage.KciDamageSource;

import java.util.Collection;
import java.util.UUID;

public class DamageSourceReference extends UUIDBasedReference<KciDamageSource> {
    DamageSourceReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    DamageSourceReference(Model<KciDamageSource> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Damage source";
    }

    @Override
    Collection<Model<KciDamageSource>> getCollection() {
        return itemSet.damageSources.elements;
    }

    @Override
    UUID extractIdentity(KciDamageSource values) {
        return values.getId();
    }
}
