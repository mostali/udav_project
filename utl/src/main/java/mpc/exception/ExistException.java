package mpc.exception;

public class ExistException extends Exception {
	public ExistException() {
		super();
	}

	public ExistException(String message) {
		super(message);
	}

	public ExistException(String message, Object... args) {
		super(String.format(message, args));
	}

	public ExistException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public ExistException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
