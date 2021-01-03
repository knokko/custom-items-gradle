package nl.knokko.customitems.editor.util;

public class Reference<T> {
	
	private T currentValue;

	public Reference(T initialValue) {
		currentValue = initialValue;
	}

	public T get() {
		return currentValue;
	}
	
	public void set(T newValue) {
		currentValue = newValue;
	}
}
