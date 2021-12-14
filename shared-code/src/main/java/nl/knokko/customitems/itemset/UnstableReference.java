package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;

import java.util.function.Supplier;

abstract class UnstableReference<M extends Model<V>, V extends ModelValues> implements Supplier<V> {

    final M model;

    UnstableReference(M model) {
        Checks.notNull(model);
        this.model = model;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            return model == ((UnstableReference) other).model;
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

    M getModel() {
        return model;
    }
}
