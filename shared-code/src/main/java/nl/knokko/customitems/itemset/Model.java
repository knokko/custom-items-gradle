package nl.knokko.customitems.itemset;

import nl.knokko.customitems.model.ModelValues;

final class Model<V extends ModelValues> {

    private V values;

    @SuppressWarnings("unchecked")
    public Model(V values) {
        this.values = (V) values.copy(false);
    }

    public V getValues() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public void setValues(V newValues) {
        this.values = (V) newValues.copy(false);
    }

    @Override
    public String toString() {
        return "Model(" + values + ")";
    }
}
