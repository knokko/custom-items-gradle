package nl.knokko.customitems.itemset;

import nl.knokko.customitems.recipe.upgrade.UpgradeValues;

import java.util.Collection;
import java.util.UUID;

public class UpgradeReference extends UUIDBasedReference<UpgradeValues> {

    UpgradeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    UpgradeReference(Model<UpgradeValues> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "upgrade";
    }

    @Override
    Collection<Model<UpgradeValues>> getCollection() {
        return itemSet.upgrades.elements;
    }

    @Override
    UUID extractIdentity(UpgradeValues values) {
        return values.getId();
    }

    @Override
    public String toString() {
        Model<UpgradeValues> upgrade = getModel();
        if (upgrade != null) return upgrade.getValues().getName();
        else return "Upgrade " + id;
    }
}
