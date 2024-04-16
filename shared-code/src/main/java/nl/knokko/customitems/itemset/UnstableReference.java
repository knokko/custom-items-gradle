package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.ModelValues;

import java.util.Objects;
import java.util.function.Supplier;

abstract class UnstableReference<V extends ModelValues> extends ModelReference<V> implements Supplier<V> {

    final Model<V> model;

    UnstableReference(Model<V> model) {
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            //noinspection unchecked
            return model == ((UnstableReference<V>) other).model;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return model.hashCode();
    }

    public V get() {
        return model.getValues();
    }

    Model<V> getModel() {
        return model;
    }
}
