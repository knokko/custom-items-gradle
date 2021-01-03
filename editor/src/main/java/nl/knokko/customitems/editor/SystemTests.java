package nl.knokko.customitems.editor;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class SystemTests {
	
	private static final byte[] DUMMY_BYTES = {87, -100, 37, 91, 4, 65};
	private static final String DUMMY_STRING = "Dummy String";

	public static SystemTestResult performTests() {
		File folder = Editor.getFolder();
		if (!folder.exists() && !folder.mkdirs()) {
			return SystemTestResult.CANT_CREATE_DIRECTORIES;
		}
		
		File dummyBinFile = new File(folder + "/systemTestFile.bin");
		try {
			OutputStream output = Files.newOutputStream(dummyBinFile.toPath());
			output.write(DUMMY_BYTES);
			output.flush();
			output.close();
		} catch (IOException io) {
			return SystemTestResult.CANT_CREATE_BINARY_FILE;
		}
		
		byte[] dummyBytes = new byte[DUMMY_BYTES.length];
		try {
			InputStream input = Files.newInputStream(dummyBinFile.toPath());
			DataInputStream dataInput = new DataInputStream(input);
			dataInput.readFully(dummyBytes);
			input.close();
		} catch (IOException io) {
			return SystemTestResult.CANT_READ_BINARY_FILE;
		}
		
		if (!Arrays.equals(dummyBytes, DUMMY_BYTES)) {
			return SystemTestResult.INCORRECT_BINARY_FILE;
		}
		
		if (!dummyBinFile.delete()) {
			return SystemTestResult.CANT_DELETE_BINARY_FILE;
		}
		
		File dummyTextFile = new File(folder + "/systemTestFile.txt");
		try {
			PrintWriter output = new PrintWriter(dummyTextFile);
			output.println(DUMMY_STRING);
			output.flush();
			output.close();
		} catch (IOException io) {
			return SystemTestResult.CANT_CREATE_TEXT_FILE;
		}
		
		String dummyString;
		try {
			Scanner input = new Scanner(dummyTextFile);
			dummyString = input.nextLine();
			input.close();
		} catch (IOException io) {
			return SystemTestResult.CANT_READ_TEXT_FILE;
		}
		
		if (!dummyString.equals(DUMMY_STRING)) {
			return SystemTestResult.INCORRECT_TEXT_FILE;
		}
		
		if (!dummyTextFile.delete()) {
			return SystemTestResult.CANT_DELETE_TEXT_FILE;
		}
		
		return SystemTestResult.SUCCESS;
	}
	
	public static enum SystemTestResult {
		
		SUCCESS,
		CANT_CREATE_DIRECTORIES,
		CANT_CREATE_BINARY_FILE,
		CANT_READ_BINARY_FILE,
		INCORRECT_BINARY_FILE,
		CANT_DELETE_BINARY_FILE,
		CANT_CREATE_TEXT_FILE,
		CANT_READ_TEXT_FILE,
		INCORRECT_TEXT_FILE,
		CANT_DELETE_TEXT_FILE
	}
}
