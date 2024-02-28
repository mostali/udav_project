package mpc.exception;

public class FMessageRuntimeException extends RuntimeException {
	public FMessageRuntimeException() {
		super();
	}

	public FMessageRuntimeException(String message) {
		super(message);
	}

	public FMessageRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public FMessageRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public FMessageRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
