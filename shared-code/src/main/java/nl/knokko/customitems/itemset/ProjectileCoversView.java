package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.SProjectileCover;

import java.util.Collection;

public class ProjectileCoversView extends CollectionView<SProjectileCover, ProjectileCoverValues, ProjectileCoverReference> {
    public ProjectileCoversView(Collection<SProjectileCover> liveCollection) {
        super(liveCollection, ProjectileCoverReference::new);
    }
}
