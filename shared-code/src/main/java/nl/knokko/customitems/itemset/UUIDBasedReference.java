package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class UUIDBasedReference<V extends ModelValues> extends ModelReference<V> implements Supplier<V> {

    UUID id;
    ItemSet itemSet;

    Model<V> model;

    UUIDBasedReference(UUID id, ItemSet itemSet) {
        this.id = Objects.requireNonNull(id);
        this.itemSet = Objects.requireNonNull(itemSet);
        itemSet.uuidReferences.add(this);
    }

    UUIDBasedReference(Model<V> model) {
        this.model = Objects.requireNonNull(model);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other != null && other.getClass() == this.getClass()) {
            UUID ownId = model != null ? extractIdentity(model.getValues()) : id;
            UUIDBasedReference<V> otherReference = (UUIDBasedReference<V>) other;
            UUID otherId = otherReference.model != null ? otherReference.extractIdentity(otherReference.model.getValues()) : otherReference.id;
            return ownId.equals(otherId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.model != null ? extractIdentity(this.model.getValues()) : this.id).hashCode();
    }

    abstract String getDescription();

    abstract Collection<Model<V>> getCollection();

    abstract UUID extractIdentity(V values);

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
            id = null;
            itemSet = null;
        }
        return model;
    }
}
