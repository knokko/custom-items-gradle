package nl.knokko.customitems.trouble;

public class IntegrityException extends Exception {

	private static final long serialVersionUID = -7165904546153507584L;
	
	public IntegrityException(long expected, long actual) {
		super("Expected hash " + expected + ", but got " + actual);
	}
	
	public IntegrityException(Throwable cause) {
		super("Failed to read remaining content", cause);
	}
}
