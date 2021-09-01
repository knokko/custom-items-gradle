package nl.knokko.customitems.model;

public abstract class Model<V extends ModelValues> {

    protected V values;

    @SuppressWarnings("unchecked")
    public Model(V values) {
        this.values = (V) values.copy(false);
    }

    public V getValues() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public V cloneValues() {
        return (V) values.copy(true);
    }

    @SuppressWarnings("unchecked")
    public void setValues(V newValues) {
        this.values = (V) newValues.copy(false);
    }
}
