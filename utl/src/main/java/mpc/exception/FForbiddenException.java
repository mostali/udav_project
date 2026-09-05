package mpc.exception;

public class FForbiddenException extends IllegalArgumentException {
	public FForbiddenException() {
		super();
	}

	public FForbiddenException(String message) {
		super(message);
	}

	public FForbiddenException(String message, Object... args) {
		this(String.format(message, args));
	}

	public FForbiddenException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public FForbiddenException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
