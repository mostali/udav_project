package mpc.exception;

public class EmptyRuntimeException extends RuntimeException {
	public EmptyRuntimeException() {
		super();
	}

	public EmptyRuntimeException(String message) {
		super(message);
	}

	public EmptyRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public EmptyRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public EmptyRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
