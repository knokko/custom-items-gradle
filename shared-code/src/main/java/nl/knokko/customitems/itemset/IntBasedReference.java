package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

abstract class IntBasedReference<M extends Model<V>, V extends ModelValues> extends ModelReference<M, V> implements Supplier<V> {

    int id;
    ItemSet itemSet;

    M model;

    IntBasedReference(int id, ItemSet itemSet) {
        Checks.notNull(itemSet);
        this.id = id;
        this.itemSet = itemSet;
        itemSet.intReferences.add(this);
    }

    IntBasedReference(M model) {
        Checks.notNull(model);
        this.model = model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            int ownId = model != null ? extractIdentity(model.getValues()) : id;
            IntBasedReference<M, V> otherReference = (IntBasedReference<M, V>) other;
            int otherId = otherReference.model != null ? otherReference.extractIdentity(otherReference.model.getValues()) : otherReference.id;
            return ownId == otherId;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        int obtainedID = model != null ? extractIdentity(model.getValues()) : id;
        return getDescription() + " " + obtainedID;
    }

    @Override
    public int hashCode() {
        return this.model != null ? extractIdentity(this.model.getValues()) : this.id;
    }

    abstract String getDescription();

    abstract Collection<M> getCollection();

    abstract int extractIdentity(V values);

    public V get() {
        M model = getModel();
        if (model == null) throw new RuntimeException("Can't find " + getDescription() + " with id " + id);
        return model.getValues();
    }

    M getModel() {
        if (model == null) {
            if (!itemSet.finishedLoading) {
                throw new IllegalStateException("Attempted to load " + getDescription() + id + " before the item set finished loading");
            }

            Optional<M> foundModel = CollectionHelper.find(getCollection(), item -> extractIdentity(item.getValues()), id);

            if (foundModel.isPresent()) {
                model = foundModel.get();
            } else {
                return null;
            }
            id = -1;
            itemSet = null;
        }
        return model;
    }
}
