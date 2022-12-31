package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.CollectionView;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;

import java.util.Collection;

public class UpgradesView extends CollectionView<Upgrade, UpgradeValues, UpgradeReference> {
    public UpgradesView(Collection<Upgrade> liveCollection) {
        super(liveCollection, UpgradeReference::new);
    }
}
