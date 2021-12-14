package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.SCustomProjectile;

import java.util.Collection;

public class CustomProjectilesView extends CollectionView<SCustomProjectile, CustomProjectileValues, ProjectileReference> {
    public CustomProjectilesView(Collection<SCustomProjectile> liveCollection) {
        super(liveCollection, ProjectileReference::new);
    }
}
