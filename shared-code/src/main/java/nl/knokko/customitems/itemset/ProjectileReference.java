package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.KciProjectile;

import java.util.Collection;

public class ProjectileReference extends StringBasedReference<KciProjectile> {

    ProjectileReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileReference(Model<KciProjectile> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "projectile";
    }

    @Override
    Collection<Model<KciProjectile>> getCollection() {
        return itemSet.projectiles.elements;
    }

    @Override
    String extractIdentity(KciProjectile values) {
        return values.getName();
    }
}
