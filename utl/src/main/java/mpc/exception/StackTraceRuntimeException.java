package mpc.exception;

public class StackTraceRuntimeException extends RuntimeException {
	public StackTraceRuntimeException() {
		super();
	}

	public StackTraceRuntimeException(String message) {
		super(message);
	}

	public StackTraceRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public StackTraceRuntimeException(String message, Object... args) {
		this(String.format(message, args));
	}

	public StackTraceRuntimeException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public StackTraceRuntimeException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}

}
