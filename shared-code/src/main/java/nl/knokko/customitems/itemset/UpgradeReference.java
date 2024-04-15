package nl.knokko.customitems.itemset;

import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;

import java.util.Collection;
import java.util.UUID;

public class UpgradeReference extends UUIDBasedReference<Upgrade, UpgradeValues> {

    UpgradeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    UpgradeReference(Upgrade model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "upgrade";
    }

    @Override
    Collection<Upgrade> getCollection() {
        return itemSet.upgrades.elements;
    }

    @Override
    UUID extractIdentity(UpgradeValues values) {
        return values.getId();
    }

    @Override
    public String toString() {
        Upgrade upgrade = getModel();
        if (upgrade != null) return upgrade.getValues().getName();
        else return "Upgrade " + id;
    }
}
