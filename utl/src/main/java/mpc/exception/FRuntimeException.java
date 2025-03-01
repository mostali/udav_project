package mpc.exception;

public class FRuntimeException extends RuntimeException {
	public FRuntimeException() {
		super();
	}

	public FRuntimeException(String message) {
		super(message);
	}

	public FRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public FRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public FRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
