package mpc.exception;

public class FIllegalStateException extends IllegalStateException {
	public FIllegalStateException() {
		super();
	}

	public FIllegalStateException(String message) {
		super(message);
	}

	public FIllegalStateException(String message, Object... args) {
		this(String.format(message, args));
	}

	public FIllegalStateException(Throwable throwable) {
		super(throwable);
	}

	public FIllegalStateException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public FIllegalStateException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
