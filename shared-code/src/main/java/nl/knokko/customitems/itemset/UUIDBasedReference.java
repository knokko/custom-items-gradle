package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class UUIDBasedReference<M extends Model<V>, V extends ModelValues> extends ModelReference<M, V> implements Supplier<V> {

    UUID id;
    ItemSet itemSet;

    M model;

    UUIDBasedReference(UUID id, ItemSet itemSet) {
        Checks.nonNull(id, itemSet);
        this.id = id;
        this.itemSet = itemSet;
        itemSet.uuidReferences.add(this);
    }

    UUIDBasedReference(M model) {
        Checks.notNull(model);
        this.model = model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other != null && other.getClass() == this.getClass()) {
            UUID ownId = model != null ? extractIdentity(model.getValues()) : id;
            UUIDBasedReference<M, V> otherReference = (UUIDBasedReference<M, V>) other;
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

    abstract Collection<M> getCollection();

    abstract UUID extractIdentity(V values);

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
            id = null;
            itemSet = null;
        }
        return model;
    }
}
