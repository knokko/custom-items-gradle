package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.ModelValues;

public abstract class ModelReference<V extends ModelValues> {

    public abstract V get();

    abstract Model<V> getModel();
}
