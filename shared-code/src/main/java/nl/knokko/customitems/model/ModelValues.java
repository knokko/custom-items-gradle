package nl.knokko.customitems.model;

public abstract class ModelValues {

    private final boolean mutable;

    protected ModelValues(boolean mutable) {
        this.mutable = mutable;
    }

    public boolean isMutable() {
        return mutable;
    }

    protected void assertMutable() {
        if (!mutable) {
            throw new UnsupportedOperationException("Attempting to mutate immutable model values");
        }
    }

    public abstract ModelValues copy(boolean mutable);
}
