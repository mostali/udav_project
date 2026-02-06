package mpc.exception;

public class ExistRuntimeException extends RuntimeException {
	public ExistRuntimeException() {
		super();
	}

	public ExistRuntimeException(String message) {
		super(message);
	}

	public ExistRuntimeException(String message, Object... args) {
		super(String.format(message, args));
	}

	public ExistRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public ExistRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
