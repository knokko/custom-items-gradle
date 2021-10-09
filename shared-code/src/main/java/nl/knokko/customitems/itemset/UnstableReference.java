package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;

abstract class UnstableReference<M extends Model<V>, V extends ModelValues> {

    final M model;

    UnstableReference(M model) {
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
}
