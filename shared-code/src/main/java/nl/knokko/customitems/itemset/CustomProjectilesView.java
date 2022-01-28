package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.CustomProjectile;

import java.util.Collection;

public class CustomProjectilesView extends CollectionView<CustomProjectile, CustomProjectileValues, ProjectileReference> {
    public CustomProjectilesView(Collection<CustomProjectile> liveCollection) {
        super(liveCollection, ProjectileReference::new);
    }
}
