package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.Collection;
import java.util.Optional;

abstract class StringBasedReference<M extends Model<V>, V extends ModelValues> {

    String name;
    SItemSet itemSet;

    M model;

    StringBasedReference(String name, SItemSet itemSet) {
        Checks.nonNull(name, itemSet);
        this.name = name;
        this.itemSet = itemSet;
    }

    StringBasedReference(M model) {
        Checks.notNull(model);
        this.model = model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            String ownName = name != null ? name : extractIdentity(model.getValues());
            StringBasedReference<M, V> otherRef = (StringBasedReference<M, V>) other;
            String otherName = otherRef.name != null ? otherRef.name : otherRef.extractIdentity(otherRef.model.getValues());
            return ownName.equals(otherName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (model != null) return extractIdentity(model.getValues()).hashCode();
        return name.hashCode();
    }

    abstract String getDescription();

    abstract Collection<M> getCollection();

    abstract String extractIdentity(V values);

    public V get() {
        M foundModel = getModel();
        if (foundModel == null) {
            throw new RuntimeException("Can't find " + getDescription() + " with name " + name);
        }
        return foundModel.getValues();
    }

    M getModel() {
        if (model == null) {
            if (!itemSet.finishedLoading) {
                throw new IllegalStateException("Attempted to load " + getDescription() + name + " before the item set finished loading");
            }

            Optional<M> foundModel = CollectionHelper.find(getCollection(), item -> extractIdentity(item.getValues()), name);

            if (foundModel.isPresent()) {
                model = foundModel.get();
            } else {
                return null;
            }
            name = null;
            itemSet = null;
        }
        return model;
    }
}
