package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.ProjectileCover;

import java.util.Collection;

public class ProjectileCoversView extends CollectionView<ProjectileCover, ProjectileCoverValues, ProjectileCoverReference> {
    public ProjectileCoversView(Collection<ProjectileCover> liveCollection) {
        super(liveCollection, ProjectileCoverReference::new);
    }
}
