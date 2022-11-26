package nl.knokko.customitems.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Logger extends PrintStream {
	
	private final PrintStream oldOut;

	public Logger(File file, PrintStream oldOut) throws FileNotFoundException {
		super(file);
		this.oldOut = oldOut;
	}
	
	@Override
	public void write(int b) {
		super.write(b);
		oldOut.write(b);
	}
	
	@Override
	public void write(byte[] bytes, int offset, int length) {
		super.write(bytes, offset, length);
		oldOut.write(bytes, offset, length);
	}
}