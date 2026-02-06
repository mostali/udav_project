package mpc.exception;

public class SendException extends RuntimeException {
	public SendException() {
		super();
	}

	public SendException(String message) {
		super(message);
	}

	public SendException(String message, Object... args) {
		this(String.format(message, args));
	}

	public SendException(Throwable throwable) {
		super(throwable);
	}

	public SendException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public SendException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
