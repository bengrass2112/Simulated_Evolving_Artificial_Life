package exceptions;

/** An exception indicating a syntax error. */
public class SyntaxError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5108418848124464988L;

	public SyntaxError(String message) {
        super(message);
    }
}
