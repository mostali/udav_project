package mpc.exception;

public class TimeoutRuntimeException extends IllegalStateException {
	public TimeoutRuntimeException() {
		super();
	}

	public TimeoutRuntimeException(String message) {
		super(message);
	}

	public TimeoutRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public TimeoutRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public TimeoutRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public TimeoutRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
