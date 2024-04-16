package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.CustomProjectileValues;

import java.util.Collection;

public class ProjectileReference extends StringBasedReference<CustomProjectileValues> {

    ProjectileReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileReference(Model<CustomProjectileValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "projectile";
    }

    @Override
    Collection<Model<CustomProjectileValues>> getCollection() {
        return itemSet.projectiles.elements;
    }

    @Override
    String extractIdentity(CustomProjectileValues values) {
        return values.getName();
    }
}
