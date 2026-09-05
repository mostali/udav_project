package mpc.exception;

public class FIllegalArgumentException extends IllegalArgumentException {
	public FIllegalArgumentException() {
		super();
	}

	public FIllegalArgumentException(String message) {
		super(message);
	}

	public FIllegalArgumentException(String message, Object... args) {
		this(String.format(message, args));
	}

	public FIllegalArgumentException(Throwable throwable, String message) {
		super(message, throwable);
	}

	public FIllegalArgumentException(Throwable throwable, String message, Object... args) {
		this(throwable, String.format(message, args));
	}
}
