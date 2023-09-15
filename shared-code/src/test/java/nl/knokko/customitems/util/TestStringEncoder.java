package nl.knokko.customitems.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	private void checkEncodeTexty(byte[] payload) {
		byte[] textyPayload1 = StringEncoder.encodeTextyBytes(payload, false);
		byte[] textyPayload2 = StringEncoder.encodeTextyBytes(payload, true);

		String stringPayload1 = new String(textyPayload1, StandardCharsets.US_ASCII);
		String stringPayload2 = new String(textyPayload2, StandardCharsets.US_ASCII);

		byte[] revertedString1 = stringPayload1.getBytes(StandardCharsets.US_ASCII);
		byte[] revertedString2 = stringPayload2.getBytes(StandardCharsets.US_ASCII);

		assertArrayEquals(textyPayload1, revertedString1);
		assertArrayEquals(textyPayload2, revertedString2);

		byte[] decodedPayload1 = StringEncoder.decodeTextyBytes(revertedString1);
		byte[] decodedPayload2 = StringEncoder.decodeTextyBytes(revertedString2);

		assertArrayEquals(decodedPayload1, payload);
		assertArrayEquals(decodedPayload2, payload);
	}

	@Test
	public void testRandomBytes() {
		Random rng = new Random(83401);

		for (int counter = 0; counter < 100; counter++) {
			byte[] payload = new byte[rng.nextInt(1000)];
			rng.nextBytes(payload);
			checkEncodeTexty(payload);
		}
	}
}
