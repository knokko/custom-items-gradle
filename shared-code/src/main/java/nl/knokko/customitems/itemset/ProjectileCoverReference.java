package nl.knokko.customitems.itemset;

import nl.knokko.customitems.projectile.cover.ProjectileCover;

import java.util.Collection;

public class ProjectileCoverReference extends StringBasedReference<ProjectileCover> {
    ProjectileCoverReference(String name, ItemSet itemSet) {
        super(name, itemSet);
    }

    ProjectileCoverReference(Model<ProjectileCover> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "Projectile cover";
    }

    @Override
    Collection<Model<ProjectileCover>> getCollection() {
        return itemSet.projectileCovers.elements;
    }

    @Override
    String extractIdentity(ProjectileCover values) {
        return values.getName();
    }
}
