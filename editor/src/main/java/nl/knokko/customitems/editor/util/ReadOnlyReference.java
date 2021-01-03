package nl.knokko.customitems.editor.util;

public class ReadOnlyReference<T> {
	
	private final Reference<T> reference;

	public ReadOnlyReference(Reference<T> reference) {
		this.reference = reference;
	}

	public T get() {
		return reference.get();
	}
}
