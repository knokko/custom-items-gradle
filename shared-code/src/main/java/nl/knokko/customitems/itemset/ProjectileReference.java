package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.SCustomProjectile;

import java.util.Collection;

public class ProjectileReference extends StringBasedReference<SCustomProjectile, CustomProjectileValues> {

    ProjectileReference(String name, SItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileReference(SCustomProjectile model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "projectile";
    }

    @Override
    Collection<SCustomProjectile> getCollection() {
        return itemSet.projectiles;
    }

    @Override
    String extractIdentity(CustomProjectileValues values) {
        return values.getName();
    }
}
