package nl.knokko.customitems.util;

import java.util.function.Supplier;

public class EagerSupplier<T> implements Supplier<T> {

    private final T value;

    public EagerSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
