package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.SProjectileCover;

import java.util.Collection;

public class ProjectileCoverReference extends StringBasedReference<SProjectileCover, ProjectileCoverValues> {
    ProjectileCoverReference(String name, SItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileCoverReference(SProjectileCover model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Projectile cover";
    }

    @Override
    Collection<SProjectileCover> getCollection() {
        return itemSet.projectileCovers;
    }

    @Override
    String extractIdentity(ProjectileCoverValues values) {
        return values.getName();
    }
}
