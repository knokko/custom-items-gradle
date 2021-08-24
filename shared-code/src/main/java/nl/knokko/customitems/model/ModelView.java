package nl.knokko.customitems.model;

public abstract class ModelView<M, V> {

    protected final M model;

    public ModelView(M model) {
        this.model = model;
    }

    public V getValues() {
        return model.getValues();
    }

    public V cloneValues() {
        return model.cloneValues();
    }
}
