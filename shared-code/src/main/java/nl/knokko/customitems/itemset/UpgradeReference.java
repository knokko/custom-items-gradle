package nl.knokko.customitems.itemset;

import nl.knokko.customitems.recipe.upgrade.Upgrade;

import java.util.Collection;
import java.util.UUID;

public class UpgradeReference extends UUIDBasedReference<Upgrade> {

    UpgradeReference(UUID id, ItemSet itemSet) {
        super(id, itemSet);
    }

    UpgradeReference(Model<Upgrade> model) {
        super(model);
    }

    @Override
    String getDescription() {
        return "upgrade";
    }

    @Override
    Collection<Model<Upgrade>> getCollection() {
        return itemSet.upgrades.elements;
    }

    @Override
    UUID extractIdentity(Upgrade values) {
        return values.getId();
    }

    @Override
    public String toString() {
        Model<Upgrade> upgrade = getModel();
        if (upgrade != null) return upgrade.getValues().getName();
        else return "Upgrade " + id;
    }
}
