package mpc.exception;

public class FNullPointerException extends NullPointerException {
	public FNullPointerException() {
		super();
	}

	public FNullPointerException(String message) {
		super(message);
	}

	public FNullPointerException(String message, Object... args) {
		this(String.format(message, args));
	}

}
