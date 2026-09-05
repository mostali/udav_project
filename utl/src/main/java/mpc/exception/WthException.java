package mpc.exception;

/**
 * What is happens?
 */
public class WthException extends IllegalArgumentException {
	public WthException() {
		super();
	}

	public WthException(Enum type) {
		this(type.name());
	}

	public WthException(String message) {
		super(message);
	}

	public WthException(String message, Object... args) {
		this(String.format(message, args));
	}

	public WthException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public WthException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
