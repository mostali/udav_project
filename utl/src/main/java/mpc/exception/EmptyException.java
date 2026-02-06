package mpc.exception;

public class EmptyException extends Exception {
	public EmptyException() {
		super();
	}

	public EmptyException(String message) {
		super(message);
	}

	public EmptyException(String message, Object... args) {
		super(String.format(message, args));
	}

	public EmptyException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public EmptyException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
