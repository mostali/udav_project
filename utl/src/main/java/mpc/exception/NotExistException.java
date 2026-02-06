package mpc.exception;

public class NotExistException extends RuntimeException {
	public NotExistException() {
		super();
	}

	public NotExistException(String message) {
		super(message);
	}

	public NotExistException(String message, Object... args) {
		super(String.format(message, args));
	}

	public NotExistException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public NotExistException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
