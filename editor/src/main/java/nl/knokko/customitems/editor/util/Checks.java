package nl.knokko.customitems.editor.util;

import java.util.Collection;

public class Checks {

	public static void notNull(Object object) {
		if (object == null)
			throw new NullPointerException();
	}
	
	public static void nonNull(Object[] array) {
		for (Object object : array)
			notNull(object);
	}
	
	public static void nonNull(Collection<?> collection) {
		collection.forEach(Checks::notNull);
	}
}
