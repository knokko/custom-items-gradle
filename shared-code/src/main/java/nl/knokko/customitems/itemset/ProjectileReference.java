package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.CustomProjectile;

import java.util.Collection;

public class ProjectileReference extends StringBasedReference<CustomProjectile, CustomProjectileValues> {

    ProjectileReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileReference(CustomProjectile model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "projectile";
    }

    @Override
    Collection<CustomProjectile> getCollection() {
        return itemSet.projectiles;
    }

    @Override
    String extractIdentity(CustomProjectileValues values) {
        return values.getName();
    }
}
