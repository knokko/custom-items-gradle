package nl.knokko.customitems.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import nl.knokko.customitems.bithelper.BitHelper;

public class StringEncoder {

	public static String encode(String input) {
		byte[] payloadBytes = input.getBytes(StandardCharsets.UTF_8);
		
		// Also encode the length to make sure no characters were forgotten
		byte[] inputBytes = new byte[4 + payloadBytes.length];
		inputBytes[0] = BitHelper.int0(payloadBytes.length);
		inputBytes[1] = BitHelper.int1(payloadBytes.length);
		inputBytes[2] = BitHelper.int2(payloadBytes.length);
		inputBytes[3] = BitHelper.int3(payloadBytes.length);
		System.arraycopy(payloadBytes, 0, inputBytes, 4, payloadBytes.length);
		
		// Turn them into alphabetic chars to avoid any special characters
		char[] outputChars = new char[inputBytes.length * 2];
		for (int inputIndex = 0; inputIndex < inputBytes.length; inputIndex++) {
			int inputByte = inputBytes[inputIndex] & 0xFF;
			int outputIndex = inputIndex * 2;
			outputChars[outputIndex] = (char) ('a' + inputByte / 16);
			outputChars[outputIndex + 1] = (char) ('a' + inputByte % 16);
		}
		return new String(outputChars);
	}
	
	public static String decode(String encoded) throws IllegalArgumentException {
		
		// Start by filtering out potential white spaces, line breaks, or whatever
		// other garbage the string contains
		StringBuilder outputString = new StringBuilder();
		for (int index = 0; index < encoded.length(); index++) {
			char currentChar = encoded.charAt(index);
			if (currentChar >= 'a' && currentChar < 'a' + 16) {
				outputString.append(currentChar);
			}
		}
		
		char[] outputChars = outputString.toString().toCharArray();
		if (outputChars.length % 2 != 0) {
			throw new IllegalArgumentException("Encoded strings must have even length");
		}
		
		// Reconstruct the inputBytes of the encode method
		byte[] inputBytes = new byte[outputChars.length / 2];
		for (int inputIndex = 0; inputIndex < inputBytes.length; inputIndex++) {
			int outputIndex = inputIndex * 2;
			int first = outputChars[outputIndex] - 'a';
			int second = outputChars[outputIndex + 1] - 'a';
			inputBytes[inputIndex] = (byte) (16 * first + second);
		}
		
		if (inputBytes.length < 4) {
			throw new IllegalArgumentException("Length is much too short");
		}
		
		// Verify that the length is correct
		int inputLength = BitHelper.makeInt(
				inputBytes[0], inputBytes[1], inputBytes[2], inputBytes[3]
		);
		if (inputLength + 4 != inputBytes.length) {
			throw new IllegalArgumentException("Expected length " + (inputLength + 4) + " but got " + inputBytes.length);
		}
		
		byte[] payloadBytes = Arrays.copyOfRange(inputBytes, 4, inputBytes.length);
		return new String(payloadBytes, StandardCharsets.UTF_8);
	}

	public static byte[] decodeTextyBytes(byte[] bytes) throws IllegalArgumentException {
		int counter = 0;
		for (byte b : bytes) {
			if (b >= 'a' && b < ('a' + 16)) {
				counter++;
			}
		}

		int byteSize = counter / 2;
		if (byteSize * 2 != counter) {
            throw new IllegalArgumentException();
		}

		byte[] dataBytes = new byte[byteSize];

		int textIndex = 0;
		for (int dataIndex = 0; dataIndex < byteSize; dataIndex++) {
			int firstPart = bytes[textIndex++];
			while (firstPart < 'a' || firstPart >= 'a' + 16) {
				firstPart = bytes[textIndex++];
			}
			firstPart -= 'a';
			int secondPart = bytes[textIndex++];
			while (secondPart < 'a' || secondPart >= 'a' + 16) {
				secondPart = bytes[textIndex++];
			}
			secondPart -= 'a';
			dataBytes[dataIndex] = (byte) (firstPart + 16 * secondPart);
		}

		return dataBytes;
	}

	public static byte[] encodeTextyBytes(byte[] bytes, boolean useLineBreaks) {
		byte[] textBytes = new byte[2 * bytes.length + 2 * (bytes.length / 50)];

		int textIndex = 0;
		int textCounter = 0;
		byte charCodeA = (byte) 'a';
		byte charCodeSR = (byte) '\r';
		byte charCodeSN = (byte) '\n';

		for (byte data : bytes) {
			int value = data & 0xFF;
			textBytes[textIndex++] = (byte) (charCodeA + value % 16);
			textBytes[textIndex++] = (byte) (charCodeA + value / 16);

			textCounter++;
			if (useLineBreaks && textCounter == 50) {
				textCounter = 0;
				textBytes[textIndex++] = charCodeSR;
				textBytes[textIndex++] = charCodeSN;
			}
		}

		return textBytes;
	}
}
