package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

abstract class IntBasedReference<V extends ModelValues> extends ModelReference<V> implements Supplier<V> {

    int id;
    ItemSet itemSet;

    Model<V> model;

    IntBasedReference(int id, ItemSet itemSet) {
        this.id = id;
        this.itemSet = Objects.requireNonNull(itemSet);
        itemSet.intReferences.add(this);
    }

    IntBasedReference(Model<V> model) {
        this.model = Objects.requireNonNull(model);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            int ownId = model != null ? extractIdentity(model.getValues()) : id;
            IntBasedReference<V> otherReference = (IntBasedReference<V>) other;
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

    abstract Collection<Model<V>> getCollection();

    abstract int extractIdentity(V values);

    public V get() {
        Model<V> model = getModel();
        if (model == null) throw new RuntimeException("Can't find " + getDescription() + " with id " + id);
        return model.getValues();
    }

    Model<V> getModel() {
        if (model == null) {
            if (!itemSet.finishedLoading) {
                throw new IllegalStateException("Attempted to load " + getDescription() + id + " before the item set finished loading");
            }

            Optional<Model<V>> foundModel = CollectionHelper.find(getCollection(), item -> extractIdentity(item.getValues()), id);

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
