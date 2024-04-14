package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.Model;
import nl.knokko.customitems.model.ModelValues;

public abstract class ModelReference<M extends Model<V>, V extends ModelValues> {

    public abstract V get();

    abstract M getModel();
}
