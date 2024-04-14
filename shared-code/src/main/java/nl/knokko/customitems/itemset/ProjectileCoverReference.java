package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.ProjectileCover;

import java.util.Collection;

public class ProjectileCoverReference extends StringBasedReference<ProjectileCover, ProjectileCoverValues> {
    ProjectileCoverReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileCoverReference(ProjectileCover model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Projectile cover";
    }

    @Override
    Collection<ProjectileCover> getCollection() {
        return itemSet.projectileCovers.elements;
    }

    @Override
    String extractIdentity(ProjectileCoverValues values) {
        return values.getName();
    }
}
