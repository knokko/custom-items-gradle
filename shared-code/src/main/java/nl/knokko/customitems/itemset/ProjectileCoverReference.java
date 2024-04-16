package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;

import java.util.Collection;

public class ProjectileCoverReference extends StringBasedReference<ProjectileCoverValues> {
    ProjectileCoverReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileCoverReference(Model<ProjectileCoverValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Projectile cover";
    }

    @Override
    Collection<Model<ProjectileCoverValues>> getCollection() {
        return itemSet.projectileCovers.elements;
    }

    @Override
    String extractIdentity(ProjectileCoverValues values) {
        return values.getName();
    }
}
