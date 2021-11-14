package nl.knokko.customitems.util;

import java.util.Collection;

import static java.lang.Math.abs;

public class Checks {

	public static void notNull(Object object) {
		if (object == null)
			throw new NullPointerException();
	}
	
	public static void nonNull(Object... array) {
		for (Object object : array)
			notNull(object);
	}

	public static void nonNullArray(Object[] array) {
	    for (Object object : array) {
	    	notNull(object);
		}
	}
	
	public static void nonNull(Collection<?> collection) {
		collection.forEach(Checks::notNull);
	}

	public static boolean isClose(float a, float b) {
		return isClose(a, b, 0.001f);
	}

	public static boolean isClose(float a, float b, float maxDifference) {
		return abs(a - b) <= maxDifference;
	}

	public static boolean isClose(double a, double b) {
		return isClose(a, b, 0.001);
	}

	public static boolean isClose(double a, double b, double maxDifference) {
		return abs(a - b) <= maxDifference;
	}
}
