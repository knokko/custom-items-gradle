package nl.knokko.customitems.trouble;

public class UnknownEncodingException extends Exception {

	private static final long serialVersionUID = 7865246839960686227L;
	
	public final String domain;
	public final int encoding;
	
	public UnknownEncodingException(String domain, int encoding) {
		this.domain = domain;
		this.encoding = encoding;
	}
}
