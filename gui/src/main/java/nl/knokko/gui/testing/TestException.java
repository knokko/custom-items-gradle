package nl.knokko.gui.testing;

public class TestException extends RuntimeException {

	private static final long serialVersionUID = 7607942209216101992L;
	
	public TestException(String message) {
		super(message);
	}
	
	public TestException(String message, Throwable caught) {
		super(message, caught);
	}
}