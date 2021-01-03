package nl.knokko.customitems.util;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.Test;

public class TestStringEncoder {
	
	private void checkEncode(String toEncode) {
		assertEquals(toEncode, StringEncoder.decode(StringEncoder.encode(toEncode)));
	}

	@Test
	public void testBasicStrings() {
		checkEncode("Hello, World!");
		checkEncode("Multiple	\n\rlines		\n\r");
		checkEncode("Also لْأَبْجَدِيَّة الْعَرَبِيَّة");
	}
	
	private boolean isValid(String toTry) {
		byte[] asBytes = toTry.getBytes(StandardCharsets.UTF_8);
		return new String(asBytes, StandardCharsets.UTF_8).equals(toTry);
	}
	
	private void testRandomString(String toTry) {
		assertEquals(toTry, StringEncoder.decode(StringEncoder.encode(toTry)));
	}
	
	@Test
	public void testRandomStrings() {
		Random rng = new Random(872364723);
		for (int counter = 0; counter < 1000; counter++) {
			int length = 5 + rng.nextInt(20);
			char[] theChars = new char[length];
			for (int index = 0; index < length; index++) {
				theChars[index] = (char) rng.nextInt(Character.MAX_VALUE);
			}
			String toTry = new String(theChars);
			if (isValid(toTry)) {
				testRandomString(new String(theChars));
			} else {
				counter--;
			}
		}
	}
}
