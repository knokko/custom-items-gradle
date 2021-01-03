package nl.knokko.customitems.util;

@FunctionalInterface
public interface ExceptionSupplier<T, E extends Throwable> {

	T get() throws E;
}
