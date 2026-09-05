package mpc.exception;

public class RecallRuntimeException extends RuntimeException {
	public RecallRuntimeException() {
		super();
	}

	public RecallRuntimeException(String message) {
		super(message);
	}

	public RecallRuntimeException(String message, Object... args) {
		super(String.format(message, args));
	}

	public RecallRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public RecallRuntimeException(Throwable throwable, String message, Object... args) {
		super(String.format(message, args), throwable);
	}
}
