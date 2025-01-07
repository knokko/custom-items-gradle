package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

abstract class StringBasedReference<V extends ModelValues> extends ModelReference<V> implements Supplier<V> {

    String name;
    ItemSet itemSet;

    Model<V> model;

    StringBasedReference(String name, ItemSet itemSet) {
        this.name = Objects.requireNonNull(name);
        this.itemSet = Objects.requireNonNull(itemSet);
        itemSet.stringReferences.add(this);
    }

    StringBasedReference(Model<V> model) {
        this.model = Objects.requireNonNull(model);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            String ownName = name != null ? name : extractIdentity(model.getValues());
            StringBasedReference<V> otherRef = (StringBasedReference<V>) other;
            String otherName = otherRef.name != null ? otherRef.name : otherRef.extractIdentity(otherRef.model.getValues());
            return ownName.equals(otherName);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.name == null) return this.extractIdentity(this.model.getValues());
        else return this.name;
    }

    @Override
    public int hashCode() {
        if (model != null) return extractIdentity(model.getValues()).hashCode();
        return name.hashCode();
    }

    abstract String getDescription();

    abstract Collection<Model<V>> getCollection();

    abstract String extractIdentity(V values);

    public V get() {
        Model<V> foundModel = getModel();
        if (foundModel == null) {
            throw new RuntimeException("Can't find " + getDescription() + " with name " + name);
        }
        return foundModel.getValues();
    }

    Model<V> getModel() {
        if (model == null) {
            if (!itemSet.finishedLoading) {
                throw new IllegalStateException("Attempted to load " + getDescription() + " " + name + " before the item set finished loading");
            }

            Optional<Model<V>> foundModel = CollectionHelper.find(getCollection(), item -> extractIdentity(item.getValues()), name);

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
