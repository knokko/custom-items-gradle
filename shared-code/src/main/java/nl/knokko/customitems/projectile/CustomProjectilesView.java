package nl.knokko.customitems.projectile;

import nl.knokko.customitems.model.CollectionView;

import java.util.Collection;

public class CustomProjectilesView extends CollectionView<SCustomProjectile, CustomProjectileValues> {
    public CustomProjectilesView(Collection<SCustomProjectile> liveCollection) {
        super(liveCollection);
    }
}
